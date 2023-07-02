package io.github.alexiscomete.lapinousecond.worlds

enum class Zooms(
    val zoom: Int,
    val before: Zooms?
) {
    ZOOM_OUT(1, null),
    ZOOM_ZONES(7, ZOOM_OUT),
    ZOOM_ZONES_DETAILS(20, ZOOM_ZONES),
    ZOOM_IN(50, ZOOM_ZONES_DETAILS);

    var next: Zooms? = null

    init {
        if (before != null) {
            before.next = this
        }
    }

    fun zoomInTo(zooms: Zooms, x: Int, y: Int, starting: Boolean = true): Pair<Int, Int> {
        if (zooms == this) {
            return if (starting) {
                Pair(x, y)
            } else {
                Pair(x * zoom, y * zoom)
            }
        }
        if (starting) {
            return next?.zoomInTo(zooms, x, y, false) ?: throw Exception("Zooms not found")
        }
        return next?.zoomInTo(zooms, x * zoom, y * zoom, false) ?: throw Exception("Zooms not found")
    }

    fun zoomOutTo(zooms: Zooms, x: Int, y: Int): Pair<Int, Int> {
        if (zooms == this) {
            return Pair(x, y)
        }
        return before?.zoomOutTo(zooms, x / zoom, y / zoom) ?: throw Exception("Zooms not found")
    }

    fun zoomOutTo(zooms: Zooms, x: Double, y: Double): Pair<Double, Double> {
        if (zooms == this) {
            return Pair(x, y)
        }
        return before?.zoomOutTo(zooms, x / zoom, y / zoom) ?: throw Exception("Zooms not found")
    }
}