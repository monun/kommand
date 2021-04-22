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
import org.bukkit.command.Command
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

class EnumArgumentTest {
    @Test
    fun test() {
        val mockContext = KommandContext(mock(Command::class.java), emptyArray(), emptyList())
        val argument = EnumArgument(TestEnum.values().asList())

        for (value in TestEnum.values()) {
            val name = value.name

            assertIterableEquals(listOf(name), argument.suggest(mockContext, name))
            assertEquals(value, argument.parse(mockContext, name))
        }

        assert(argument.suggest(mockContext, "NONE").isEmpty())
        assertNull(argument.parse(mockContext, "NONE"))
    }
}

enum class TestEnum {
    A,
    B,
    C
}