
import io.github.alexiscomete.lapinousecond.entity.xp.ComputeLevel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class UnitTestLevels {

    @Test
    fun testXpForLevel() {
        val level = ComputeLevel()
        assertEquals(0.0, level.xpForLevel(0))
        assertEquals(2.5, level.xpForLevel(1))
        assertEquals(7.5, level.xpForLevel(2))
        assertEquals(15.0, level.xpForLevel(3))
        assertEquals(25.0, level.xpForLevel(4))
    }

    @Test
    fun testXpForLevelWithLast() {
        val level = ComputeLevel()
        assertEquals(0.0, level.xpForLevel(0, 0.0))
        assertEquals(2.5, level.xpForLevel(1, 0.0))
        assertEquals(7.5, level.xpForLevel(2, 2.5))
        assertEquals(15.0, level.xpForLevel(3, 7.5))
        assertEquals(25.0, level.xpForLevel(4, 15.0))
        assertEquals(50.0, level.xpForLevel(10, 25.0))
    }

    @Test
    fun testLevelForXp() {
        val level = ComputeLevel()
        assertEquals(0, level.levelForXp(0.0))
        assertEquals(1, level.levelForXp(2.5))
        assertEquals(1, level.levelForXp(3.0))
        assertEquals(2, level.levelForXp(7.5))
        assertEquals(2, level.levelForXp(8.0))
        assertEquals(3, level.levelForXp(15.0))
        assertEquals(3, level.levelForXp(20.0))
        assertEquals(3, level.levelForXp(24.9))
        assertEquals(4, level.levelForXp(25.0))
        assertEquals(4, level.levelForXp(25.1))
        assertEquals(4, level.levelForXp(30.0))
    }
}
