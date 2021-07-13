package io.github.monun.kommand.node

import io.github.monun.kommand.*

// 커맨드 노드
@KommandDSL
interface KommandNode: KommandArgumentSupport {
    fun requires(requires: KommandRequirement.(context: KommandSource) -> Boolean)

    fun executes(executes: KommandExecutor.(context: KommandContext) -> Unit)

    fun then(name: String, init: LiteralNode.() -> Unit)

    fun then(argument: Pair<String, KommandArgument<*>>, init: ArgumentNode.() -> Unit)
}

@KommandDSL
object KommandRequirement

@KommandDSL
object KommandExecutor