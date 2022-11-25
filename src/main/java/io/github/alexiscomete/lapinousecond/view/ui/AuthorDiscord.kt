package io.github.alexiscomete.lapinousecond.view.ui

import org.javacord.api.entity.user.User

class AuthorDiscord(user: User, name: String? = null, avatar: String? = null, image: String? = null) : Author {
    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun getAvatar(): String {
        TODO("Not yet implemented")
    }

    override fun hasAvatar(): Boolean {
        TODO("Not yet implemented")
    }

    override fun setName(name: String): Author {
        TODO("Not yet implemented")
    }

    override fun setAvatar(avatar: String): Author {
        TODO("Not yet implemented")
    }
}