package io.github.alexiscomete.lapinousecond.useful

class ProgressionBar(
    private var indicator: String,
    private var fill: String,
    private var nFill: String,
    var maxValue: Double,
    var value: Double,
    var numberChars: Int
) {

    val bar: String
        get() {
            //if (value < 0) {
            //    value = 0.0
            //}
            //if (maxValue < 0) {
            //    maxValue = 1.0
            //}
            val min = partMin
            return "`${before(min)}$indicator${after(numberChars - min)}`"
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