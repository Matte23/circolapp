package net.underdesk.circolapp

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.AppIntroPageTransformerType
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
                backgroundColor = ContextCompat.getColor(this, R.color.colorPrimary)
            )
        )
        addSlide(SchoolSelectionFragment.newInstance())
        addSlide(
            AppIntroFragment.newInstance(
                title = getString(R.string.activity_intro_last_slide_title),
                description = getString(R.string.activity_intro_last_slide_description),
                backgroundColor = ContextCompat.getColor(this, R.color.colorPrimary)
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
        finish()
    }
}
