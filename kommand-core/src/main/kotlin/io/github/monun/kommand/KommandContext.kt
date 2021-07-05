package io.github.monun.kommand

import kotlin.reflect.KProperty

interface KommandContext {
    val input: String
    val source: KommandSource

    operator fun <T> get(name: String?): T
}

operator fun <T> KommandContext.getValue(name: String?, property: KProperty<*>): T = this[name]
