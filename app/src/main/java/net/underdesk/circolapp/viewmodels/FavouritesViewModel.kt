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

package net.underdesk.circolapp.viewmodels

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.*
import androidx.preference.PreferenceManager
import net.underdesk.circolapp.shared.data.Circular
import net.underdesk.circolapp.shared.data.CircularRepository
import net.underdesk.circolapp.utils.DoubleTrigger

class FavouritesViewModel internal constructor(
    private val circularRepository: CircularRepository,
    application: Application
) : AndroidViewModel(application), SharedPreferences.OnSharedPreferenceChangeListener {
    private val preferenceManager = PreferenceManager.getDefaultSharedPreferences(application)

    private val schoolID =
        MutableLiveData(preferenceManager.getString("school", "0")?.toInt() ?: 0)

    init {
        preferenceManager.registerOnSharedPreferenceChangeListener(this)
    }

    val query = MutableLiveData("")
    val circulars: LiveData<List<Circular>> =
        Transformations.switchMap(DoubleTrigger(query, schoolID)) { input ->
            if (input.first == null || input.first == "") {
                circularRepository.circularDao.getFavourites(input.second ?: 0).asLiveData()
            } else {
                circularRepository.circularDao.searchFavourites(
                    "%${input.first}%",
                    input.second ?: 0
                ).asLiveData()
            }
        }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "school")
            schoolID.postValue(preferenceManager.getString("school", "0")?.toInt() ?: 0)
    }
}
