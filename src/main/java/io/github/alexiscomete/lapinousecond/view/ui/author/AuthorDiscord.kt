package io.github.alexiscomete.lapinousecond.view.ui.author

import org.javacord.api.entity.user.User
import java.awt.image.BufferedImage

class AuthorDiscord(
    user: User,
    override var linkAvatar: String? = null,
    override var imageAvatar: BufferedImage? = null,
    override var name: String = run { user.name }
) : Author {

    init {
        if (imageAvatar != null) {
            linkAvatar = null
        } else if (linkAvatar == null && user.avatar != null) {
            linkAvatar = user.avatar.url.toString()
        }
    }

    override fun hasLinkAvatar(): Boolean {
        return linkAvatar != null
    }

    override fun hasImageAvatar(): Boolean {
        return imageAvatar != null
    }
}