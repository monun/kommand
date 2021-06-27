package io.github.monun.kommand

import org.bukkit.command.CommandSender
import org.bukkit.command.PluginCommand
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*

class JavaPluginTest {
    private fun mockedPlugin(): JavaPlugin {
        val plugin = mock(JavaPlugin::class.java)
        val command = PluginCommand::class.java.getDeclaredConstructor(String::class.java, Plugin::class.java).apply { isAccessible = true }.newInstance("test", plugin)
        `when`(plugin.getCommand("test")).thenReturn(command)

        return plugin
    }

    private fun mockedPlayer(): CommandSender {
        return mock(CraftPlayer::class.java)
    }

    @Test
    fun test() {
        val plugin = mockedPlugin()
        val command = plugin.getCommand("test") ?: error("Not found command test")
        val sender = mockedPlayer()
        val ensurer = mock(Ensurer::class.java)

        plugin.kommand {
            register("test") {
                then("first") {
                    executes {
                        ensurer.first()
                    }
                }
                then("second") {
                    executes {
                        ensurer.second()
                    }
                }
            }
        }
        val executor = command.executor
        executor.onCommand(sender, command, "test", arrayOf("first"))
        executor.onCommand(sender, command, "test", arrayOf("second"))

        verify(ensurer, times(1)).first()
        verify(ensurer, times(1)).second()
    }

    open class Ensurer {
        open fun first() {
            println("FIRST")
        }
        open fun second() {
            println("SECOND")
        }
    }
}