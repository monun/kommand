/*
 * Copyright (C) 2023 Monun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.monun.kommand.internal.compat.v1_20_2

import com.destroystokyo.paper.profile.CraftPlayerProfile
import com.destroystokyo.paper.profile.PlayerProfile
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.*
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.monun.kommand.*
import io.github.monun.kommand.internal.AbstractKommandArgument
import io.github.monun.kommand.internal.ReflectionSupport
import io.github.monun.kommand.wrapper.*
import io.github.monun.kommand.wrapper.Rotation
import io.papermc.paper.brigadier.PaperBrigadier
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.*
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument
import net.minecraft.commands.arguments.blocks.BlockStateArgument
import net.minecraft.commands.arguments.coordinates.*
import net.minecraft.commands.arguments.item.FunctionArgument
import net.minecraft.commands.arguments.item.ItemArgument
import net.minecraft.commands.arguments.item.ItemPredicateArgument
import net.minecraft.commands.synchronization.SuggestionProviders
import net.minecraft.core.Vec3i
import net.minecraft.core.registries.Registries
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ColumnPos
import net.minecraft.world.level.block.state.pattern.BlockInWorld
import org.bukkit.*
import org.bukkit.advancement.Advancement
import org.bukkit.block.Block
import org.bukkit.block.data.BlockData
import org.bukkit.craftbukkit.v1_20_R2.CraftParticle
import org.bukkit.craftbukkit.v1_20_R2.block.CraftBlock
import org.bukkit.craftbukkit.v1_20_R2.block.data.CraftBlockData
import org.bukkit.craftbukkit.v1_20_R2.enchantments.CraftEnchantment
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_20_R2.potion.CraftPotionEffectType
import org.bukkit.craftbukkit.v1_20_R2.util.CraftNamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Team
import java.lang.reflect.Method
import java.util.*
import java.util.concurrent.CompletableFuture

open class NMSKommandArgument<T>(
    val type: ArgumentType<*>,
    private val provider: (NMSKommandContext, name: String) -> T,
    private val defaultSuggestionProvider: SuggestionProvider<CommandSourceStack>? = null
) : AbstractKommandArgument<T>() {
    private companion object {
        private val originalMethod: Method = ArgumentType::class.java.declaredMethods.find { method ->
            val parameterTypes = method.parameterTypes

            parameterTypes.count() == 2
                    && parameterTypes[0] == CommandContext::class.java
                    && parameterTypes[1] == SuggestionsBuilder::class.java
        } ?: error("Not found listSuggestion")

        private val overrideSuggestions = hashMapOf<Class<*>, Boolean>()

        private fun checkOverrideSuggestions(type: Class<*>): Boolean = overrideSuggestions.computeIfAbsent(type) {
            originalMethod.declaringClass != type.getMethod(
                originalMethod.name,
                *originalMethod.parameterTypes
            ).declaringClass
        }
    }

    private val hasOverrideSuggestion: Boolean by lazy {
        checkOverrideSuggestions(type.javaClass)
    }

    fun from(context: NMSKommandContext, name: String): T {
        return provider(context, name)
    }

    fun listSuggestions(
        context: NMSKommandContext,
        builder: SuggestionsBuilder
    ): CompletableFuture<Suggestions> {
        this.suggestionProvider?.let {
            val suggestion = NMSKommandSuggestion(builder)
            it(suggestion, context)
            if (!suggestion.suggestsDefault) return builder.buildFuture()
        }

        defaultSuggestionProvider?.let { return it.getSuggestions(context.handle, builder) }
        if (hasOverrideSuggestion) return type.listSuggestions(context.handle, builder)
        return builder.buildFuture()
    }
}

infix fun <T> ArgumentType<*>.provideDynamic(
    provider: (context: NMSKommandContext, name: String) -> T
): NMSKommandArgument<T> {
    return NMSKommandArgument(this, provider)
}

infix fun <T> ArgumentType<*>.provide(
    provider: (context: CommandContext<CommandSourceStack>, name: String) -> T
): NMSKommandArgument<T> {
    return NMSKommandArgument(this, { context, name ->
        provider(context.handle, name)
    })
}

infix fun <T> Pair<ArgumentType<*>, SuggestionProvider<CommandSourceStack>>.provide(
    provider: (context: CommandContext<CommandSourceStack>, name: String) -> T
): NMSKommandArgument<T> {
    return NMSKommandArgument(first, { context, name ->
        provider(context.handle, name)
    }, defaultSuggestionProvider = second)
}

class NMSKommandArgumentSupport : KommandArgumentSupport {
    // com.mojang.brigadier.arguments

    override fun bool(): KommandArgument<Boolean> {
        return BoolArgumentType.bool() provide BoolArgumentType::getBool
    }

    override fun int(minimum: Int, maximum: Int): KommandArgument<Int> {
        return IntegerArgumentType.integer(minimum, maximum) provide IntegerArgumentType::getInteger
    }

    override fun float(minimum: Float, maximum: Float): KommandArgument<Float> {
        return FloatArgumentType.floatArg(minimum, maximum) provide FloatArgumentType::getFloat
    }

    override fun double(minimum: Double, maximum: Double): KommandArgument<Double> {
        return DoubleArgumentType.doubleArg(minimum, maximum) provide DoubleArgumentType::getDouble
    }

    override fun long(minimum: Long, maximum: Long): KommandArgument<Long> {
        return LongArgumentType.longArg(minimum, maximum) provide LongArgumentType::getLong
    }

    override fun string(type: StringType): KommandArgument<String> {
        return type.createType() provide StringArgumentType::getString
    }

    // net.minecraft.commands.arguments

    override fun angle(): KommandArgument<Float> {
        return AngleArgument.angle() provide AngleArgument::getAngle
    }

    override fun color(): KommandArgument<TextColor> {
        return ColorArgument.color() provide { context, name ->
            ColorArgument.getColor(context, name).color?.let { color ->
                NamedTextColor.namedColor(color) ?: TextColor.color(color)
            } ?: NamedTextColor.WHITE
        }
    }

    override fun component(): KommandArgument<Component> {
        return ComponentArgument.textComponent() provide { context, name ->
            val nmsComponent = ComponentArgument.getComponent(context, name)
            PaperBrigadier.componentFromMessage(nmsComponent)
        }
    }

    override fun compoundTag(): KommandArgument<JsonObject> {
        return CompoundTagArgument.compoundTag() provide { context, name ->
            val compoundTag = CompoundTagArgument.getCompoundTag(context, name)
            JsonParser.parseString(compoundTag.toString()) as JsonObject
        }
    }

    override fun dimension(): KommandArgument<World> {
        return DimensionArgument.dimension() provide { context, name ->
            DimensionArgument.getDimension(context, name).world
        }
    }

    override fun entityAnchor(): KommandArgument<EntityAnchor> {
        return EntityAnchorArgument.anchor() provide { context, name ->
            when (EntityAnchorArgument.getAnchor(context, name) ?: error("Unknown entity anchor")) {
                EntityAnchorArgument.Anchor.FEET -> EntityAnchor.FEET
                EntityAnchorArgument.Anchor.EYES -> EntityAnchor.EYES
            }
        }
    }

    override fun entity(): KommandArgument<Entity> {
        return EntityArgument.entity() provide { context, name ->
            EntityArgument.getEntity(context, name).bukkitEntity
        }
    }

    override fun entities(): KommandArgument<Collection<Entity>> {
        return EntityArgument.entities() provide { context, name ->
            EntityArgument.getEntities(context, name).map { it.bukkitEntity }
        }
    }

    override fun player(): KommandArgument<Player> {
        return EntityArgument.player() provide { context, name ->
            EntityArgument.getPlayer(context, name).bukkitEntity
        }
    }

    override fun players(): KommandArgument<Collection<Player>> {
        return EntityArgument.players() provide { context, name ->
            EntityArgument.getPlayers(context, name).map { it.bukkitEntity }
        }
    }

    override fun summonableEntity(): KommandArgument<NamespacedKey> {
        return ResourceArgument.resource(
            commandBuildContext,
            Registries.ENTITY_TYPE
        ) to SuggestionProviders.SUMMONABLE_ENTITIES provide { context, name ->
            CraftNamespacedKey.fromMinecraft(ResourceArgument.getSummonableEntityType(context, name).key().location())
        }
    }

    override fun profile(): KommandArgument<Collection<PlayerProfile>> {
        return GameProfileArgument.gameProfile() provide { context, name ->
            val nms = GameProfileArgument.getGameProfiles(context, name)
            nms.map { CraftPlayerProfile.asBukkitMirror(it) }
        }
    }

    private val enchantmentMap = Enchantment.values().map { it as CraftEnchantment }.associateBy { it.handle }

    override fun enchantment(): KommandArgument<Enchantment> {
        return ResourceArgument.resource(commandBuildContext, Registries.ENCHANTMENT) provide { context, name ->
            val key = ResourceArgument.getEnchantment(context, name)
            val value = key.value()

            enchantmentMap[value] ?: error("Not found enchantment ${value.getFullname(0)}")
        }
    }

    override fun message(): KommandArgument<Component> {
        return MessageArgument.message() provide { context, name ->
            PaperBrigadier.componentFromMessage(MessageArgument.getMessage(context, name))
        }
    }

    private val mobEffectMap = PotionEffectType.values().map { it as CraftPotionEffectType }.associateBy { it.handle }

    override fun mobEffect(): KommandArgument<PotionEffectType> {
        return ResourceArgument.resource(commandBuildContext, Registries.MOB_EFFECT) provide { context, name ->
            val key = ResourceArgument.getMobEffect(context, name)
            val value = key.value()

            mobEffectMap[value] ?: error("Not found mob effect ${value.displayName}")
        }
    }

    override fun objective(): KommandArgument<Objective> {
        return ObjectiveArgument.objective() provide { context, name ->
            val nms = ObjectiveArgument.getObjective(context, name)
            Bukkit.getScoreboardManager().mainScoreboard.getObjective(nms.name) ?: error("Objective error!")
        }
    }

    override fun objectiveCriteria(): KommandArgument<String> {
        return ObjectiveCriteriaArgument.criteria() provide { context, name ->
            ObjectiveCriteriaArgument.getCriteria(context, name).name
        }
    }

    override fun particle(): KommandArgument<Particle> {
        return ParticleArgument.particle(commandBuildContext) provide { context, name ->
            CraftParticle.minecraftToBukkit(ParticleArgument.getParticle(context, name).type)
        }
    }

    override fun intRange(): KommandArgument<IntRange> {
        return RangeArgument.intRange() provide { context, name ->
            val nms = RangeArgument.Ints.getRange(context, name)
            val min = nms.min.orElse(Int.MIN_VALUE)
            val max = nms.max.orElse(Int.MAX_VALUE)
            min..max
        }
    }

    //float
    override fun doubleRange(): KommandArgument<ClosedFloatingPointRange<Double>> {
        return RangeArgument.floatRange() provide { context, name ->
            val nms = RangeArgument.Floats.getRange(context, name)
            val min = nms.min.orElse(-Double.MAX_VALUE)
            val max = nms.max.orElse(Double.MAX_VALUE)
            min..max
        }
    }

    override fun advancement(): KommandArgument<Advancement> {
        return ResourceLocationArgument.id() provide { context, name ->
            val nms = ResourceLocationArgument.getAdvancement(context, name)
            nms.toBukkit()
        }
    }

    override fun recipe(): KommandArgument<Recipe> {
        return ResourceLocationArgument.id() to SuggestionProviders.ALL_RECIPES provide { context, name ->
            val nms = ResourceLocationArgument.getRecipe(context, name)
            nms.toBukkitRecipe()
        }
    }

    private val displaySlots = DisplaySlot.values().associateBy { it.id }

    override fun displaySlot(): KommandArgument<DisplaySlot> {
        return ScoreboardSlotArgument.displaySlot() provide { context, name ->
            val slotName = ScoreboardSlotArgument.getDisplaySlot(context, name).serializedName
            displaySlots[slotName] ?: error("Not found display slot $slotName")
        }
    }

    override fun score(): KommandArgument<String> {
        return ScoreHolderArgument.scoreHolder() provide { context, name ->
            ScoreHolderArgument.getName(context, name)
        }
    }

    override fun scores(): KommandArgument<Collection<String>> {
        return ScoreHolderArgument.scoreHolders() provide { context, name ->
            ScoreHolderArgument.getNames(context, name)
        }
    }

    override fun slot(): KommandArgument<Int> {
        return SlotArgument.slot() provide { context, name ->
            SlotArgument.getSlot(context, name)
        }
    }

//    new SimpleCommandExceptionType(Component.translatable("commands.team.option.seeFriendlyInvisibles.alreadyEnabled"));

    /**
     * TeamArgument의 error
     */
    private val errorTeamNotFound: DynamicCommandExceptionType = DynamicCommandExceptionType { name: Any? ->
        net.minecraft.network.chat.Component.translatable(
            "team.notFound",
            name
        )
    }

    override fun team(): KommandArgument<Team> {
        return TeamArgument.team() provide { context, name ->
            /**
             * CraftTeam이 패키지 접근만 허용하여 생성불가
             * PlayerTeam(nms) -> name -> BukkitTeam 순으로 가져와야함
             * 하지만...
             *
             * val team: PlayerTeam = TeamArgument.getTeam(context, name)
             * val teamName = team.name <-- spigot mapping에서 오류가 있음
             * java.lang.NoSuchMethodError: 'java.lang.String net.minecraft.world.scores.ScoreboardTeam.b()'
             */
            val teamName: String = context.getArgument(name, String::class.java)
            Bukkit.getScoreboardManager().mainScoreboard.getTeam(teamName) ?: throw errorTeamNotFound.create(teamName)
        }
    }

    override fun time(): KommandArgument<Int> {
        val argument = TimeArgument.time()
        return argument provide { context, name ->
            val time = context.getArgument(name, String::class.java)
            argument.parse(StringReader(time))
        }
    }

    override fun uuid(): KommandArgument<UUID> {
        return UuidArgument.uuid() provide UuidArgument::getUuid
    }

    companion object {
        private val commandBuildContext: CommandBuildContext = ReflectionSupport.getFieldInstance(
            MinecraftServer.getServer().resources.managers,
            "commandBuildContext",
            "c"
        )
    }

    // net.minecraft.commands.arguments.blocks

    override fun blockPredicate(): KommandArgument<(Block) -> Boolean> {
        return BlockPredicateArgument.blockPredicate(commandBuildContext) provide { context, name ->
            { block ->
                BlockPredicateArgument.getBlockPredicate(context, name)
                    .test(BlockInWorld(context.source.level, (block as CraftBlock).position, true))
            }
        }
    }

    override fun blockState(): KommandArgument<BlockData> {
        return BlockStateArgument.block(commandBuildContext) provide { context, name ->
            CraftBlockData.fromData(BlockStateArgument.getBlock(context, name).state)
        }
    }

    // net.minecraft.commands.arguments.coordinates

    override fun blockPosition(type: PositionLoadType): KommandArgument<BlockPosition3D> {
        /**
         * Issue [https://github.com/monun/kommand/issues/18]
         *
         * mojang mapping -> spigot mapping 변환시 상속된 타입의 메서드 이름 or 필드 이름을 잘 감지하지 못함
         * 변수의 타입을 메서드, 필드가 선언된 타입으로 정확히 선언해야함
         * [net.minecraft.core.BlockPos]의 경우 [net.minecraft.core.Vec3i]를 상속받아 상위의 메서드를 가지고 있음
         * BlockPos#getX <- 실패
         * Vec3i#getX <- 성공
         *
         * 아마 remapping 작업이 다음과 같은 바이트 코드만 감지하는듯
         * invokeVirtual 'Vec3i#getX'
         */
        return BlockPosArgument.blockPos() provide { context, name ->
            val blockPosition: Vec3i = when (type) {
                PositionLoadType.LOADED -> BlockPosArgument.getLoadedBlockPos(context, name)
                PositionLoadType.SPAWNABLE -> BlockPosArgument.getSpawnablePos(context, name)
            }

            BlockPosition3D(blockPosition.x, blockPosition.y, blockPosition.z)
        }
    }

    override fun blockPosition2D(): KommandArgument<BlockPosition2D> {
        return ColumnPosArgument.columnPos() provide { context, name ->
            val columnPosition: ColumnPos = ColumnPosArgument.getColumnPos(context, name)
            BlockPosition2D(columnPosition.x, columnPosition.z)
        }
    }

    override fun position(): KommandArgument<Position3D> {
        return Vec3Argument.vec3() provide { context, name ->
            val vec3 = Vec3Argument.getVec3(context, name)
            Position3D(vec3.x, vec3.y, vec3.z)
        }
    }

    override fun position2D(): KommandArgument<Position2D> {
        return Vec2Argument.vec2() provide { context, name ->
            val vec2 = Vec2Argument.getVec2(context, name)
            Position2D(vec2.x.toDouble(), vec2.y.toDouble())
        }
    }

    override fun rotation(): KommandArgument<Rotation> {
        return RotationArgument.rotation() provide { context, name ->
            val rotation = RotationArgument.getRotation(context, name).getRotation(context.source)
            Rotation(rotation.x, rotation.y)
        }
    }

    override fun swizzle(): KommandArgument<EnumSet<Axis>> {
        return SwizzleArgument.swizzle() provide { context, name ->
            EnumSet.copyOf(SwizzleArgument.getSwizzle(context, name).map { axis ->
                Axis.valueOf(axis.getName().uppercase())
            })
        }
    }

    // net.minecraft.commands.arguments.item

    override fun function(): KommandArgument<() -> Unit> {
        return FunctionArgument.functions() provide { context, name ->
            {
                FunctionArgument.getFunctions(context, name).map { function ->
                    context.source.server.functions.execute(function, context.source)
                }
            }
        }
    }

    override fun item(): KommandArgument<ItemStack> {
        return ItemArgument.item(commandBuildContext) provide { context, name ->
            CraftItemStack.asBukkitCopy(ItemArgument.getItem(context, name).createItemStack(1, false))
        }
    }

    override fun itemPredicate(): KommandArgument<(ItemStack) -> Boolean> {
        return ItemPredicateArgument.itemPredicate(commandBuildContext) provide { context, name ->
            { itemStack ->
                ItemPredicateArgument.getItemPredicate(context, name).test(CraftItemStack.asNMSCopy(itemStack))
            }
        }
    }

    private val unknownArgument =
        SimpleCommandExceptionType(net.minecraft.network.chat.Component.translatable("command.unknown.argument"))

    override fun <T> dynamic(
        type: StringType,
        function: KommandSource.(context: KommandContext, input: String) -> T?
    ): KommandArgument<T> {
        return type.createType() provideDynamic { context, name ->
            context.source.function(context, StringArgumentType.getString(context.handle, name))
                ?: throw unknownArgument.create()
        }
    }
}

fun StringType.createType(): StringArgumentType {
    return when (this) {
        StringType.SINGLE_WORD -> StringArgumentType.word()
        StringType.QUOTABLE_PHRASE -> StringArgumentType.string()
        StringType.GREEDY_PHRASE -> StringArgumentType.greedyString()
    }
}
