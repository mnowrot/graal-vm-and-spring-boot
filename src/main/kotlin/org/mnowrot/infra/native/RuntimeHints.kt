package org.mnowrot.infra.native

import org.apache.coyote.AbstractProtocol
import org.apache.coyote.http11.AbstractHttp11Protocol
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar


class RuntimeHints : RuntimeHintsRegistrar {
    override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
        // Register types for reflection
        hints.reflection().registerType(AbstractProtocol::class.java, MemberCategory.INVOKE_PUBLIC_METHODS)
        hints.reflection().registerType(AbstractHttp11Protocol::class.java, MemberCategory.INVOKE_PUBLIC_METHODS)
    }
}