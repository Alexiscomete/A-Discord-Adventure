package io.github.alexiscomete.lapinousecond.view.ui.author

import java.awt.image.BufferedImage

class AuthorFiction(
    override var linkAvatar: String?,
    override var imageAvatar: BufferedImage?,
    override var name: String
) : Author {
    override fun hasLinkAvatar(): Boolean {
        return linkAvatar != null
    }

    override fun hasImageAvatar(): Boolean {
        return imageAvatar != null
    }
}