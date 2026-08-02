package com.example.wheelpicker.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class WheelOption(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val color: Long,
    val weight: Int = 10,
) {
    fun normalized(): WheelOption =
        copy(label = label.trim(), weight = weight.coerceIn(1, 100))
}

@Serializable
data class WheelConfig(
    val options: List<WheelOption> = defaultOptions(),
    val password: String = DEFAULT_PASSWORD,
    val forcedOptionId: String? = null,
    val forcedQueue: List<String> = emptyList(),
) {
    fun normalized(): WheelConfig {
        val validIds = options.map { it.id }.toSet()
        var queue = forcedQueue.filter { it in validIds }
        if (queue.isEmpty() && forcedOptionId != null && forcedOptionId in validIds) {
            queue = listOf(forcedOptionId)
        }
        return copy(options = options.map { it.normalized() }, forcedQueue = queue)
    }
}

@Serializable
data class SpinRecord(
    val optionId: String,
    val label: String,
    val timestamp: Long,
)

const val DEFAULT_PASSWORD = "8888"
const val MAX_OPTIONS = 20
const val MIN_OPTIONS = 2

val DEFAULT_PALETTE: List<Long> = listOf(
    0xFFFF5722,
    0xFFFF9800,
    0xFFFFC107,
    0xFF4CAF50,
    0xFF2196F3,
    0xFF3F51B5,
    0xFF9C27B0,
    0xFFE91E63,
    0xFF009688,
    0xFF795548,
    0xFF607D8B,
    0xFFF44336,
)

fun defaultOptions(): List<WheelOption> {
    val labels = listOf("选项一", "选项二", "选项三", "选项四", "选项五", "选项六")
    return labels.mapIndexed { index, label ->
        WheelOption(label = label, color = DEFAULT_PALETTE[index % DEFAULT_PALETTE.size])
    }
}

fun nextAutoColor(options: List<WheelOption>): Long {
    val used = options.map { it.color }.toSet()
    return DEFAULT_PALETTE.firstOrNull { it !in used } ?: DEFAULT_PALETTE[options.size % DEFAULT_PALETTE.size]
}
