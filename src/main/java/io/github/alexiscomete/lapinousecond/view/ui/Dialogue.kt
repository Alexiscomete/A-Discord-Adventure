package io.github.alexiscomete.lapinousecond.view.ui

interface Dialogue {
    fun add(part: DialoguePart): Dialogue
    fun remove(part: DialoguePart): Dialogue
    fun removeLast(): Dialogue
    fun removeFirst(): Dialogue
    fun clear(): Dialogue
    fun getParts(): List<DialoguePart>
    fun getFirst(): DialoguePart?
    fun getLast(): DialoguePart?
    fun getPart(index: Int): DialoguePart?
    fun removePart(index: Int): Dialogue
    fun addPart(index: Int, part: DialoguePart): Dialogue
}
