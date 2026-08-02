package com.example.wheelpicker.domain

import com.example.wheelpicker.data.model.WheelOption
import kotlin.random.Random

class SpinEngine(
    private val randomSelector: OptionSelector = RandomSelector(),
    private val forcedSelectorFactory: (String) -> OptionSelector = { ForcedSelector(it) },
    private val random: Random = Random.Default,
) {

    fun computeTargetRotation(
        options: List<WheelOption>,
        forcedOptionId: String?,
        currentRotation: Float,
    ): Float {
        require(options.size >= 2) { "at least 2 options required" }
        require(forcedOptionId == null || options.any { it.id == forcedOptionId }) {
            "forced option must exist in options"
        }

        val selected = if (forcedOptionId != null) {
            forcedSelectorFactory(forcedOptionId).select(options)
        } else {
            randomSelector.select(options)
        }
        val selectedIndex = options.indexOf(selected)
        val center = sectorCenter(options, selectedIndex)

        val currentMod = ((currentRotation % 360f) + 360f) % 360f
        val targetMod = ((360f - (center % 360f)) % 360f + 360f) % 360f
        var delta = targetMod - currentMod
        if (delta < 0f) delta += 360f

        val rotations = 4 + random.nextInt(5)
        return currentRotation + rotations * 360f + delta
    }

    fun finalSectorIndex(options: List<WheelOption>, finalRotation: Float): Int {
        require(options.isNotEmpty()) { "options must not be empty" }
        val pointerAngle = ((-finalRotation % 360f) + 360f) % 360f
        val total = options.sumOf { it.weight.toLong() }.toFloat()
        var start = 0f
        for (index in options.indices) {
            val sweep = options[index].weight / total * 360f
            if (pointerAngle >= start && pointerAngle < start + sweep) return index
            start += sweep
        }
        return options.lastIndex
    }

    fun selectedOption(options: List<WheelOption>, finalRotation: Float): WheelOption =
        options[finalSectorIndex(options, finalRotation)]

    fun sectorStart(options: List<WheelOption>, index: Int): Float {
        require(index in options.indices) { "index out of bounds" }
        val total = options.sumOf { it.weight.toLong() }.toFloat()
        return options.take(index).sumOf { it.weight.toLong() } / total * 360f
    }

    fun sectorAngle(options: List<WheelOption>, index: Int): Float {
        require(index in options.indices) { "index out of bounds" }
        val total = options.sumOf { it.weight.toLong() }.toFloat()
        return options[index].weight / total * 360f
    }

    fun sectorCenter(options: List<WheelOption>, index: Int): Float =
        sectorStart(options, index) + sectorAngle(options, index) / 2f

    fun assertSectorsSumTo360(options: List<WheelOption>): Float =
        options.indices.sumOf { sectorAngle(options, it).toDouble() }.toFloat()
}
