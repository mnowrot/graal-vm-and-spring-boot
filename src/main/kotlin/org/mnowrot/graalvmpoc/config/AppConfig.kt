package org.mnowrot.graalvmpoc.config

import org.mnowrot.graalvmpoc.infra.native.RuntimeHints
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints

@Configuration
@ImportRuntimeHints(RuntimeHints::class)
@ConfigurationProperties(prefix = "config")
class AppConfig {
    lateinit var message: String
}