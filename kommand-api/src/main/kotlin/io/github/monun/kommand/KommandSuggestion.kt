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

package io.github.monun.kommand

import net.kyori.adventure.text.ComponentLike

// 추천 빌더
@KommandDSL
interface KommandSuggestion {
    fun suggestDefault()

    fun suggest(value: Int, tooltip: (() -> ComponentLike)? = null)

    fun suggest(text: String, tooltip: (() -> ComponentLike)? = null)

    fun suggest(
        candidates: Iterable<String>,
        tooltip: ((String) -> ComponentLike)? = null
    )

    fun <T> suggest(
        candidates: Iterable<T>,
        transform: (T) -> String,
        tooltip: ((T) -> ComponentLike)? = null
    )

    fun <T> suggest(
        candidates: Map<String, T>,
        tooltip: ((T) -> ComponentLike)? = null
    )
}
