package io.github.alexiscomete.lapinousecond.data.dataclass

data class ProgressionBar(
    private var indicator: String,
    private var fill: String,
    private var nFill: String,
    var maxValue: Double,
    var value: Double,
    var numberChars: Int
) {

    val bar: String
        get() {
            val min = partMin
            return "`${before(min)}$indicator${after(numberChars - min)}`"
        }

    override fun toString(): String {
        return bar
    }

    private fun before(n: Int): String {
        return fill.repeat(n)
    }

    private fun after(n: Int): String {
        return nFill.repeat(n)
    }

    private val partMin: Int
        get() = ((value / maxValue) * numberChars).toInt()
}