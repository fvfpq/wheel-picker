package com.example.wheelpicker.domain

class BackdoorController(
    private val now: () -> Long = { System.currentTimeMillis() },
    private val tapWindowMs: Long = 1500,
    private val requiredTaps: Int = 5,
) {

    private var tapCount = 0
    private var lastTapTime = 0L

    fun onTopBarTap(): Boolean {
        val current = now()
        if (current - lastTapTime > tapWindowMs) {
            tapCount = 0
        }
        lastTapTime = current
        tapCount++
        return tapCount >= requiredTaps
    }

    fun consumeTopBarTaps() {
        tapCount = 0
    }

    fun verifyPassword(input: String, storedPassword: String): Boolean =
        input == storedPassword
}
