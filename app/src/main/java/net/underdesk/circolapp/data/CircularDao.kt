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
    @Query("SELECT * FROM circulars WHERE school is :school ORDER BY id DESC")
    fun getCirculars(school: Int): List<Circular>

    @Query("SELECT * FROM circulars WHERE school is :school ORDER BY id DESC")
    fun getLiveCirculars(school: Int): LiveData<List<Circular>>

    @Query("SELECT * FROM circulars WHERE school is :school AND name LIKE :query ORDER BY id DESC")
    fun searchCirculars(query: String, school: Int): LiveData<List<Circular>>

    @Query("SELECT * FROM circulars WHERE school is :school AND id = :id ORDER BY id DESC")
    fun getCircular(id: Long, school: Int): Circular

    @Query("SELECT * FROM circulars WHERE school is :school AND favourite ORDER BY id DESC")
    fun getFavourites(school: Int): LiveData<List<Circular>>

    @Query("SELECT * FROM circulars WHERE school is :school AND favourite AND name LIKE :query ORDER BY id DESC")
    fun searchFavourites(query: String, school: Int): LiveData<List<Circular>>

    @Query("SELECT * FROM circulars WHERE school is :school AND reminder ORDER BY id DESC")
    fun getReminders(school: Int): LiveData<List<Circular>>

    @Query("SELECT * FROM circulars WHERE school is :school AND reminder AND name LIKE :query ORDER BY id DESC")
    fun searchReminders(query: String, school: Int): LiveData<List<Circular>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(circulars: List<Circular>)

    @Update
    fun update(circular: Circular)

    @Query("DELETE FROM circulars")
    fun deleteAll()
}
