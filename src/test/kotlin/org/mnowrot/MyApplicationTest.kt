package org.mnowrot

import kotlin.test.Test
import kotlin.test.assertEquals

class MyApplicationTest {
    @Test
    fun `home endpoint returns Hello World`() {
        val app = MyApplication()
        assertEquals("Hello World!\n", app.home())
    }
}
