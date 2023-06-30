package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import java.awt.image.BufferedImage

class ResultUI(
    playerUI: PlayerUI,
    val title: String?,
    val description: String?,
    private val linkedImage: String?,
    private val buffuredImage: BufferedImage?,
    private val underString: String?
) : StaticUI(
    listOf(), playerUI,
) {
    override fun getTitle(): String? {
        return title
    }

    override fun getDescription(): String? {
        return description
    }

    override fun getFields(): List<Pair<String, String>>? {
        return null
    }

    override fun getLinkedImage(): String? {
        return linkedImage
    }

    override fun getBufferedImage(): BufferedImage? {
        return buffuredImage
    }

    override fun getUnderString(): String? {
        return underString
    }
}