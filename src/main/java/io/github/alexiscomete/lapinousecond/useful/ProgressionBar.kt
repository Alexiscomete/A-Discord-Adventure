package io.github.alexiscomete.lapinousecond.useful

class ProgressionBar(
    private var indicator: String,
    private var fill: String,
    private var nFill: String,
    var max_value: Double,
    var value: Double,
    var numberChars: Int
) {

    val bar: String
        get() {
            val min = partMin
            val max = numberChars - min
            val r = "`" + before(min) + indicator + after(max) + "`"
            println(r)
            return r
        }

    private fun before(n: Int): String {
        return fill.repeat(n)
    }

    private fun after(n: Int): String {
        return nFill.repeat(n)
    }

    private val partMin: Int
        get() = (value / max_value * numberChars).toInt()
}