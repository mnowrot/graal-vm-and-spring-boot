package org.mnowrot.graalvmpoc.infra.rest

import org.mnowrot.graalvmpoc.config.AppConfig
import kotlin.test.Test
import kotlin.test.assertEquals

private const val message = "Hello World!"

class HelloWorldControllerTest {
    @Test
    fun `home endpoint returns Hello World`() {
        // given
        val config = AppConfig()
        config.message = message

        // when
        val helloWorldController = HelloWorldController(config)

        // then
        assertEquals("$message\n", helloWorldController.home())
    }
}
