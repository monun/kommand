package io.github.monun.kommand.internal.v1_17_R1;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public final class NMSArgument {
    public static LiteralArgumentBuilder<CommandSourceStack> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static RequiredArgumentBuilder<CommandSourceStack, ?> argument(String name, ArgumentType<?> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    private NMSArgument() {}
}
