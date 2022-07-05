package io.github.alexiscomete.lapinousecond.useful

class ProgressionBar(
    private var indicator: String,
    var size: Int,
    private var fill: String,
    private var fillSize: Int,
    private var nFill: String,
    private var nSize: Int,
    private var max: Double,
    private var value: Double,
    numberChars: Int
) {
    private var numberChars: Int

    init {
        this.numberChars = numberChars - size
    }

    val bar: String
        get() {
            val min = partMin
            val minChars = min / fillSize
            val max = numberChars - min
            val maxChars = max / nSize
            val r = "`" + before(minChars) + indicator + after(maxChars) + "`"
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
        get() = (value / max * numberChars).toInt()
}