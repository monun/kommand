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
     * 명령 최상위 권한
     *
     * 기본값 = null
     */
    var permission: String?

}