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

package io.github.monun.kommand.v1_17_1

import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.monun.kommand.AbstractKommandSuggestion
import io.papermc.paper.brigadier.PaperBrigadier
import net.kyori.adventure.text.ComponentLike

class NMSKommandSuggestion(
    private val nms: SuggestionsBuilder
) : AbstractKommandSuggestion() {
    override fun suggest(value: Int, tooltip: (() -> ComponentLike)?) {
        if (tooltip == null) nms.suggest(value)
        else nms.suggest(value, PaperBrigadier.message(tooltip()))
    }

    override fun suggest(text: String, tooltip: (() -> ComponentLike)?) {
        if (tooltip == null) nms.suggest(text)
        else nms.suggest(text, PaperBrigadier.message(tooltip()))
    }
}