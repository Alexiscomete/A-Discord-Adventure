package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import java.awt.image.BufferedImage

class ResultUI(
    playerUI: PlayerUI,
    private val title: String?,
    private val description: String?,
    private val linkedImage: String?,
    private val buffuredImage: BufferedImage?,
    private val underString: String?
) : StaticUI(
    listOf(), playerUI,
) {
    override fun getTitle() = title

    override fun getDescription() = description

    override fun getFields() = null

    override fun getLinkedImage() = linkedImage

    override fun getBufferedImage() = buffuredImage

    override fun getUnderString() = underString
}