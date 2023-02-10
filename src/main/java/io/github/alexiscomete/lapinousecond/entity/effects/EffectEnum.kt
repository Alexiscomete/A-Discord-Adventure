package io.github.alexiscomete.lapinousecond.entity.effects

import io.github.alexiscomete.lapinousecond.entity.Player

fun timeMillisForOnePixel(player: Player): Long {
    return 10000L / (player.getEffectLevel(EffectEnum.SPEED_TRAVELING) + 1)
}

fun priceToTravelWithEffect(player: Player, pathSize: Int): Double {
    return pathSize * 0.5 / (player.getEffectLevel(EffectEnum.COST_TRAVELING) + 1)
}

enum class EffectEnum(val displayName: String, val parentEffectEnum: EffectEnum? = null) {
    SPEED_TRAVELING("Fusée hypersonique"),
    COST_TRAVELING("Corruption de la guilde des lapins de voyage"),
    RABBIT_ADVERTISEMENT("Champ de vision envahi par la pub (RP)");
}