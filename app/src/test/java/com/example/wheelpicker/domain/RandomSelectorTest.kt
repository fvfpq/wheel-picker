package com.example.wheelpicker.domain

import com.example.wheelpicker.data.model.WheelOption
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class RandomSelectorTest {

    private fun option(weight: Int, label: String = "x"): WheelOption =
        WheelOption(label = label, color = 0xFF000000, weight = weight)

    @Test
    fun `equal weights produce uniform distribution`() {
        val options = listOf(option(10, "a"), option(10, "b"), option(10, "c"))
        val selector = RandomSelector(Random(42))
        val samples = 60000
        val counts = Array(3) { 0 }
        repeat(samples) {
            val index = options.indexOf(selector.select(options))
            counts[index]++
        }
        val expected = samples / 3.0
        counts.forEach { c ->
            val deviation = Math.abs(c - expected) / expected
            assert(deviation < 0.02) { "uniform distribution deviated: $deviation" }
        }
    }

    @Test
    fun `weighted distribution approximates weight ratio`() {
        val options = listOf(option(1, "a"), option(3, "b"), option(6, "c"))
        val selector = RandomSelector(Random(7))
        val samples = 100000
        val counts = Array(3) { 0 }
        repeat(samples) {
            val index = options.indexOf(selector.select(options))
            counts[index]++
        }
        val total = 10.0
        options.forEachIndexed { index, opt ->
            val ratio = opt.weight / total
            val observed = counts[index].toDouble() / samples
            val deviation = Math.abs(observed - ratio) / ratio
            assert(deviation < 0.02) { "weighted distribution deviated for $index: $deviation" }
        }
    }

    @Test
    fun `single option always selected`() {
        val options = listOf(option(10))
        val selector = RandomSelector(Random(1))
        repeat(100) {
            assertEquals(options[0], selector.select(options))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `empty options throws`() {
        RandomSelector().select(emptyList())
    }
}
