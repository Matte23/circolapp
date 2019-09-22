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

package net.underdesk.circolapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.underdesk.circolapp.data.AppDatabase
import net.underdesk.circolapp.data.Circular
import net.underdesk.circolapp.server.DataFetcher
import java.io.IOException

class CircularLetterViewModel(application: Application) : AndroidViewModel(application) {
    init {
        object : Thread() {
            override fun run() {
                updateCirculars()
            }
        }.start()
    }

    val circulars: LiveData<List<Circular>> =
        AppDatabase.getInstance(getApplication()).circularDao().getLiveCirculars()

    val showMessage = MutableLiveData<Boolean>().apply { value = false }

    private fun updateCirculars() {
        val fetcher = DataFetcher()

        try {
            val newCirculars = fetcher.getCircularsFromServer()
            if (newCirculars.size != circulars.value?.size ?: true) {
                AppDatabase.getInstance(getApplication()).circularDao().deleteAll()
                AppDatabase.getInstance(getApplication()).circularDao().insertAll(newCirculars)
            }
        } catch (exception: IOException) {
            showMessage.postValue(true)
        }
    }
}