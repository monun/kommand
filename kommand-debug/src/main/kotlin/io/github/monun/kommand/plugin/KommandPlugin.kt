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

package io.github.monun.kommand.plugin

import com.destroystokyo.paper.profile.PlayerProfile
import com.google.gson.JsonObject
import io.github.monun.kommand.KommandArgument
import io.github.monun.kommand.StringType
import io.github.monun.kommand.getValue
import io.github.monun.kommand.kommand
import io.github.monun.kommand.wrapper.BlockPosition3D
import io.github.monun.kommand.wrapper.EntityAnchor
import io.github.monun.kommand.wrapper.Position3D
import io.github.monun.kommand.wrapper.Rotation
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.md_5.bungee.api.ChatColor
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Team
import java.util.*

class KommandPlugin : JavaPlugin() {
    override fun onEnable() {
        val bool = KommandArgument.bool()
        val int = KommandArgument.int()
        val word = KommandArgument.string(StringType.SINGLE_WORD)
        val string = KommandArgument.string(StringType.QUOTABLE_PHRASE)
        val greedy = KommandArgument.string(StringType.GREEDY_PHRASE)

        kommand {
            register("my", "myalias") {
                permission("my.commands")

                then("age") {
                    then("age" to int) {
                        permission("my.age")

                        executes {
                            val age: Int by it
                            Bukkit.broadcast(text("내 나이는 $age 살입니다."))
                        }
                    }
                }
                then("flag") {
                    then("flag" to bool) {
                        executes {
                            val flag: Boolean by it
                            Bukkit.broadcast(text("플래그 $flag"))
                        }
                    }
                }
                then("word") {
                    then("text" to word) {
                        executes {
                            val text: String by it
                            Bukkit.broadcast(text("word $text"))
                        }
                    }
                }
                then("string") {
                    then("text" to string) {
                        executes {
                            val text: String by it
                            Bukkit.broadcast(text("quote $text"))
                        }
                    }
                }
                then("greedy") {
                    then("text" to greedy) {
                        executes {
                            val text: String by it
                            Bukkit.broadcast(text("greedy $text"))
                        }
                    }
                }
                then("color") {
                    then("color" to KommandArgument.color()) {
                        executes {
                            val color: ChatColor by it
                            Bukkit.broadcast(text("$color color"))
                        }
                    }
                }
                then("component") {
                    then("component" to KommandArgument.component()) {
                        executes {
                            val component: Component by it
                            Bukkit.broadcast(component)
                        }
                    }
                }
                then("compoundTag") {
                    then("compoundTag" to KommandArgument.compoundTag()) {
                        executes {
                            val compoundTag: JsonObject by it
                            Bukkit.broadcast(text(compoundTag.toString()))
                        }
                    }
                }
                then("dimension") {
                    then("world" to KommandArgument.dimension()) {
                        executes {
                            val world: World by it
                            Bukkit.broadcast(text(world.toString()))
                        }
                    }
                }
                then("entityAnchor") {
                    then("entityAnchor" to KommandArgument.entityAnchor()) {
                        executes {
                            val entityAnchor: EntityAnchor by it
                            Bukkit.broadcast(text("anchor ${entityAnchor.name}"))
                        }
                    }
                }
                then("entity") {
                    then("entity" to KommandArgument.entity()) {
                        executes {
                            val entity: Entity by it
                            Bukkit.broadcast(text("entity $entity"))
                        }
                    }
                }
                then("entities") {
                    then("entities" to KommandArgument.entities()) {
                        executes {
                            val entities: Collection<Entity> by it
                            Bukkit.broadcast(text("entities $entities"))
                        }
                    }
                }
                then("player") {
                    then("player" to KommandArgument.player()) {
                        executes {
                            val player: Player by it
                            Bukkit.broadcast(text("player $player"))
                        }
                    }
                }
                then("players") {
                    then("players" to KommandArgument.players()) {
                        executes {
                            val players: Collection<Player> by it
                            Bukkit.broadcast(text("players $players"))
                        }
                    }
                }
                then("summonable") {
                    then("summonable" to KommandArgument.summonableEntity()) {
                        executes {
                            val summonable: NamespacedKey by it
                            Bukkit.broadcast(text("summonable $summonable"))
                        }
                    }
                }
                then("profile") {
                    then("profile" to KommandArgument.profile()) {
                        executes {
                            val profile: Collection<PlayerProfile> by it
                            Bukkit.broadcast(text(profile.toString()))
                        }
                    }
                }
                then("enchantment") {
                    then("enchantment" to KommandArgument.enchantment()) {
                        executes {
                            val enchantment: Enchantment by it
                            Bukkit.broadcast(text(enchantment.toString()))
                        }
                    }
                }
                then("mobeffect") {
                    then("mobeffect" to KommandArgument.mobEffect()) {
                        executes {
                            val mobeffect: PotionEffectType by it
                            Bukkit.broadcast(text("mobeffect ${mobeffect.name}"))
                        }
                    }
                }
                //
                then("blockPredicate") {
                    requires {
                        playerOrNull != null
                    }

                    then("predicate" to KommandArgument.blockPredicate()) {
                        executes {
                            val predicate: (Block) -> Boolean by it
                            val flag = predicate(player.location.add(0.0, -1.0, 0.0).block)
                            Bukkit.broadcast(text("$flag predicate"))
                        }
                    }
                }
                then("blockState") {
                    then("state" to KommandArgument.blockState()) {
                        executes {
                            val state: BlockData by it
                            val asString = state.getAsString(true)
                            Bukkit.broadcast(text("blockData: $asString"))
                        }
                    }
                }
                then("blockPosition") {
                    requires {
                        playerOrNull != null
                    }

                    then("position" to KommandArgument.blockPosition()) {
                        executes {
                            val position: BlockPosition3D by it
                            Bukkit.broadcast(text(position.toBlock(player.world).type.translationKey))
                        }
                    }
                }
                then("position") {
                    requires {
                        playerOrNull != null
                    }

                    then("position" to KommandArgument.position()) {
                        executes {
                            val position: Position3D by it
                            Bukkit.broadcast(text("${position.asVector.distance(player.location.toVector())} far"))
                        }
                    }
                }
                then("rotation") {
                    requires {
                        playerOrNull != null
                    }

                    then("rotation" to KommandArgument.rotation()) {
                        executes {
                            val rotation: Rotation by it
//                        it.source.player.setRotation(rotation.yaw, rotation.pitch)
                            Bukkit.broadcast(text("[${rotation.yaw}, ${rotation.pitch}]"))
                        }
                    }
                }
                then("swizzle") {
                    then("swizzle" to KommandArgument.swizzle()) {
                        executes {
                            val swizzle: EnumSet<Axis> by it
                            Bukkit.broadcast(text(swizzle.joinToString()))
                        }
                    }
                }
                then("item") {
                    requires {
                        playerOrNull != null
                    }

                    then("item" to KommandArgument.item()) {
                        executes {
                            val item: ItemStack by it
                            player.inventory.addItem(item)
                        }
                    }
                }
                then("itemPredicate") {
                    requires {
                        playerOrNull != null
                    }
                    then("predicate" to KommandArgument.itemPredicate()) {
                        executes {
                            val predicate: (ItemStack) -> Boolean by it
                            val flag = predicate(player.inventory.itemInMainHand)
                            Bukkit.broadcast(text("$flag predicate"))
                        }
                    }
                }
                then("dynamic") {
                    val dynamicArgument = dynamic { _, input ->
                        when (input) {
                            "one" -> 1
                            "two" -> 2
                            "three" -> 3
                            else -> null
                        }
                    }.apply {
                        suggests {
                            suggest(listOf("one", "two", "three")) {
                                text("tooltip $it")
                            }
                        }
                    }

                    then("dynamic" to dynamicArgument) {
                        executes {
                            val dynamic: Int by it
                            broadcast(text(dynamic))
                        }
                    }
                }
                then("dynamicMap") {
                    val map = mapOf(
                        "apple" to Material.APPLE,
                        "diamond" to Material.DIAMOND,
                        "grass" to Material.GRASS
                    )
                    val mapArgument = dynamicByMap(map)

                    then("dynamic" to mapArgument) {
                        executes {
                            val dynamic: Material by it
                            broadcast(text(dynamic.name))
                        }
                    }
                }
                then("dynamicEnum") {
                    val set = EnumSet.of(Material.STONE, Material.COBBLESTONE, Material.ACACIA_BOAT)
                    val enumArgument = dynamicByEnum(set)

                    then("dynamic" to enumArgument) {
                        executes {
                            val dynamic: Material by it
                            broadcast(text(dynamic.name))
                        }
                    }
                }
                then("multiple") {
                    then("first" to int(), "second" to bool(), "third" to string()) {
                        executes {
                            val first: Int by it
                            val second: Boolean by it
                            val third: String by it

                            broadcast(text("$first $second $third"))
                        }
                    }
                }
                then("team") {
                    then("team" to team()) {
                        executes {
                            val team: Team by it

                            broadcast(text("team = ${team.name}"))
                        }
                    }
                }
            }
        }
    }
}