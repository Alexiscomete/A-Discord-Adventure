package io.github.alexiscomete.lapinousecond.view.ui.dialogue

import io.github.alexiscomete.lapinousecond.view.ui.author.Author

class PartOfDialogue(
    private var before: DialoguePart?,
    private var after: DialoguePart?,
    private var author: Author,
    private var content: String
) : DialoguePart {
    override fun next(): DialoguePart? {
        return after
    }

    override fun isLast(): Boolean {
        return after == null
    }

    override fun getContent(): String {
        return content
    }

    override fun before(): DialoguePart? {
        return before
    }

    override fun isFirst(): Boolean {
        return before == null
    }

    override fun getAuthor(): Author {
        return author
    }

    override fun setAuthor(author: Author): DialoguePart {
        this.author = author
        return this
    }

    override fun setContent(content: String): DialoguePart {
        this.content = content
        return this
    }

    override fun setBefore(before: DialoguePart?): DialoguePart {
        this.before = before
        return this
    }

    override fun setNext(next: DialoguePart?): DialoguePart {
        this.after = next
        return this
    }

    override fun weight(): Int {
        return content.length + author.name.length
    }
}