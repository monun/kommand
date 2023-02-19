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

import io.github.monun.kommand.KommandDSL
import io.github.monun.kommand.KommandSource

// 상수 문자열 노드
@KommandDSL
interface RootNode : KommandNode {
    /**
     * 명령의 접두사
     *
     * 기본값 = <플러그인의 이름>
     */
    var fallbackPrefix: String

    /**
     * 명령의 설명
     *
     * 기본값 = A <플러그인의 이름> provided command
     */
    var description: String

    /**
     * 명령어 사용법
     *
     * 기본값 = /<명령>
     */
    var usage: String


    /**
     * 명령 실행에 필요한 권한을 체크합니다.
     *
     * 이 함수는 모든 하위 노드에 영향을 줍니다.
     *
     * ```kotlin
     * kommand {
     *   register("mycmd") {
     *   requires { hasPermission(4) }
     *     then("first") { // mycmd first
     *       executes {
     *       // 4 레벨 권한이 있어야 실행됨
     *       }
     *       then("second") { // /mycmd first second
     *         executes {
     *         // 4 레벨 권한이 있어야 실행됨
     *         }
     *       }
     *     }
     *   }
     * }
     * ```
     *
     * @see KommandNode.requires
     */
    override fun requires(requires: KommandSource.() -> Boolean)

}