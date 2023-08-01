package io.github.alexiscomete.lapinousecond.entity.effects

class EffectsManager {
    private val effects = ArrayList<Effect>()

    fun getEffectLevel(effect: EffectEnum): Int {
        updateAndRemoveEffects()
        var level = 0
        for (currentEffect in effects) {
            if (currentEffect.type == effect) {
                if (currentEffect is TimedEffect && currentEffect.isFinished()) {
                    continue
                }
                level += currentEffect.level
            }
        }
        return level
    }

    fun getEffectsCopy(): ArrayList<Effect> {
        return ArrayList(effects)
    }

    fun updateAndRemoveEffects() {
        for (effect in effects) {
            if (effect is TimedEffect) {
                effect.update()
                if (effect.canBeRemovedAutomatically()) {
                    effects.remove(effect)
                }
            }
        }
    }

    fun addEffect(effect: Effect) {
        effects.add(effect)
    }

    fun removeEffects(effect: EffectEnum) {
        for (currentEffect in effects) {
            if (currentEffect.type == effect) {
                effects.remove(currentEffect)
            }
        }
    }
}