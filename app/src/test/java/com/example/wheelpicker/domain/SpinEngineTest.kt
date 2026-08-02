package com.example.wheelpicker.domain

import com.example.wheelpicker.data.model.WheelOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class SpinEngineTest {

    private fun option(weight: Int, label: String = "x"): WheelOption =
        WheelOption(label = label, color = 0xFF000000, weight = weight)

    private val engine = SpinEngine(
        randomSelector = RandomSelector(Random(1)),
        random = Random(2),
    )

    @Test
    fun `sector angles sum to 360`() {
        val options = listOf(option(1), option(3), option(6), option(10))
        assertEquals(360f, engine.assertSectorsSumTo360(options), 0.001f)
    }

    @Test
    fun `sector mapping matches weights`() {
        val options = listOf(option(1, "a"), option(3, "b"), option(6, "c"))
        val startA = engine.sectorStart(options, 0)
        val startB = engine.sectorStart(options, 1)
        val startC = engine.sectorStart(options, 2)
        assertEquals(0f, startA, 0.001f)
        assertEquals(36f, startB, 0.001f)
        assertEquals(144f, startC, 0.001f)
        assertEquals(360f, startC + engine.sectorAngle(options, 2), 0.001f)
    }

    @Test
    fun `forced option lands on its own sector center`() {
        val options = listOf(option(1, "a"), option(1, "b"), option(1, "c"), option(1, "d"))
        options.forEachIndexed { index, opt ->
            val target = engine.computeTargetRotation(options, opt.id, 0f)
            assert(target > 4 * 360f) { "rotation must spin at least 4 turns" }
            assertEquals(index, engine.finalSectorIndex(options, target))
        }
    }

    @Test
    fun `forced option lands on sector center across many runs`() {
        val options = listOf(option(2, "a"), option(5, "b"), option(3, "c"))
        repeat(200) {
            val target = engine.computeTargetRotation(options, options[1].id, 0f)
            assertEquals(1, engine.finalSectorIndex(options, target))
        }
    }

    @Test
    fun `random rotation always maps to a valid sector`() {
        val options = listOf(option(1, "a"), option(7, "b"), option(2, "c"), option(9, "d"))
        repeat(500) {
            val target = engine.computeTargetRotation(options, null, 0f)
            val index = engine.finalSectorIndex(options, target)
            assertTrue(index in options.indices)
            assert(target > 0)
        }
    }

    @Test
    fun `pointer stays on sector center regardless of prior rotation`() {
        val options = listOf(option(2, "a"), option(5, "b"), option(3, "c"))
        var rotation = 0f
        repeat(300) {
            rotation = engine.computeTargetRotation(options, null, rotation)
            val index = engine.finalSectorIndex(options, rotation)
            val center = engine.sectorCenter(options, index)
            val mod = ((rotation % 360f) + 360f) % 360f
            val expected = ((360f - (center % 360f)) % 360f + 360f) % 360f
            assertEquals(expected, mod, 0.001f)
        }
    }

    @Test
    fun `sector center falls inside its own sector`() {
        val options = listOf(option(2, "a"), option(3, "b"), option(5, "c"))
        options.indices.forEach { index ->
            val center = engine.sectorCenter(options, index)
            val start = engine.sectorStart(options, index)
            assertTrue(center >= start && center < start + engine.sectorAngle(options, index))
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rotation with fewer than 2 options throws`() {
        engine.computeTargetRotation(listOf(option(1)), null, 0f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `forced option not in list throws`() {
        engine.computeTargetRotation(listOf(option(1), option(1)), "missing-id", 0f)
    }
}
