package io.github.alexiscomete.lapinousecond.view.ui.longuis.inv

import io.github.alexiscomete.lapinousecond.view.ui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.StaticUI
import java.awt.image.BufferedImage

class InvResourcesUI(playerUI: PlayerUI) : StaticUI(
    interactionUICustomUILists = listOf(),
    playerUI
) {
    override fun getTitle(): String? {
        TODO("Not yet implemented")
    }

    override fun getDescription(): String? {
        TODO("Not yet implemented")
    }

    override fun getFields(): List<Pair<String, String>>? {
        TODO("Not yet implemented")
    }

    override fun getLinkedImage(): String? {
        return null
    }

    override fun getBufferedImage(): BufferedImage? {
        return null
    }

    override fun getUnderString(): String? {
        TODO("Not yet implemented")
    }
}