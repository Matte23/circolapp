package net.underdesk.circolapp.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.underdesk.circolapp.data.CircularRepository

class RemindersViewModelFactory(
    private val circularRepository: CircularRepository,
    val application: Application
) : ViewModelProvider.AndroidViewModelFactory(application) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RemindersViewModel(circularRepository, application) as T
    }
}
