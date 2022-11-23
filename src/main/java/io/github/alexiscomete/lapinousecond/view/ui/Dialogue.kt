package io.github.alexiscomete.lapinousecond.view.ui

interface Dialogue {
    fun next(): Dialogue
    fun isLast(): Boolean
    fun getContent(): String
    fun getTitle(): String
    fun before(): Dialogue
    fun isFirst(): Boolean
    fun getAuthor(): Author
}
