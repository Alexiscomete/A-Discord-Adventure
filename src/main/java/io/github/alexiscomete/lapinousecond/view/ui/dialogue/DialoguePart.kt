package io.github.alexiscomete.lapinousecond.view.ui.dialogue

import io.github.alexiscomete.lapinousecond.view.ui.author.Author

interface DialoguePart {
    fun next(): DialoguePart?
    fun isLast(): Boolean
    fun getContent(): String
    fun before(): DialoguePart?
    fun isFirst(): Boolean
    fun getAuthor(): Author
    fun setAuthor(author: Author): DialoguePart
    fun setContent(content: String): DialoguePart
    fun setBefore(before: DialoguePart?): DialoguePart
    fun setNext(next: DialoguePart?): DialoguePart
    fun weight(): Int
}