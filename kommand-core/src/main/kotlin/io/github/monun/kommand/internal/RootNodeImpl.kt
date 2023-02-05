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

import io.github.monun.kommand.node.RootNode

class RootNodeImpl : AbstractKommandNode(), RootNode {
    override var fallbackPrefix: String by kommandField("")
    override var description: String by kommandField("")
    override var usage: String by kommandField("")
    override var permission: String? by kommandField(null)

    internal fun initialize(
        dispatcher: KommandDispatcherImpl,
        name: String,
        fallbackPrefix: String,
        description: String
    ) {
        super.initialize0(dispatcher, name)

        this.fallbackPrefix = fallbackPrefix
        this.description = description
        this.usage = "/$name"
    }
}