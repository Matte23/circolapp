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

package net.underdesk.circolapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CircularDao {
    @Query("SELECT * FROM circulars ORDER BY id DESC")
    fun getCirculars(): List<Circular>

    @Query("SELECT * FROM circulars ORDER BY id DESC")
    fun getLiveCirculars(): LiveData<List<Circular>>

    @Query("SELECT * FROM circulars WHERE name LIKE :query ORDER BY id DESC")
    fun searchCirculars(query: String): LiveData<List<Circular>>

    @Query("SELECT * FROM circulars WHERE id = :id ORDER BY id DESC")
    fun getCircular(id: Long): Circular

    @Query("SELECT * FROM circulars WHERE favourite ORDER BY id DESC")
    fun getFavourites(): LiveData<List<Circular>>

    @Query("SELECT * FROM circulars WHERE favourite AND name LIKE :query ORDER BY id DESC")
    fun searchFavourites(query: String): LiveData<List<Circular>>

    @Query("SELECT * FROM circulars WHERE reminder ORDER BY id DESC")
    fun getReminders(): LiveData<List<Circular>>

    @Query("SELECT * FROM circulars WHERE reminder AND name LIKE :query ORDER BY id DESC")
    fun searchReminders(query: String): LiveData<List<Circular>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(circulars: List<Circular>)

    @Update
    fun update(circular: Circular)

    @Query("DELETE FROM circulars")
    fun deleteAll()
}
