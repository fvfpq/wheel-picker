package com.example.wheelpicker.domain

import com.example.wheelpicker.data.model.WheelOption
import kotlin.random.Random

fun interface OptionSelector {
    fun select(options: List<WheelOption>): WheelOption
}

class RandomSelector(private val random: Random = Random.Default) : OptionSelector {

    override fun select(options: List<WheelOption>): WheelOption {
        require(options.isNotEmpty()) { "options must not be empty" }
        if (options.size == 1) return options[0]

        val total = options.sumOf { it.weight.toLong() }
        require(total > 0) { "total weight must be positive" }

        var r = random.nextLong(total)
        for (option in options) {
            r -= option.weight
            if (r < 0) return option
        }
        return options.last()
    }
}

class ForcedSelector(private val forcedOptionId: String?) : OptionSelector {

    override fun select(options: List<WheelOption>): WheelOption {
        require(forcedOptionId != null) { "forcedOptionId must not be null" }
        return options.first { it.id == forcedOptionId }
    }
}
