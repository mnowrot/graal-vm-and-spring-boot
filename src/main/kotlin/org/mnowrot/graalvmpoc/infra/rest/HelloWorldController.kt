package org.mnowrot.graalvmpoc.infra.rest

import io.github.oshai.kotlinlogging.KotlinLogging
import org.mnowrot.graalvmpoc.config.AppConfig
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController(private val config: AppConfig) {

    companion object {
        val log = KotlinLogging.logger {}
    }

    @GetMapping("/")
    fun home(): String {
        log.info { "GET / called. Message to be returned: ${config.message}" }
        return "${config.message}\n"
    }
}