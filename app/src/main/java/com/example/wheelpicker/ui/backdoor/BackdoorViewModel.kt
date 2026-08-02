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

    fun forceNext(optionId: String?) {
        viewModelScope.launch {
            repository.setForcedOption(optionId)
        }
    }

    fun updatePassword(password: String) {
        viewModelScope.launch {
            repository.updatePassword(password)
        }
    }
}
