package net.underdesk.circolapp.fragments.intro

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.github.appintro.SlidePolicy
import com.tiper.MaterialSpinner
import kotlinx.android.synthetic.main.fragment_school_selection.view.*
import net.underdesk.circolapp.R
import net.underdesk.circolapp.server.ServerAPI

class SchoolSelectionFragment : Fragment(), SlidePolicy, MaterialSpinner.OnItemSelectedListener {
    private lateinit var preferenceManager: SharedPreferences
    private var schoolSelected = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_school_selection, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val items = getSchoolListArray()
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)

        view.school_selection_spinner.adapter = adapter
        view.school_selection_spinner.onItemSelectedListener = this
    }

    private fun getSchoolListArray(): ArrayList<String> {
        val servers = ServerAPI.Companion.Servers.values()
        val entryNames = arrayListOf<String>()

        for (i in servers.indices) {
            entryNames.add(ServerAPI.getServerName(servers[i]))
        }

        return entryNames
    }

    override fun onItemSelected(parent: MaterialSpinner, view: View?, position: Int, id: Long) {
        val editor = preferenceManager.edit()
        editor.putString("school", position.toString())
        editor.apply()

        ServerAPI.changeServer(position, requireContext())
        schoolSelected = true

        parent.error = null
    }

    override fun onNothingSelected(parent: MaterialSpinner) {
        // Intentionally empty
    }

    override val isPolicyRespected: Boolean
        get() = schoolSelected

    override fun onUserIllegallyRequestedNextPage() {
        view?.school_selection_spinner?.error =
            getString(R.string.activity_intro_school_selection_error)
    }

    companion object {
        fun newInstance(): SchoolSelectionFragment {
            return SchoolSelectionFragment()
        }
    }
}
