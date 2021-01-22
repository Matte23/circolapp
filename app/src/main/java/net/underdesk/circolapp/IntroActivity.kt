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

package net.underdesk.circolapp

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType
import net.underdesk.circolapp.fragments.intro.LegalFragment
import net.underdesk.circolapp.fragments.intro.SchoolSelectionFragment

class IntroActivity : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isWizardMode = true

        setTransformer(AppIntroPageTransformerType.Depth)

        addSlide(
            AppIntroFragment.newInstance(
                title = getString(R.string.title_intro),
                description = getString(R.string.activity_intro_welcome_description),
                imageDrawable = R.mipmap.ic_launcher,
                backgroundColor = ContextCompat.getColor(this, R.color.colorPrimary),
                titleColor = ContextCompat.getColor(this, R.color.colorOnPrimary),
                descriptionColor = ContextCompat.getColor(this, R.color.colorOnPrimary)
            )
        )
        addSlide(LegalFragment.newInstance())
        addSlide(SchoolSelectionFragment.newInstance())
        addSlide(
            AppIntroFragment.newInstance(
                title = getString(R.string.activity_intro_last_slide_title),
                description = getString(R.string.activity_intro_last_slide_description),
                backgroundColor = ContextCompat.getColor(this, R.color.colorPrimary),
                titleColor = ContextCompat.getColor(this, R.color.colorOnPrimary),
                descriptionColor = ContextCompat.getColor(this, R.color.colorOnPrimary)
            )
        )
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        super.onSkipPressed(currentFragment)
        // Decide what to do when the user clicks on "Skip"
        finish()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Decide what to do when the user clicks on "Done"

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.edit {
            putBoolean("first_start", false)
        }
        finish()
    }
}
