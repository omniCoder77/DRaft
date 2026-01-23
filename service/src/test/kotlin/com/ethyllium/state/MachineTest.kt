package com.ethyllium.state

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.reflect.Field

class MachineTest {

    @AfterEach
    fun resetSingleton() {
        val instanceField: Field = Machine::class.java.getDeclaredField("instance")
        instanceField.isAccessible = true
        instanceField.set(null, null)
    }

    @Test
    fun `should create quorum correctly`() {
        Machine.create(listOf("A", "B", "C"))
        assertEquals(2, Machine.getInstance().quorumSize())
    }

    @Test
    fun `should throw exception if created twice`() {
        Machine.create(listOf("A"))

        assertThrows(IllegalStateException::class.java) {
            Machine.create(listOf("B"))
        }
    }

    @Test
    fun `should throw exception if getting instance before creation`() {
        assertThrows(IllegalStateException::class.java) {
            Machine.getInstance()
        }
    }
}