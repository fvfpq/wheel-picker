package com.example.wheelpicker.data

import com.example.wheelpicker.data.model.DEFAULT_PASSWORD
import com.example.wheelpicker.data.model.SpinRecord
import com.example.wheelpicker.data.model.WheelConfig
import com.example.wheelpicker.data.model.WheelOption
import com.example.wheelpicker.data.model.defaultOptions
import com.example.wheelpicker.data.model.nextAutoColor
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WheelModelsTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `default config has 6 options and default password`() {
        val config = WheelConfig()
        assertEquals(6, config.options.size)
        assertEquals(DEFAULT_PASSWORD, config.password)
        assertTrue(config.options.all { it.label.isNotBlank() })
    }

    @Test
    fun `weight normalization clamps to range`() {
        val option = WheelOption(label = "a", color = 0xFF000000, weight = 500).normalized()
        assertEquals(100, option.weight)

        val optionLow = WheelOption(label = "a", color = 0xFF000000, weight = 0).normalized()
        assertEquals(1, optionLow.weight)

        val optionOk = WheelOption(label = "a", color = 0xFF000000, weight = 50).normalized()
        assertEquals(50, optionOk.weight)
    }

    @Test
    fun `label is trimmed on normalization`() {
        val option = WheelOption(label = "  hello  ", color = 0xFF000000).normalized()
        assertEquals("hello", option.label)
    }

    @Test
    fun `config json round trip`() {
        val original = WheelConfig(
            options = listOf(
                WheelOption(label = "甲", color = 0xFF112233, weight = 20),
                WheelOption(label = "乙", color = 0xFF445566, weight = 30),
            ),
            password = "1234",
            forcedOptionId = "some-id",
        )
        val encoded = json.encodeToString(WheelConfig.serializer(), original)
        val decoded = json.decodeFromString(WheelConfig.serializer(), encoded)
        assertEquals(original, decoded)
    }

    @Test
    fun `json with missing fields falls back to defaults`() {
        val decoded = json.decodeFromString(WheelConfig.serializer(), """{"options":[]}""")
        assertEquals(0, decoded.options.size)
        assertEquals(DEFAULT_PASSWORD, decoded.password)
        assertEquals(null, decoded.forcedOptionId)
    }

    @Test
    fun `auto color avoids duplicates`() {
        val options = defaultOptions()
        val next = nextAutoColor(options)
        assertTrue(options.none { it.color == next })
    }

    @Test
    fun `spin record serialization`() {
        val record = SpinRecord(optionId = "id", label = "win", timestamp = 123456L)
        val encoded = json.encodeToString(SpinRecord.serializer(), record)
        val decoded = json.decodeFromString(SpinRecord.serializer(), encoded)
        assertEquals(record, decoded)
    }

    @Test
    fun `option ids are unique by default`() {
        val a = WheelOption(label = "a", color = 0xFF000000)
        val b = WheelOption(label = "b", color = 0xFF000000)
        assertNotEquals(a.id, b.id)
    }
}
