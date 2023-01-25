package io.github.alexiscomete.lapinousecond.entity.effects

open class TimedEffect(type: EffectEnum, level: Int, val duration: Long) : Effect(type, level) {
    var remainingDuration = duration
    var beginTime = System.currentTimeMillis()

    open fun tick() {
        if (remainingDuration > 0) {
            remainingDuration--
        }
    }

    open fun update() {
        val currentTime = System.currentTimeMillis()
        val timeElapsed = currentTime - beginTime
        remainingDuration -= timeElapsed
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