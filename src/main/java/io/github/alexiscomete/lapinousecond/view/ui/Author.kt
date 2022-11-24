package io.github.alexiscomete.lapinousecond.view.ui

interface Author {
    fun getName(): String
    fun getAvatar(): String
    fun hasAvatar(): Boolean
    fun setName(name: String): Author
    fun setAvatar(avatar: String): Author

}
