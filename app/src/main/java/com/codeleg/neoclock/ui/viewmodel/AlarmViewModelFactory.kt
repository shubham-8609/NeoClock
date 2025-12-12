package com.codeleg.neoclock.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.codeleg.neoclock.repository.AlarmRepository
import com.codeleg.neoclock.viewmodel.AlarmViewModel

class AlarmViewModelFactory(
    private val repository: AlarmRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlarmViewModel(repository, application) as T
    }
}
