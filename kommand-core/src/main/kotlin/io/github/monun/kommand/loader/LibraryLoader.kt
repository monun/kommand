/*
 * Copyright 2021 Monun
 *
 * Licensed under the General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://opensource.org/licenses/gpl-3.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.github.monun.kommand.loader

import org.apache.commons.lang.reflect.ConstructorUtils
import org.bukkit.Bukkit
import java.lang.reflect.InvocationTargetException

object LibraryLoader {
    @Suppress("UNCHECKED_CAST")
    fun <T> load(type: Class<T>, vararg initArgs: Any? = emptyArray()): T {
        val packageName = type.`package`.name
        val className = "NMS${type.simpleName}"
        val parameterTypes = initArgs.map {
            it?.javaClass
        }.toTypedArray()

        val candidates = ArrayList<String>(2)
        candidates.add("$packageName.$bukkitVersion.$className")

        val lastDot = packageName.lastIndexOf('.')
        if (lastDot > 0) {
            val superPackageName = packageName.substring(0, lastDot)
            val subPackageName = packageName.substring(lastDot + 1)
            candidates.add("$superPackageName.$bukkitVersion.$subPackageName.$className")
        }

        return try {
            val nmsClass = candidates.mapNotNull { candidate ->
                try {
                    Class.forName(candidate, true, type.classLoader).asSubclass(type)
                } catch (exception: ClassNotFoundException) {
                    null
                }
            }.firstOrNull() ?: throw ClassNotFoundException("Not found nms library class: $candidates")
            val constructor = ConstructorUtils.getMatchingAccessibleConstructor(nmsClass, parameterTypes)
                ?: throw UnsupportedOperationException("${type.name} does not have Constructor for [${parameterTypes.joinToString()}]")
            constructor.newInstance() as T
        } catch (exception: ClassNotFoundException) {
            throw UnsupportedOperationException("${type.name} does not support this version: $minecraftVersion", exception)
        } catch (exception: IllegalAccessException) {
            throw UnsupportedOperationException("${type.name} constructor is not visible")
        } catch (exception: InstantiationException) {
            throw UnsupportedOperationException("${type.name} is abstract class")
        } catch (exception: InvocationTargetException) {
            throw UnsupportedOperationException("${type.name} has an error occurred while creating the instance", exception)
        }
    }

    private val bukkitVersion by lazy {
        with("v\\d+_\\d+_R\\d+".toPattern().matcher(Bukkit.getServer()::class.java.`package`.name)) {
            when {
                find() -> group()
                else -> throw NoSuchElementException("No such bukkit version exists")
            }
        }
    }

    private val minecraftVersion by lazy {
        with("(?<=\\(MC: )[\\d.]+?(?=\\))".toPattern().matcher(Bukkit.getVersion())) {
            when {
                find() -> group()
                else -> throw NoSuchElementException("No such minecraft version exists")
            }
        }
    }
}