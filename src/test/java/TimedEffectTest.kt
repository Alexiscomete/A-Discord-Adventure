import io.github.alexiscomete.lapinousecond.entity.effects.EffectEnum
import io.github.alexiscomete.lapinousecond.entity.effects.RepeatedTimedEffect
import io.github.alexiscomete.lapinousecond.entity.effects.TimedEffect
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TimedEffectTest {
    @Test
    fun testTimedEffect() {
        val effect = TimedEffect(EffectEnum.COST_TRAVELING, 2, 3)
        assertEquals(3, effect.remainingDuration)
        effect.tick()
        assertEquals(2, effect.remainingDuration)
        effect.tick()
        assertEquals(1, effect.remainingDuration)
        assertEquals(false, effect.isFinished())
        assertEquals(false, effect.canBeRemovedAutomatically())
        effect.tick()
        assertEquals(0, effect.remainingDuration)
        effect.tick()
        assertEquals(0, effect.remainingDuration)
        assertEquals(true, effect.isFinished())
        assertEquals(true, effect.canBeRemovedAutomatically())

        effect.start()
        assertEquals(3, effect.remainingDuration)
    }

    @Test
    fun testRepeatedTimedEffect() {
        val effect = RepeatedTimedEffect(EffectEnum.COST_TRAVELING, 2, 3, 2, 2)
        repeat(2) {
            assertEquals(3, effect.remainingDuration)
            effect.tick()
            assertEquals(2, effect.remainingDuration)
            effect.tick()
            assertEquals(1, effect.remainingDuration)
            assertEquals(false, effect.isFinished())
            assertEquals(false, effect.canBeRemovedAutomatically())
            effect.tick()
            assertEquals(0, effect.remainingDuration)
            assertEquals(true, effect.isFinished())
            assertEquals(false, effect.canBeRemovedAutomatically())
            effect.tick()
            assertEquals(0, effect.remainingDuration)
        }
        assertEquals(true, effect.canBeRemovedAutomatically())
    }
}