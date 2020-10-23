package net.underdesk.circolapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.underdesk.circolapp.data.CircularRepository

class RemindersViewModelFactory(
    private val circularRepository: CircularRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RemindersViewModel(circularRepository) as T
    }
}
