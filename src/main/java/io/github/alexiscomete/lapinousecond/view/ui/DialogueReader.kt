package io.github.alexiscomete.lapinousecond.view.ui

class DialogueReader(
    val dialogue: Dialogue,
    private val onEnd: () -> Unit,
    private val maxCharOnOnePart: Int = -1,
    private val maxParts: Int = -1
) {
    private var currentParts = run {
        val parts = mutableListOf<DialoguePart>()
        var currentPart = dialogue.getFirst()
        var currentWeight = 0
        while (currentPart != null && (maxCharOnOnePart == -1 || currentWeight + currentPart.weight() < maxCharOnOnePart) && (maxParts == -1 || parts.size < maxParts)) {
            parts.add(currentPart)
            currentWeight += currentPart.weight()
            currentPart = currentPart.next()
        }
        parts
    }
    private var currentMin: DialoguePart? = dialogue.getFirst()
    private var currentMax: DialoguePart? = currentParts.lastOrNull()

    fun hasNext(): Boolean {
        if (currentMax == null) return false
        return currentMax?.next() != null
    }

    fun hasPrevious(): Boolean {
        if (currentMin == null) return false
        return currentMin?.before() != null
    }

    fun hasContent(): Boolean {
        return currentParts.isNotEmpty()
    }

    fun next(): DialogueReader {
        if (currentMax == null) return this
        val next = currentMax?.next()
        if (next == null) {
            onEnd()
            return this
        }
        currentMin = next
        currentParts = run {
            val parts = mutableListOf<DialoguePart>()
            var currentPart = currentMin
            var currentWeight = 0
            while (currentPart != null && (maxCharOnOnePart == -1 || currentWeight + currentPart.weight() < maxCharOnOnePart) && (maxParts == -1 || parts.size < maxParts)) {
                parts.add(currentPart)
                currentWeight += currentPart.weight()
                currentPart = currentPart.next()
            }
            parts
        }
        currentMax = currentParts.lastOrNull()
        return this
    }

    fun previous(): DialogueReader {
        if (currentMin == null) return this
        val before = currentMin!!.before() ?: return this
        currentMax = before
        currentParts = run {
            val parts = mutableListOf<DialoguePart>()
            var currentPart = currentMax
            var currentWeight = 0
            while (currentPart != null && (maxCharOnOnePart == -1 || currentWeight + currentPart.weight() < maxCharOnOnePart) && (maxParts == -1 || parts.size < maxParts)) {
                parts.add(currentPart)
                currentWeight += currentPart.weight()
                currentPart = currentPart.before()
            }
            parts
        }
        currentMin = currentParts.firstOrNull()
        return this
    }

    fun getCurrentParts(): List<DialoguePart> {
        return currentParts
    }
}