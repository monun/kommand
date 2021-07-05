package io.github.monun.kommand

// 커맨드 노드
interface KommandNode {
    fun requires(requires: (context: KommandContext) -> Boolean)

    fun executes(executes: (context: KommandContext) -> Unit)

    fun then(name: String, init: LiteralNode.() -> Unit)

    fun then(argument: Pair<String, KommandArgument>, init: ArgumentNode.() -> Unit)
}