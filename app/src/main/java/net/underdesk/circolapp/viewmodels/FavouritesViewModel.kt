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

package net.underdesk.circolapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import net.underdesk.circolapp.data.AppDatabase
import net.underdesk.circolapp.data.Circular

class FavouritesViewModel(application: Application) : AndroidViewModel(application) {
    val query = MutableLiveData<String>("")
    val circulars: LiveData<List<Circular>> = Transformations.switchMap(query) { input ->
        if (input == null || input == "") {
            AppDatabase.getInstance(getApplication()).circularDao().getFavourites()
        } else {
            AppDatabase.getInstance(getApplication()).circularDao().searchFavourites("%$input%")
        }
    }
}