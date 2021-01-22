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

package net.underdesk.circolapp.fragments.intro

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.github.appintro.SlidePolicy
import net.underdesk.circolapp.R
import net.underdesk.circolapp.databinding.FragmentLegalBinding

class LegalFragment : Fragment(), SlidePolicy {

    private var _binding: FragmentLegalBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLegalBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.legalText.text = HtmlCompat.fromHtml(
            getString(R.string.activity_intro_legal_text),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        binding.legalText.movementMethod = LinkMovementMethod.getInstance()

        binding.legalCheckbox.setOnCheckedChangeListener { checkBox, checked ->
            if (checked)
                checkBox.error = null
        }
    }

    override val isPolicyRespected: Boolean
        get() = binding.legalCheckbox.isChecked

    override fun onUserIllegallyRequestedNextPage() {
        binding.legalCheckbox.error =
            getString(R.string.activity_intro_legal_error)
    }

    companion object {
        fun newInstance(): LegalFragment {
            return LegalFragment()
        }
    }
}
