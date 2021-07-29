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

package io.github.monun.kommand.internal

import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.KommandContext
import io.github.monun.kommand.KommandSuggestion

abstract class AbstractKommandArgument<T> : KommandArgument<T> {
    var suggestionProvider: (KommandSuggestion.(KommandContext) -> Unit)? = null

    override fun suggests(provider: KommandSuggestion.(context: KommandContext) -> Unit) {
        this.suggestionProvider = provider
    }
}