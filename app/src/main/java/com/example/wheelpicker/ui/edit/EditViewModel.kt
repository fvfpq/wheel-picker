package com.example.wheelpicker.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wheelpicker.data.OptionRepository
import com.example.wheelpicker.data.model.WheelOption
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EditViewModel(private val repository: OptionRepository) : ViewModel() {

    val options: StateFlow<List<WheelOption>> = repository.config
        .map { it.options }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun save(options: List<WheelOption>) {
        viewModelScope.launch {
            repository.updateOptions(options)
        }
    }
}
