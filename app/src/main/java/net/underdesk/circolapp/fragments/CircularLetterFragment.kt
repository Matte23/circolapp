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

package net.underdesk.circolapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_circular_letters.view.*
import net.underdesk.circolapp.MainActivity
import net.underdesk.circolapp.R
import net.underdesk.circolapp.adapters.CircularLetterAdapter
import net.underdesk.circolapp.viewmodels.CircularLetterViewModel

class CircularLetterFragment : Fragment() {

    private lateinit var circularLetterViewModel: CircularLetterViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_circular_letters, container, false)

        root.circulars_list.layoutManager = LinearLayoutManager(context)

        circularLetterViewModel =
            ViewModelProviders.of(this).get(CircularLetterViewModel::class.java)
        circularLetterViewModel.circulars.observe(this, Observer {
            root.circulars_list.adapter = CircularLetterAdapter(it, activity as MainActivity)
        })
        circularLetterViewModel.showMessage.observe(this, Observer {
            if (it) activity?.findViewById<ConstraintLayout>(R.id.container)?.let { view ->
                Snackbar.make(
                    view,
                    getString(R.string.snackbar_connection_not_available),
                    Snackbar.LENGTH_LONG
                ).show()

                circularLetterViewModel.showMessage.postValue(false)
            }
        })
        return root
    }
}