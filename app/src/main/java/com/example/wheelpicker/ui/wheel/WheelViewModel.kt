package com.example.wheelpicker.ui.wheel

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wheelpicker.data.OptionRepository
import com.example.wheelpicker.data.model.SpinRecord
import com.example.wheelpicker.data.model.WheelConfig
import com.example.wheelpicker.domain.BackdoorController
import com.example.wheelpicker.domain.SpinEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class WheelViewModel(private val repository: OptionRepository) : ViewModel() {

    private val backdoor = BackdoorController()
    private val engine = SpinEngine()

    val config: StateFlow<WheelConfig> = repository.config
        .stateIn(viewModelScope, SharingStarted.Eagerly, WheelConfig())

    val history = repository.history
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val rotation = Animatable(0f)

    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning.asStateFlow()

    private val _resultLabel = MutableStateFlow<String?>(null)
    val resultLabel: StateFlow<String?> = _resultLabel.asStateFlow()

    private val _showPasswordPrompt = MutableStateFlow(false)
    val showPasswordPrompt: StateFlow<Boolean> = _showPasswordPrompt.asStateFlow()

    private val _passwordError = MutableStateFlow<String?>(null)
    val passwordError: StateFlow<String?> = _passwordError.asStateFlow()

    fun onTopBarTap() {
        if (backdoor.onTopBarTap()) {
            backdoor.consumeTopBarTaps()
            _passwordError.value = null
            _showPasswordPrompt.value = true
        }
    }

    fun dismissPasswordPrompt() {
        _showPasswordPrompt.value = false
        _passwordError.value = null
    }

    fun reportPasswordError() {
        _passwordError.value = "密码错误"
    }

    fun verifyPassword(input: String): Boolean =
        backdoor.verifyPassword(input, config.value.password)

    private val _spinRequest = MutableStateFlow(0)
    val spinRequest: StateFlow<Int> = _spinRequest.asStateFlow()

    fun requestSpin() {
        if (_isSpinning.value) return
        val cfg = config.value
        if (cfg.options.size < 2) return
        _spinRequest.value += 1
    }

    suspend fun performSpin() {
        if (_isSpinning.value) return
        val cfg = config.value
        if (cfg.options.size < 2) return
        try {
            _isSpinning.value = true
            _resultLabel.value = null
            val target = engine.computeTargetRotation(cfg.options, cfg.forcedOptionId, rotation.value)
            val turns = ((target - rotation.value) / 360f).toInt().coerceAtLeast(4)
            rotation.animateTo(
                targetValue = target,
                animationSpec = tween(
                    durationMillis = 2800 + turns * 180,
                    easing = CubicBezierEasing(0.12f, 1f, 0.35f, 1f),
                ),
            )
            val final = rotation.value
            rotation.animateTo(final - 8f, animationSpec = tween(80))
            rotation.animateTo(final + 4f, animationSpec = tween(80))
            rotation.snapTo(final)
            val selected = engine.selectedOption(cfg.options, rotation.value)
            _resultLabel.value = selected.label
            if (cfg.forcedOptionId != null) {
                repository.setForcedOption(null)
            }
            repository.addRecord(
                SpinRecord(optionId = selected.id, label = selected.label, timestamp = System.currentTimeMillis())
            )
        } catch (e: Throwable) {
            android.util.Log.e("WheelSpin", "spin failed", e)
            _resultLabel.value = "出错了：${e.javaClass.simpleName} ${e.message}"
        } finally {
            _isSpinning.value = false
            _spinRequest.value = 0
        }
    }

    fun clearResult() {
        _resultLabel.value = null
    }
}
