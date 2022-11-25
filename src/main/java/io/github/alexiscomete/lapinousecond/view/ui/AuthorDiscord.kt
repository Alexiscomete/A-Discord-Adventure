package io.github.alexiscomete.lapinousecond.view.ui

import org.javacord.api.entity.user.User
import java.awt.image.BufferedImage

class AuthorDiscord(user: User, name: String? = null, avatar: String? = null, image: BufferedImage? = null) : Author {

    private var playerName: String
    private var playerAvatar: String? = null
    private var playerImage: BufferedImage? = null

    init {
        playerName = name ?: user.name
        if (image != null) {
            playerImage = image
        } else if (avatar != null) {
            playerAvatar = avatar
        } else if (user.avatar != null) {
            playerAvatar = user.avatar.url.toString()
        }
    }

    override fun getName(): String {
        return playerName
    }

    override fun getLinkAvatar(): String {
        return playerAvatar ?: throw IllegalStateException("No avatar link")
    }

    override fun hasLinkAvatar(): Boolean {
        return playerAvatar != null
    }

    override fun setName(name: String): Author {
        playerName = name
        return this
    }

    override fun setAvatar(avatar: String): Author {
        playerAvatar = avatar
        return this
    }

    override fun setAvatar(avatar: BufferedImage): Author {
        playerImage = avatar
        return this
    }

    override fun getImageAvatar(): BufferedImage {
        return playerImage ?: throw IllegalStateException("No avatar image")
    }

    override fun hasImageAvatar(): Boolean {
        return playerImage != null
    }

}