package io.github.monun.kommand.node

import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandSource

// 커맨드 노드
interface KommandNode {
    fun requires(requires: (context: KommandSource) -> Boolean)

    fun executes(executes: (context: KommandContext) -> Unit)

    fun then(name: String, init: LiteralNode.() -> Unit)

    fun then(argument: Pair<String, KommandArgument<*>>, init: ArgumentNode.() -> Unit)
}