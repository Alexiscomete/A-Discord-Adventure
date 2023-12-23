import io.github.alexiscomete.lapinousecond.entity.xp.ComputeLevel
import io.github.alexiscomete.lapinousecond.entity.xp.roundCustom
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

    @Test
    fun testTotalXpForNextLevel() {
        val level = ComputeLevel()
        assertEquals(2.5, level.totalXpForNextLevel(0.0))
        assertEquals(7.5, level.totalXpForNextLevel(2.5))
        assertEquals(15.0, level.totalXpForNextLevel(7.5))
        assertEquals(25.0, level.totalXpForNextLevel(15.0))
        assertEquals(37.5, level.totalXpForNextLevel(25.0))
        // -- intermediate values --
        assertEquals(7.5, level.totalXpForNextLevel(2.6))
        assertEquals(7.5, level.totalXpForNextLevel(2.7))
        assertEquals(7.5, level.totalXpForNextLevel(7.4))
        assertEquals(2.5, level.totalXpForNextLevel(0.5))
        assertEquals(2.5, level.totalXpForNextLevel(0.6))
        assertEquals(2.5, level.totalXpForNextLevel(0.7))
        assertEquals(15.0, level.totalXpForNextLevel(7.8))
        assertEquals(15.0, level.totalXpForNextLevel(7.9))
        assertEquals(15.0, level.totalXpForNextLevel(10.0))
        assertEquals(25.0, level.totalXpForNextLevel(15.1))
        assertEquals(25.0, level.totalXpForNextLevel(15.2))
        assertEquals(25.0, level.totalXpForNextLevel(20.0))
        assertEquals(37.5, level.totalXpForNextLevel(25.1))
        assertEquals(37.5, level.totalXpForNextLevel(25.2))
        assertEquals(37.5, level.totalXpForNextLevel(30.0))
    }

    @Test
    fun testXpForNextLevel() {
        val level = ComputeLevel()
        assertEquals(2.5, level.xpForNextLevel(0.0))
        assertEquals(5.0, level.xpForNextLevel(2.5))
        assertEquals(7.5, level.xpForNextLevel(7.5))
        assertEquals(10.0, level.xpForNextLevel(15.0))
        assertEquals(12.5, level.xpForNextLevel(25.0))
        // -- intermediate values --
        assertEquals(4.9, level.xpForNextLevel(2.6))
        assertEquals(4.8, level.xpForNextLevel(2.7))
        assertEquals(2.0, level.xpForNextLevel(0.5))
        assertEquals(1.9, level.xpForNextLevel(0.6))
        assertEquals(1.8, level.xpForNextLevel(0.7))
        assertEquals(7.2, level.xpForNextLevel(7.8))
        assertEquals(7.1, level.xpForNextLevel(7.9))
        assertEquals(5.0, level.xpForNextLevel(10.0))
        assertEquals(9.9, level.xpForNextLevel(15.1))
        assertEquals(9.8, level.xpForNextLevel(15.2))
        assertEquals(5.0, level.xpForNextLevel(20.0))
        assertEquals(12.3, level.xpForNextLevel(25.2))
        assertEquals(7.5, level.xpForNextLevel(30.0))
    }

    @Test
    fun testTotalXpForCurrentLevel() {
        val level = ComputeLevel()
        assertEquals(0.0, level.totalXpForCurrentLevel(0.0))
        assertEquals(2.5, level.totalXpForCurrentLevel(2.5))
        assertEquals(7.5, level.totalXpForCurrentLevel(7.5))
        assertEquals(15.0, level.totalXpForCurrentLevel(15.0))
        assertEquals(25.0, level.totalXpForCurrentLevel(25.0))
        // -- intermediate values --
        assertEquals(0.0, level.totalXpForCurrentLevel(0.5))
        assertEquals(0.0, level.totalXpForCurrentLevel(0.6))
        assertEquals(0.0, level.totalXpForCurrentLevel(0.7))
        assertEquals(2.5, level.totalXpForCurrentLevel(2.6))
        assertEquals(2.5, level.totalXpForCurrentLevel(2.7))
        assertEquals(2.5, level.totalXpForCurrentLevel(7.4))
        assertEquals(7.5, level.totalXpForCurrentLevel(7.8))
        assertEquals(7.5, level.totalXpForCurrentLevel(7.9))
        assertEquals(7.5, level.totalXpForCurrentLevel(10.0))
        assertEquals(15.0, level.totalXpForCurrentLevel(15.1))
        assertEquals(15.0, level.totalXpForCurrentLevel(15.2))
        assertEquals(15.0, level.totalXpForCurrentLevel(20.0))
        assertEquals(25.0, level.totalXpForCurrentLevel(25.1))
        assertEquals(25.0, level.totalXpForCurrentLevel(25.2))
        assertEquals(25.0, level.totalXpForCurrentLevel(30.0))
    }

    @Test
    fun testXpInCurrentLevel() {
        val level = ComputeLevel()
        assertEquals(0.0, level.xpInCurrentLevel(0.0))
        assertEquals(0.0, level.xpInCurrentLevel(2.5))
        assertEquals(0.0, level.xpInCurrentLevel(7.5))
        assertEquals(0.0, level.xpInCurrentLevel(15.0))
        assertEquals(0.0, level.xpInCurrentLevel(25.0))
        // -- intermediate values --
        assertEquals(0.5, level.xpInCurrentLevel(0.5))
        assertEquals(0.6, level.xpInCurrentLevel(0.6))
        assertEquals(0.7, level.xpInCurrentLevel(0.7).roundCustom(1))
        assertEquals(0.1, level.xpInCurrentLevel(2.6).roundCustom(1))
        assertEquals(0.2, level.xpInCurrentLevel(2.7).roundCustom(1))
        assertEquals(4.9, level.xpInCurrentLevel(7.4))
        assertEquals(0.3, level.xpInCurrentLevel(7.8).roundCustom(1))
    }
}
