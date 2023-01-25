package io.github.alexiscomete.lapinousecond.entity.effects

class RepeatedTimedEffect(type: EffectEnum, level: Int, duration: Long, val timeBetween: Long,  var numberOfRepeats: Int) : TimedEffect(type, level, duration) {
    var endTime = System.currentTimeMillis() + duration
    var remainingTimeBetween = timeBetween

    override fun tick() {
        if (remainingDuration > 0) {
            super.tick()
        } else {
            if (remainingTimeBetween > 0) {
                remainingTimeBetween--
            } else {
                start()
            }
        }
    }
}