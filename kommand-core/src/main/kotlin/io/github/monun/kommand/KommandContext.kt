package io.github.monun.kommand

import kotlin.reflect.KProperty

// 컨텍스트
interface KommandContext {
    val source: KommandSource
    val input: String

    operator fun <T> get(name: String): T
}

operator fun <T> KommandContext.getValue(
    name: String?,
    property: KProperty<*>
): T {
    requireNotNull(name)
    return this[name]
}