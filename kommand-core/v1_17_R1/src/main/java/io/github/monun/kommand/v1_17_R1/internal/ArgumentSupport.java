package io.github.monun.kommand.v1_17_R1.internal;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public final class ArgumentSupport {
    public static LiteralArgumentBuilder<CommandSourceStack> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static RequiredArgumentBuilder<CommandSourceStack, ?> argument(String name, ArgumentType<?> argumentType) {
        return RequiredArgumentBuilder.argument(name, argumentType);
    }

    private ArgumentSupport() {}
}
