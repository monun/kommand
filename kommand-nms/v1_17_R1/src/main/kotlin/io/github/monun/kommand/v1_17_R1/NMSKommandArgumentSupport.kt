package io.github.monun.kommand.v1_17_R1

import io.github.monun.kommand.KommandArgumentSupport
import io.github.monun.kommand.argument.IntArgument
import io.github.monun.kommand.v1_17_R1.argument.NMSIntArgument

class NMSKommandArgumentSupport: KommandArgumentSupport {
    override fun int(): IntArgument = NMSIntArgument()
}