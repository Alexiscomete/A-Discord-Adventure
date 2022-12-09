package io.github.alexiscomete.lapinousecond.view.ui.dialogue

class DialogueBase(
    private var title: String,
    private var first: DialoguePart?,
    private var last: DialoguePart?
) : Dialogue {

    override fun add(part: DialoguePart): Dialogue {
        if (first == null) {
            first = part
            last = part
        } else {
            last?.setNext(part)
            part.setBefore(last)
            last = part
        }
        return this
    }

    override fun removeLast(): Dialogue {
        if (first == last) {
            first = null
            last = null
        } else {
            last = last?.before()
            last?.setNext(null)
        }
        return this
    }

    override fun remove(part: DialoguePart): Dialogue {
        var current = first
        while (current != null) {
            if (current == part) {
                if (current == first && current == last) {
                    first = null
                    last = null
                } else if (current == first) {
                    first = current.next()
                    first?.setBefore(null)
                } else if (current == last) {
                    last = current.before()
                    last?.setNext(null)
                } else {
                    current.before()?.setNext(current.next())
                    current.next()?.setBefore(current.before())
                }
                return this
            }
            current = current.next()
        }
        return this
    }

    override fun removeFirst(): Dialogue {
        first = first?.next()
        first?.setBefore(null)
        return this
    }

    override fun clear(): Dialogue {
        first = null
        last = null
        return this
    }

    override fun getParts(): List<DialoguePart> {
        val parts = mutableListOf<DialoguePart>()
        var current = first
        while (current != null) {
            parts.add(current)
            current = current.next()
        }
        return parts
    }

    override fun getFirst(): DialoguePart? {
        return first
    }

    override fun getLast(): DialoguePart? {
        return last
    }

    override fun getPart(index: Int): DialoguePart? {
        var current = first
        var i = 0
        while (current != null) {
            if (i == index) {
                return current
            }
            current = current.next()
            i++
        }
        return null
    }

    override fun removePart(index: Int): Dialogue {
        var current = first
        var i = 0
        while (current != null) {
            if (i == index) {
                if (current == first && current == last) {
                    first = null
                    last = null
                } else if (current == first) {
                    first = current.next()
                    first?.setBefore(null)
                } else if (current == last) {
                    last = current.before()
                    last?.setNext(null)
                } else {
                    current.before()?.setNext(current.next())
                    current.next()?.setBefore(current.before())
                }
                return this
            }
            current = current.next()
            i++
        }
        return this
    }

    override fun insertPart(index: Int, part: DialoguePart): Dialogue {
        var current = first
        var i = 0
        while (current != null) {
            if (i == index) {
                if (current == first) {
                    first = part
                    part.setNext(current)
                    current.setBefore(part)
                } else {
                    current.before()?.setNext(part)
                    part.setBefore(current.before())
                    part.setNext(current)
                    current.setBefore(part)
                }
                return this
            }
            current = current.next()
            i++
        }
        return this
    }

    override fun setTitle(title: String): Dialogue {
        this.title = title
        return this
    }

    override fun getTitle(): String {
        return title
    }
}