package io.github.alexiscomete.lapinousecond

/**
 * TODO: ce sera plus tard un jeu
 */
class Territory {
    fun getValue(time: Long, numberOfBattles: Int): Int {
        return getValueTime(time) * getValueBattles(numberOfBattles)
    }

    private fun getValueTime(time: Long): Int {
        val minutes = time / 60000
        return if (minutes > 5) {
            if (minutes > 15) {
                if (minutes > 40) {
                    if (minutes > 60) {
                        if (minutes > 90) {
                            if (minutes > 120) {
                                if (minutes > 200) {
                                    if (minutes > 260) {
                                        if (minutes > 360) {
                                            if (minutes > 460) {
                                                if (minutes > 560) {
                                                    if (minutes > 660) {
                                                        if (minutes > 800) {
                                                            if (minutes > 1000) {
                                                                if (minutes > 2000) {
                                                                    if (minutes > 3000) {
                                                                        if (minutes > 4000) {
                                                                            if (minutes > 5000) {
                                                                                minutes.toInt()
                                                                            } else 1000
                                                                        } else 600
                                                                    } else 400
                                                                } else 200
                                                            } else 100
                                                        } else 80
                                                    } else 69
                                                } else 56
                                            } else 44
                                        } else 35
                                    } else 27
                                } else 22
                            } else 15
                        } else 10
                    } else 7
                } else 5
            } else 3
        } else 1
    }

    private fun getValueBattles(numberOfBattles: Int): Int {
        return when (numberOfBattles) {
            0 -> 1
            1 -> 20
            2 -> 35
            3 -> 50
            4 -> 75
            5 -> 100
            6 -> 150
            7 -> 210
            8 -> 280
            9 -> 400
            10 -> 700
            else -> 800 + 90 * (numberOfBattles - 8)
        }
    }
}