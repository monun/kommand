/*
 * Copyright (c) 2021 Noonmaru
 *
 *  Licensed under the General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-3.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.monun.kommand.argument

import com.github.monun.kommand.KommandContext
import org.bukkit.Bukkit
import org.bukkit.World

object WorldArgument : KommandArgument<World> {
    override val parseFailMessage: String
        get() = "${KommandArgument.TOKEN} <-- 월드를 찾지 못했습니다."

    override fun parse(context: KommandContext, param: String) = Bukkit.getWorld(param)

    override fun suggest(context: KommandContext, target: String) =
        Bukkit.getWorlds().suggest(target) { it.name }
}