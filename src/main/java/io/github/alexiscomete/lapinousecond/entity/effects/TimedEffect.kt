package io.github.alexiscomete.lapinousecond.entity.effects

open class TimedEffect(type: EffectEnum, level: Int, val duration: Long) : Effect(type, level) {
    var remainingDuration = duration
    var beginTime = System.currentTimeMillis()

    open fun tick() {
        remainingDuration--
    }

    open fun update() {
        val currentTime = System.currentTimeMillis()
        remainingDuration -= currentTime - beginTime
        beginTime = currentTime
    }

    fun isFinished(): Boolean {
        return remainingDuration <= 0
    }

    fun start() {
        beginTime = System.currentTimeMillis()
        remainingDuration = duration
    }

    override fun canBeRemovedAutomatically(): Boolean {
        return isFinished()
    }
}