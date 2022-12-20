package io.github.alexiscomete.lapinousecond.worlds

enum class Zooms(
    val zoom: Int,
    val before: Zooms?
) {
    ZOOM_OUT(1, null),
    ZOOM_ZONES(7, ZOOM_OUT),
    ZOOM_ZONES_DETAILS(5, ZOOM_ZONES),
    ZOOM_IN(20, ZOOM_ZONES_DETAILS);

    var next: Zooms? = null

    init {
        if (before != null) {
            before.next = this
        }
    }

    fun zoomInTo(zooms: Zooms, x: Int, y: Int): Pair<Int, Int> {
        if (zooms == this) {
            return Pair(x * zoom, y * zoom)
        }
        return next!!.zoomInTo(zooms, x * zoom, y * zoom)
    }

    fun zoomOutTo(zooms: Zooms, x: Int, y: Int): Pair<Int, Int> {
        if (zooms == this) {
            return Pair(x / zoom, y / zoom)
        }
        return before!!.zoomOutTo(zooms, x / zoom, y / zoom)
    }

    fun zoomOutTo(zooms: Zooms, x: Double, y: Double): Pair<Double, Double> {
        if (zooms == this) {
            return Pair(x / zoom, y / zoom)
        }
        return before!!.zoomOutTo(zooms, x / zoom, y / zoom)
    }
}