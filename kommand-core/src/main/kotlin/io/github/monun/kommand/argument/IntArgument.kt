package io.github.monun.kommand.argument

import io.github.monun.kommand.internal.AbstractKommandArgument

class IntArgument(
    val minimum: Int = Int.MIN_VALUE,
    val maximum: Int = Int.MAX_VALUE
): AbstractKommandArgument()