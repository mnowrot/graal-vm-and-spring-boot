package org.mnowrot

import org.mnowrot.infra.native.RuntimeHints
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@SpringBootApplication
@ImportRuntimeHints(RuntimeHints::class)
open class MyApplication {
    @RequestMapping("/")
    fun home(): String {
        return "Hello World!\n"
    }
}

fun main(args: Array<String>) {
    runApplication<MyApplication>(*args)
}