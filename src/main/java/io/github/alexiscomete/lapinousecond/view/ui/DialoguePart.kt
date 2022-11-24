package io.github.alexiscomete.lapinousecond.view.ui

interface DialoguePart {
    fun next(): DialoguePart?
    fun isLast(): Boolean
    fun getContent(): String
    fun getTitle(): String
    fun before(): DialoguePart?
    fun isFirst(): Boolean
    fun getAuthor(): Author
    fun setAuthor(author: Author): DialoguePart
    fun setContent(content: String): DialoguePart
    fun setTitle(title: String): DialoguePart
    fun setBefore(before: DialoguePart?): DialoguePart
    fun setNext(next: DialoguePart?): DialoguePart
}