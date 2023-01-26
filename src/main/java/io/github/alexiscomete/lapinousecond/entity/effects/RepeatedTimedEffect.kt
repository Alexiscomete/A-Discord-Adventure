package io.github.alexiscomete.lapinousecond.entity.effects

class RepeatedTimedEffect(type: EffectEnum, level: Int, duration: Long, private val timeBetween: Long,  private var numberOfRepeats: Int) : TimedEffect(type, level, duration) {
    private var remainingTimeBetween = timeBetween

    override fun tick() {
        if (remainingDuration > 0) {
            super.tick()
        } else {
            if (remainingTimeBetween > 0) {
                remainingTimeBetween--
            }
            if (remainingTimeBetween <= 0) {
                numberOfRepeats--
                remainingTimeBetween = timeBetween
                start()
            }
        }
    }

    override fun update() {
        val currentTime = System.currentTimeMillis()
        val timeElapsed = currentTime - beginTime
        remainingDuration -= timeElapsed
        beginTime = currentTime
    }

    override fun canBeRemovedAutomatically(): Boolean {
        return numberOfRepeats <= 0
    }
}