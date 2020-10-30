package net.underdesk.circolapp.fragments.intro

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.github.appintro.SlidePolicy
import kotlinx.android.synthetic.main.fragment_legal.view.*
import net.underdesk.circolapp.R

class LegalFragment : Fragment(), SlidePolicy {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_legal, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.legal_text.text = HtmlCompat.fromHtml(
            getString(R.string.activity_intro_legal_text),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
        view.legal_text.movementMethod = LinkMovementMethod.getInstance()

        view.legal_checkbox.setOnCheckedChangeListener { checkBox, checked ->
            if (checked)
                checkBox.error = null
        }
    }

    override val isPolicyRespected: Boolean
        get() = view?.legal_checkbox?.isChecked ?: false

    override fun onUserIllegallyRequestedNextPage() {
        view?.legal_checkbox?.error =
            getString(R.string.activity_intro_legal_error)
    }

    companion object {
        fun newInstance(): LegalFragment {
            return LegalFragment()
        }
    }
}
