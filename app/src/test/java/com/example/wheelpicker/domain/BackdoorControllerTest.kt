package com.example.wheelpicker.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BackdoorControllerTest {

    private var currentTime = 0L

    private fun controller() = BackdoorController(now = { currentTime })

    @Test
    fun `five rapid taps triggers`() {
        val c = controller()
        repeat(4) { assertFalse(c.onTopBarTap()) }
        assertTrue(c.onTopBarTap())
    }

    @Test
    fun `taps slower than window reset counter`() {
        val c = controller()
        currentTime = 0
        repeat(4) { c.onTopBarTap(); currentTime += 100 }
        currentTime += 2000
        assertFalse(c.onTopBarTap())
        currentTime += 100
        repeat(4) { c.onTopBarTap(); currentTime += 100 }
        assertTrue(c.onTopBarTap())
    }

    @Test
    fun `tap count resets after consume`() {
        val c = controller()
        repeat(5) { c.onTopBarTap() }
        c.consumeTopBarTaps()
        currentTime += 1000
        repeat(4) { assertFalse(c.onTopBarTap()) }
    }

    @Test
    fun `verifyPassword matches stored`() {
        val c = controller()
        assertTrue(c.verifyPassword("8888", "8888"))
        assertFalse(c.verifyPassword("0000", "8888"))
        assertFalse(c.verifyPassword("", "8888"))
    }
}
