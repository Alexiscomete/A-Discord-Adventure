package io.github.alexiscomete.lapinousecond.view.ui.longuis.inv

import io.github.alexiscomete.lapinousecond.entity.effects.Effect
import io.github.alexiscomete.lapinousecond.entity.effects.EffectEnum
import io.github.alexiscomete.lapinousecond.entity.effects.TimedEffect
import io.github.alexiscomete.lapinousecond.view.discord.commands.classes.SECOND_TO_MILLIS

class InvEffectUIPart(
    private val effectType: EffectEnum
) {
    private var description = "niveau (temps restant)"
    private var totalLevel = 0

    fun add(effect: Effect) {
        totalLevel += effect.level
        description += if (effect is TimedEffect) {
            "; ${effect.level} (${effect.remainingDuration / SECOND_TO_MILLIS}s)"
        } else {
            "; ${effect.level}"
        }
    }

    fun getPair(): Pair<String, String> {
        return Pair(effectType.displayName, "Niveau total : $totalLevel\nDécomposition : $description")
    }
}