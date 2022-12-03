package io.github.alexiscomete.lapinousecond.view.ui

class DialogueReader(
    private val dialogue: Dialogue,
    private val ui: PlayerUI,
    private val onEnd: () -> Unit,
    private val maxCharOnOnePart: Int = -1
) {

}