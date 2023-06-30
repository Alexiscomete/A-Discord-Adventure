package io.github.alexiscomete.lapinousecond.view.ui.longuis

import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.SimpleInteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Question
import java.awt.image.BufferedImage

interface WaitingManager {
    fun estimatedRemainingTimeSeconds(): Int
    fun isFinished(): Boolean
    fun executeAfter(playerUI: PlayerUI): Question?
}

class WaitingUI(
    playerUI: PlayerUI, private val waitingManager: WaitingManager
) : StaticUI(
    listOf(
        listOf(
            SimpleInteractionUICustomUI("waiting_refresh",
                "Actualiser",
                "Actualisez la page pour voir si votre demande a été traitée.",
                InteractionStyle.SECONDARY,
                {
                    if (waitingManager.isFinished()) {
                        waitingManager.executeAfter(playerUI)
                    } else null
                })
        )
    ), playerUI
) {
    override fun getTitle(): String {
        return "Votre demande est en cours de traitement."
    }

    override fun getDescription(): String {
        return "A la fin du temps estimé, cliquez sur le bouton pour actualiser. Temps estimé restant : ${waitingManager.estimatedRemainingTimeSeconds()} secondes."
    }

    override fun getFields(): List<Pair<String, String>>? {
        return null
    }

    override fun getLinkedImage(): String? {
        return null
    }

    override fun getBufferedImage(): BufferedImage? {
        return null
    }

    override fun getUnderString(): String? {
        return null
    }
}