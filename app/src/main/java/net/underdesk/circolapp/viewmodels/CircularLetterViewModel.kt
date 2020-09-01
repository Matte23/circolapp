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
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import net.underdesk.circolapp.data.AppDatabase
import net.underdesk.circolapp.data.Circular
import net.underdesk.circolapp.server.DataFetcher
import java.io.IOException

class CircularLetterViewModel(application: Application) : AndroidViewModel(application) {
    init {
        updateCirculars()
    }

    val query = MutableLiveData<String>("")
    val circulars: LiveData<List<Circular>> = Transformations.switchMap(query) { input ->
        if (input == null || input == "") {
            AppDatabase.getInstance(getApplication()).circularDao().getLiveCirculars()
        } else {
            AppDatabase.getInstance(getApplication()).circularDao().searchCirculars("%$input%")
        }
    }

    val showMessage = MutableLiveData<Boolean>().apply { value = false }
    val circularsUpdated = MutableLiveData<Boolean>().apply { value = false }
    private var isNotUpdating = true

    fun updateCirculars() {
        if (isNotUpdating) {
            viewModelScope.launch {
                isNotUpdating = false
                val fetcher = DataFetcher()

                try {
                    val newCirculars = fetcher.getCircularsFromServer()
                    if (newCirculars.size != circulars.value?.size ?: true) {
                        AppDatabase.getInstance(getApplication()).circularDao()
                            .insertAll(newCirculars)
                    }
                } catch (exception: IOException) {
                    showMessage.postValue(true)
                } finally {
                    isNotUpdating = true
                    circularsUpdated.postValue(true)
                }
            }
        }
    }
}