package io.github.alexiscomete.lapinousecond.entity.effects

open class Effect(val type: EffectEnum, val level: Int) {
    open fun canBeRemovedAutomatically(): Boolean = false
}