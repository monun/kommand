package io.github.monun.kommand.v1_17_R1

import com.mojang.brigadier.suggestion.SuggestionsBuilder
import io.github.monun.kommand.AbstractKommandSuggestion
import io.papermc.paper.brigadier.PaperBrigadier
import net.kyori.adventure.text.ComponentLike

class NMSKommandSuggestion(
    private val nms: SuggestionsBuilder
) : AbstractKommandSuggestion() {
    override fun suggest(value: Int, tooltip: (() -> ComponentLike)?) {
        if (tooltip == null) nms.suggest(value)
        else nms.suggest(value, PaperBrigadier.message(tooltip()))
    }

    override fun suggest(text: String, tooltip: (() -> ComponentLike)?) {
        if (tooltip == null) nms.suggest(text)
        else nms.suggest(text, PaperBrigadier.message(tooltip()))
    }
}