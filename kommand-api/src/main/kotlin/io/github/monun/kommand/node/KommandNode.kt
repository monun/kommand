/*
 * Kommand
 * Copyright (C) 2021 Monun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.monun.kommand.node

import io.github.monun.kommand.*

// 커맨드 노드
@KommandDSL
interface KommandNode : KommandArgumentSupport {

    /**
     * 명령 실행에 필요한 권한을 체크합니다.
     *
     * 이 함수는 개별 명령에만 적용되며 하위 노드에 영향을 주지 않습니다.
     *
     * ```kotlin
     * kommand {
     *   register("mycmd") {
     *     then("first") { // mycmd first
     *       requires { hasPermission(4) }
     *       executes {
     *       // 4 레벨 권한이 있어야 실행됨
     *       }
     *       then("second") { // /mycmd first second
     *         executes {
     *         // 권한 필요 없음
     *       }
     *     }
     *   }
     * }
     * ```
     *
     * @see RootNode.requires
     */
    fun requires(requires: KommandSource.() -> Boolean)

    fun executes(executes: KommandSource.(KommandContext) -> Unit)

    fun then(
        argument: Pair<String, KommandArgument<*>>,
        vararg arguments: Pair<String, KommandArgument<*>>,
        init: KommandNode.() -> Unit
    )

    fun then(
        name: String,
        vararg arguments: Pair<String, KommandArgument<*>>,
        init: KommandNode.() -> Unit
    )

    operator fun String.invoke(vararg arguments: Pair<String, KommandArgument<*>>, init: KommandNode.() -> Unit) =
        then(this, *arguments, init = init)
}