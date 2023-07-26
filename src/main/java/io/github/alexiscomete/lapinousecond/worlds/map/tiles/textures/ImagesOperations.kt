package io.github.alexiscomete.lapinousecond.worlds.map.tiles.textures

import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage

/**
 * Mirror the image horizontally. WARNING: this method is executed every time it is called.
 */
fun BufferedImage.mirrorImage(): BufferedImage {
    val transform = AffineTransform.getScaleInstance(-1.0, 1.0) // Miroir horizontal
    transform.translate(-width.toDouble(), 0.0) // Ajustement des coordonnées

    val op = AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
    return op.filter(this, null)
}

/**
 * WARNING: this method is executed every time it is called.
 */
fun BufferedImage.mirrorImageVertically(): BufferedImage {
    val transform = AffineTransform.getScaleInstance(1.0, -1.0) // Miroir vertical
    transform.translate(0.0, -height.toDouble()) // Ajustement des coordonnées

    val op = AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
    return op.filter(this, null)
}