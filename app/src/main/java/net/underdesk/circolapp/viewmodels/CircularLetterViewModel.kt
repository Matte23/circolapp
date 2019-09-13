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

class CircularLetterViewModel(application: Application) : AndroidViewModel(application) {

    private val _circulars: MutableLiveData<List<Circular>> by lazy {
        MutableLiveData<List<Circular>>().also {
            loadCirculars()
        }
    }

    val circulars: LiveData<List<Circular>> = _circulars

    private fun loadCirculars() {
        object : Thread() {
            override fun run() {
                _circulars.postValue(AppDatabase.getInstance(getApplication()).circularDao().getCirculars())
                updateCirculars()
            }
        }.start()
    }

    private fun updateCirculars() {
        val fetcher = DataFetcher()

        val newCirculars = fetcher.getCircularsFromServer()
        if (newCirculars.size != _circulars.value?.size ?: true) {
            _circulars.postValue(newCirculars)
            AppDatabase.getInstance(getApplication()).circularDao().deleteAll()
            AppDatabase.getInstance(getApplication()).circularDao().insertAll(newCirculars)
        }
    }
}