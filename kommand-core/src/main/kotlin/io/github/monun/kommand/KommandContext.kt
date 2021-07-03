package io.github.monun.kommand

interface KommandContext {
    val input: String
    val source: KommandSource
}