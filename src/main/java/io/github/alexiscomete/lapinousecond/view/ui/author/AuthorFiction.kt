package io.github.alexiscomete.lapinousecond.view.ui.author

import io.github.alexiscomete.lapinousecond.view.ui.author.Author
import java.awt.image.BufferedImage

class AuthorFiction(
    private var playerName: String,
    var avatar: String? = null,
    var image: BufferedImage? = null
) : Author {
    override fun getName(): String {
        return playerName
    }

    override fun getLinkAvatar(): String {
        return avatar ?: throw IllegalStateException("No avatar link")
    }

    override fun hasLinkAvatar(): Boolean {
        return avatar != null
    }

    override fun setName(name: String): Author {
        playerName = name
        return this
    }

    override fun setAvatar(avatar: String): Author {
        this.avatar = avatar
        return this
    }

    override fun setAvatar(avatar: BufferedImage): Author {
        image = avatar
        return this
    }

    override fun getImageAvatar(): BufferedImage {
        return image ?: throw IllegalStateException("No avatar image")
    }

    override fun hasImageAvatar(): Boolean {
        return image != null
    }
}