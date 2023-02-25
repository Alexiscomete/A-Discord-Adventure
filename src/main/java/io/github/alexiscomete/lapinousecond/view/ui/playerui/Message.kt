package io.github.alexiscomete.lapinousecond.view.ui.playerui

data class Message(
    var content: String,
    var title: String? = null
) {
    fun weight(): Int {
        return content.length + (title?.length ?: 0)
    }
}