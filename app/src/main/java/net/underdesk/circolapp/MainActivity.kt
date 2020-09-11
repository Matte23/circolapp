/*
 * Circolapp
 * Copyright (C) 2019-2020  Matteo Schiff
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
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.aboutlibraries.LibsBuilder
import kotlinx.android.synthetic.main.activity_main.*
import net.underdesk.circolapp.adapters.CircularLetterAdapter
import net.underdesk.circolapp.data.Circular
import net.underdesk.circolapp.works.PollWork

class MainActivity : AppCompatActivity(), CircularLetterAdapter.AdapterCallback {

    companion object {
        internal const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 10
    }

    var searchCallback: SearchCallback? = null
    var refreshCallback: RefreshCallback? = null
    override var circularToDownload: Circular? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        loadDarkTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)
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

        if (getPreferences(Context.MODE_PRIVATE).getBoolean("first_start", true)) {
            startInfoActivity()

            getPreferences(Context.MODE_PRIVATE).edit().apply {
                putBoolean("first_start", false)
                apply()
            }
        }
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
            })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.menu_main_refresh -> {
                refreshCallback?.refresh()
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
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    downloadCircular()
                } else {
                    Snackbar.make(
                        container,
                        resources.getString(R.string.snackbar_write_permission_not_granted),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
    }

    override fun downloadCircular() {
        val request = DownloadManager.Request(Uri.parse(circularToDownload!!.url))
        request.setTitle(circularToDownload!!.name)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "Circolapp/" + circularToDownload!!.id + ".pdf"
        )

        (getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)

        Snackbar.make(
            container,
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
            .withLicenseShown(true)
            .start(this@MainActivity)
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
