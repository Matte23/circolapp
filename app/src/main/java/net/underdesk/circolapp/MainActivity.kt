/*
 * Circolapp
 * Copyright (C) 2019-2021  Matteo Schiff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.underdesk.circolapp

import android.app.DownloadManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.aboutlibraries.LibsBuilder
import kotlinx.coroutines.launch
import net.underdesk.circolapp.adapters.CircularLetterAdapter
import net.underdesk.circolapp.data.AndroidDatabase
import net.underdesk.circolapp.databinding.ActivityMainBinding
import net.underdesk.circolapp.utils.CustomTabsHelper
import net.underdesk.circolapp.utils.DownloadableFile
import net.underdesk.circolapp.works.PollWork

class MainActivity : AppCompatActivity(), CircularLetterAdapter.AdapterCallback {

    companion object {
        internal const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 10
    }

    private lateinit var binding: ActivityMainBinding

    var searchCallback: SearchCallback? = null
    var refreshCallback: RefreshCallback? = null
    override var fileToDownload: DownloadableFile? = null

    var customTabsSession: CustomTabsSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        loadDarkTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.mainToolbar)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_circular_letters,
                R.id.navigation_favourites,
                R.id.navigation_reminders
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        PollWork.enqueue(this)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (sharedPreferences.getBoolean("first_start", true)) {
            startIntroActivity()
        }
    }

    override fun onStart() {
        val connection: CustomTabsServiceConnection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(
                name: ComponentName,
                client: CustomTabsClient
            ) {
                client.warmup(0)
                customTabsSession = client.newSession(null)
            }

            override fun onServiceDisconnected(name: ComponentName) {}
        }
        CustomTabsClient.bindCustomTabsService(
            this,
            CustomTabsHelper.getPreferredCustomTabsPackage(this),
            connection
        )
        super.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        (menu.findItem(R.id.menu_main_search).actionView as SearchView).setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String): Boolean {
                    searchCallback?.search(query)
                    return false
                }

                override fun onQueryTextChange(query: String): Boolean {
                    searchCallback?.search(query)
                    return false
                }
            }
        )

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_main_refresh -> {
                refreshCallback?.refresh()
                true
            }
            R.id.menu_main_mark_all_read -> {
                lifecycleScope.launch {
                    AndroidDatabase.getDaoInstance(this@MainActivity).markAllRead(true)
                }
                true
            }
            R.id.menu_main_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.menu_main_about -> {
                startInfoActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    downloadFile()
                } else {
                    Snackbar.make(
                        binding.container,
                        resources.getString(R.string.snackbar_write_permission_not_granted),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    override fun downloadFile() {
        val regexDots = Regex("""\.+$""")
        val safeFilename = fileToDownload!!.name.replace(regexDots, "")

        var extension = fileToDownload!!.url.substringAfterLast(".", "html")
        if (!extension.matches(Regex("""[a-zA-Z]+"""))) extension = "html"

        val request = DownloadManager.Request(Uri.parse(fileToDownload!!.url))
        request.setTitle(fileToDownload!!.name)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "Circolapp/$safeFilename.$extension"
        )

        (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)

        Snackbar.make(
            binding.container,
            resources.getString(R.string.snackbar_circular_downloaded),
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun startInfoActivity() {
        LibsBuilder()
            .withAboutAppName(getString(R.string.app_name))
            .withAboutDescription(getString(R.string.activity_info_content))
            .withActivityTitle(getString(R.string.activity_info_title))
            .withAboutSpecial1(getString(R.string.activity_info_license))
            .withAboutSpecial1Description(getString(R.string.activity_info_license_description))
            .withAboutSpecial2(getString(R.string.activity_info_source_code))
            .withAboutSpecial2Description(getString(R.string.activity_info_source_code_description))
            .withAboutSpecial3(getString(R.string.activity_info_privacy_policy))
            .withAboutSpecial3Description(getString(R.string.activity_info_privacy_policy_description))
            .withLicenseShown(true)
            .start(this@MainActivity)
    }

    private fun startIntroActivity() {
        startActivity(Intent(this, IntroActivity::class.java))
    }

    private fun loadDarkTheme() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        when (sharedPreferences.getString("dark_theme", "auto")) {
            "auto" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "enabled" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "disabled" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    interface SearchCallback {
        fun search(query: String)
    }

    interface RefreshCallback {
        fun refresh()
    }
}
