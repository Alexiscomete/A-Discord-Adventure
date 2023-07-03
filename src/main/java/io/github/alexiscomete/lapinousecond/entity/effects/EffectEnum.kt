package io.github.alexiscomete.lapinousecond.entity.effects

import io.github.alexiscomete.lapinousecond.entity.entities.Player

const val DEFAULT_TIME_FOR_ONE_PIXEL = 10000L
const val DEFAULT_PRICE_FOR_ONE_PIXEL = 0.5

fun timeMillisForOnePixel(player: Player): Long {
    return DEFAULT_TIME_FOR_ONE_PIXEL / (player.getEffectLevel(EffectEnum.SPEED_TRAVELING) + 1)
}

fun priceToTravelWithEffect(player: Player, pathSize: Int): Double {
    return pathSize * DEFAULT_PRICE_FOR_ONE_PIXEL / (player.getEffectLevel(EffectEnum.COST_TRAVELING) + 1)
}

enum class EffectEnum(val displayName: String, val parentEffectEnum: EffectEnum? = null) {
    SPEED_TRAVELING("Fusée hypersonique"),
    COST_TRAVELING("Corruption de la guilde des lapins de voyage"),
    RABBIT_ADVERTISEMENT("Champ de vision envahi par la pub (RP)");
}