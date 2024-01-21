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

package io.github.monun.kommand.internal.compat.v1_20_3

import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.monun.kommand.AbstractKommandSuggestion
import io.github.monun.kommand.ref.getValue
import io.github.monun.kommand.ref.weak
import io.papermc.paper.brigadier.PaperBrigadier
import net.kyori.adventure.text.ComponentLike
import net.minecraft.commands.SharedSuggestionProvider
import java.util.*

class NMSKommandSuggestion(
    handle: SuggestionsBuilder
) : AbstractKommandSuggestion() {
    private val handle by weak(handle)

    override fun suggest(value: Int, tooltip: (() -> ComponentLike)?) {
        if (tooltip == null) handle.suggest(value)
        else handle.suggest(value, PaperBrigadier.message(tooltip()))
    }

    override fun suggest(text: String, tooltip: (() -> ComponentLike)?) {
        if (tooltip == null) handle.suggest(text)
        else handle.suggest(text, PaperBrigadier.message(tooltip()))
    }

    override fun suggest(candidates: Iterable<String>, tooltip: ((String) -> ComponentLike)?) {
        val handle = handle
        val input: String = handle.remaining.lowercase(Locale.ROOT)

        candidates.forEach { candidate ->
            val lowerCandidate = candidate.lowercase(Locale.ROOT)

            if (SharedSuggestionProvider.matchesSubStr(input, lowerCandidate)) {
                if (tooltip == null) handle.suggest(candidate)
                else handle.suggest(candidate, PaperBrigadier.message(tooltip(candidate)))
            }
        }
    }

    override fun <T> suggest(
        candidates: Iterable<T>,
        transform: (T) -> String,
        tooltip: ((T) -> ComponentLike)?
    ) {
        val handle = handle
        val input: String = handle.remaining.lowercase(Locale.ROOT)

        candidates.forEach {
            val candidate = transform(it)
            val lowerCandidate = transform(it).lowercase(Locale.ROOT)

            if (SharedSuggestionProvider.matchesSubStr(input, lowerCandidate)) {
                if (tooltip == null) handle.suggest(candidate)
                else handle.suggest(candidate, PaperBrigadier.message(tooltip(it)))
            }
        }
    }

    override fun <T> suggest(
        candidates: Map<String, T>,
        tooltip: ((T) -> ComponentLike)?
    ) {
        val handle = handle
        val input: String = handle.remaining.lowercase(Locale.ROOT)

        candidates.forEach { (key, value) ->
            val lowerCandidate = key.lowercase(Locale.ROOT)

            if (SharedSuggestionProvider.matchesSubStr(input, lowerCandidate)) {
                if (tooltip == null) handle.suggest(key)
                else handle.suggest(key, PaperBrigadier.message(tooltip(value)))
            }
        }
    }
}