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

package net.underdesk.circolapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import net.underdesk.circolapp.MainActivity
import net.underdesk.circolapp.R
import net.underdesk.circolapp.adapters.CircularLetterAdapter
import net.underdesk.circolapp.data.AndroidCircularRepository
import net.underdesk.circolapp.databinding.FragmentCircularLettersBinding
import net.underdesk.circolapp.viewmodels.CircularLetterViewModel
import net.underdesk.circolapp.viewmodels.CircularLetterViewModelFactory

class CircularLetterFragment :
    Fragment(),
    MainActivity.SearchCallback,
    MainActivity.RefreshCallback {

    private var _binding: FragmentCircularLettersBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val circularLetterViewModel: CircularLetterViewModel by viewModels {
        CircularLetterViewModelFactory(
            AndroidCircularRepository.getInstance(requireContext()),
            requireActivity().application
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCircularLettersBinding.inflate(inflater, container, false)

        binding.circularsList.layoutManager = LinearLayoutManager(context)

        circularLetterViewModel.circulars.observe(
            viewLifecycleOwner,
            {
                if (binding.circularsList.adapter == null) {
                    binding.circularsList.adapter =
                        CircularLetterAdapter(it, activity as MainActivity, lifecycleScope)
                } else {
                    (binding.circularsList.adapter as CircularLetterAdapter).changeDataSet(it)
                }
            }
        )
        circularLetterViewModel.showMessage.observe(
            viewLifecycleOwner,
            {
                if (it) activity?.findViewById<ConstraintLayout>(R.id.container)?.let { view ->
                    Snackbar.make(
                        view,
                        getString(R.string.snackbar_connection_not_available),
                        Snackbar.LENGTH_LONG
                    ).show()

                    circularLetterViewModel.showMessage.postValue(false)
                }
            }
        )
        circularLetterViewModel.circularsUpdated.observe(
            viewLifecycleOwner,
            {
                if (it) {
                    binding.circularsRefresh.isRefreshing = false

                    circularLetterViewModel.showMessage.postValue(false)
                }
            }
        )

        binding.circularsRefresh.setOnRefreshListener { circularLetterViewModel.updateCirculars() }

        (activity as MainActivity).searchCallback = this
        (activity as MainActivity).refreshCallback = this
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun search(query: String) {
        circularLetterViewModel.query.postValue(query)
    }

    override fun refresh() {
        binding.circularsRefresh.isRefreshing = true
        circularLetterViewModel.updateCirculars()
    }
}
