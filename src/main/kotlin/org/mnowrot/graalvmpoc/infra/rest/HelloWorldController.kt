package org.mnowrot.graalvmpoc.infra.rest

import org.mnowrot.graalvmpoc.config.AppConfig
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController(private val config: AppConfig) {

    @GetMapping("/")
    fun home(): String {
        return "${config.message}\n"
    }
}