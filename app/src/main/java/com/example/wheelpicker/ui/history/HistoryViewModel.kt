package com.example.wheelpicker.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wheelpicker.data.OptionRepository
import com.example.wheelpicker.data.model.SpinRecord
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: OptionRepository) : ViewModel() {

    val history: StateFlow<List<SpinRecord>> = repository.history
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun clear() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
