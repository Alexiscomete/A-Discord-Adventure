package io.github.alexiscomete.lapinousecond.entity.effects

const val DEFAULT_TIME_FOR_ONE_PIXEL = 10000L
const val DEFAULT_PRICE_FOR_ONE_PIXEL = 0.5

fun timeMillisForOnePixel(effectsManager: EffectsManager): Long {
    return DEFAULT_TIME_FOR_ONE_PIXEL / (effectsManager.getEffectLevel(EffectEnum.SPEED_TRAVELING) + 1)
}

fun priceToTravelWithEffect(effectsManager: EffectsManager, pathSize: Int): Double {
    return pathSize * DEFAULT_PRICE_FOR_ONE_PIXEL / (effectsManager.getEffectLevel(EffectEnum.COST_TRAVELING) + 1)
}

enum class EffectEnum(val displayName: String, val parentEffectEnum: EffectEnum? = null) {
    SPEED_TRAVELING("Fusée hypersonique"),
    COST_TRAVELING("Corruption de la guilde des lapins de voyage"),
    RABBIT_ADVERTISEMENT("Champ de vision envahi par la pub (RP)");
}