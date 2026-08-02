package com.example.wheelpicker.ui.backdoor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wheelpicker.data.OptionRepository
import com.example.wheelpicker.data.model.WheelConfig
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BackdoorViewModel(private val repository: OptionRepository) : ViewModel() {

    val config: StateFlow<WheelConfig> = repository.config
        .stateIn(viewModelScope, SharingStarted.Eagerly, WheelConfig())

    fun enqueue(optionId: String) {
        viewModelScope.launch {
            repository.enqueueForcedOption(optionId)
        }
    }

    fun removeAt(optionId: String) {
        viewModelScope.launch {
            repository.removeForcedOption(optionId)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            repository.clearForcedOptions()
        }
    }

    fun updatePassword(password: String) {
        viewModelScope.launch {
            repository.updatePassword(password)
        }
    }
}
