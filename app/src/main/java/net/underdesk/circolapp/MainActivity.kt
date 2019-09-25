/*
 * Circolapp
 * Copyright (C) 2019  Matteo Schiff
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
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
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
            showInfoDialog()

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
                showInfoDialog()
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

    private fun showInfoDialog() {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setTitle(R.string.dialog_info_title)
            setMessage(R.string.dialog_info_content)
            setPositiveButton(
                R.string.dialog_ok
            ) { dialog, _ ->
                dialog.dismiss()
            }
            setNeutralButton(
                R.string.dialog_licenses
            ) { _, _ ->
                startActivity(Intent(this@MainActivity, LicensesActivity::class.java))
            }
        }

        builder.create().show()
    }

    interface SearchCallback {
        fun search(query: String)
    }

    interface RefreshCallback {
        fun refresh()
    }
}
