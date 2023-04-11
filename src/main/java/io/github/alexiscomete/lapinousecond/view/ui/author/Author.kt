package io.github.alexiscomete.lapinousecond.view.ui.author

import java.awt.image.BufferedImage

interface Author {
    var linkAvatar: String?
    var imageAvatar: BufferedImage?
    var name: String

    fun hasLinkAvatar(): Boolean
    fun hasImageAvatar(): Boolean
}
