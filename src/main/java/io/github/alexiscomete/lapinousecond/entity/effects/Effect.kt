package io.github.alexiscomete.lapinousecond.entity.effects

open class Effect(type: EffectEnum, level: Int) {
    open fun canBeRemovedAutomatically(): Boolean = false
}