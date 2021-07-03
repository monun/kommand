package io.github.monun.kommand.internal

class RootKommand(name: String, val aliases: Iterable<String>) : LiteralKommand(name)