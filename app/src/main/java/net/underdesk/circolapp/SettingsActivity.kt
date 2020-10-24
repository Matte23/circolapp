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

import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*
import kotlinx.android.synthetic.main.settings_activity.*
import net.underdesk.circolapp.server.ServerAPI
import net.underdesk.circolapp.works.PollWork

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        setSupportActionBar(settings_toolbar)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val schoolPreference = findPreference<ListPreference>("school")
            schoolPreference?.let { setSchoolListPreference(it) }
            val schoolPreferenceListener =
                Preference.OnPreferenceChangeListener { _, value ->
                    ServerAPI.changeServer(value.toString().toInt())
                    true
                }
            schoolPreference?.onPreferenceChangeListener = schoolPreferenceListener

            val darkThemePreference = findPreference<Preference>("dark_theme")
            val themePreferenceListener =
                Preference.OnPreferenceChangeListener { _, value ->
                    when (value) {
                        "auto" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        "enabled" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        "disabled" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    true
                }
            darkThemePreference?.onPreferenceChangeListener = themePreferenceListener

            val pollIntervalPreference = findPreference<EditTextPreference>("poll_interval")
            pollIntervalPreference?.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            val notificationPreference =
                findPreference<SwitchPreferenceCompat>("notify_new_circulars")

            val notificationPrefChangedListener =
                Preference.OnPreferenceChangeListener { _, _ ->
                    activity?.let { PollWork.enqueue(it) }
                    true
                }
            pollIntervalPreference?.onPreferenceChangeListener = notificationPrefChangedListener
            notificationPreference?.onPreferenceChangeListener = notificationPrefChangedListener
        }

        private fun setSchoolListPreference(listPreference: ListPreference) {
            val servers = ServerAPI.Companion.Servers.values()
            val entryValues = arrayListOf<CharSequence>()
            val entryNames = arrayListOf<CharSequence>()

            for (i in servers.indices) {
                entryValues.add(i.toString())
                entryNames.add(ServerAPI.getServerName(servers[i]))
            }

            listPreference.setDefaultValue("0")
            listPreference.entryValues = entryValues.toTypedArray()
            listPreference.entries = entryNames.toTypedArray()
        }
    }
}
