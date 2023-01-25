package io.github.alexiscomete.lapinousecond.entity.effects

enum class EffectEnum(val displayName: String, val parentEffectEnum: EffectEnum? = null) {
    SPEED_TRAVELING("Fusée hypersonique"),
    COST_TRAVELING("Corruption de la guilde des lapins de voyage"),
    RABBIT_ADVERTISEMENT("Champ de vision envahi par la pub (RP)");
}