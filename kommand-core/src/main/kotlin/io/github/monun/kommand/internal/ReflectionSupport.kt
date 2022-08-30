package io.github.monun.kommand.internal

object ReflectionSupport {
    fun <T> getFieldInstance(target: Any, name: String): T {
        val type = target.javaClass
        val field = type.getDeclaredField(name).apply { isAccessible = true }

        @Suppress("UNCHECKED_CAST")
        return field.get(target) as T
    }

    fun <T> getFieldInstance(target: Any, name: String, alt: String): T {
        return kotlin.runCatching {
            getFieldInstance<T>(target, name)
        }.getOrNull() ?: getFieldInstance(target, alt)
    }
}

