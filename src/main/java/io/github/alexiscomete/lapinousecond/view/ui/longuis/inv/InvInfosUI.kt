package io.github.alexiscomete.lapinousecond.view.ui.longuis.inv

import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.SimpleInteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.StaticUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import java.awt.image.BufferedImage

const val INV_IMAGE_URL = "https://cdn.discordapp.com/attachments/854322477152337920/924612939879702588/unknown.png"

class InvInfosUI(playerUI: PlayerUI) : StaticUI(
    interactionUICustomUILists = listOf(
        listOf(
            SimpleInteractionUICustomUI(
                "inv_resources",
                "Ressources",
                "Ouvrir l'inventaire des ressources",
                InteractionStyle.NORMAL,
                {
                    playerUI.setLongCustomUI(InvResourcesUI(playerUI))
                    null
                },
                null
            ),
            SimpleInteractionUICustomUI(
                "inv_items",
                "Items",
                "Ouvrir l'inventaire des items",
                InteractionStyle.NORMAL,
                {
                    playerUI.setLongCustomUI(InvItemsUI(playerUI))
                    null
                },
                null
            ),
            SimpleInteractionUICustomUI(
                "inv_effects",
                "Effets",
                "Ouvrir la liste des effets en cours",
                InteractionStyle.NORMAL,
                {
                    playerUI.setLongCustomUI(InvEffectsUI(playerUI))
                    null
                },
                null
            )
        )
    ),
    playerUI
) {
    override fun getTitle(): String {
        return "Informations sur le joueur"
    }

    override fun getDescription(): String {
        return "Serveur actuel : ${if (currentUI.getPlayer()["serv"] == "") "serveur inconnu, utilisez -hub" else currentUI.getPlayer()["serv"]}"
    }

    override fun getFields(): List<Pair<String, String>> {
        val playerManager = currentUI.getPlayerManager()
        return listOf(
            Pair("Position", playerManager.worldManager.entityWorld.positionToString()),
            Pair("Niveau", playerManager.level.toString()),
            Pair("Pixel officiel", "Les pixels d'origine ne sont plus pris en charge pour le moment."),
            Pair("Vie", playerManager.getLifeString()),
        )
    }

    override fun getLinkedImage(): String {
        return INV_IMAGE_URL
    }

    override fun getBufferedImage(): BufferedImage? {
        return null
    }

    override fun getUnderString(): String? {
        return null
    }
}