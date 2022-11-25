package io.github.alexiscomete.lapinousecond.view.ui

import java.awt.image.BufferedImage

interface Author {
    fun getName(): String
    fun getLinkAvatar(): String?
    fun hasLinkAvatar(): Boolean
    fun setName(name: String): Author
    fun setAvatar(avatar: String): Author
    fun getImageAvatar(): BufferedImage?
    fun setAvatar(avatar: BufferedImage): Author
    fun hasImageAvatar(): Boolean
}
