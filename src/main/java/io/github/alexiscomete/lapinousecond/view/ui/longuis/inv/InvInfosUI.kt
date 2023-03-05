package io.github.alexiscomete.lapinousecond.view.ui.longuis.inv

import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.SimpleInteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.StaticUI
import java.awt.image.BufferedImage

class InvInfosUI(playerUI: PlayerUI) : StaticUI(
    interactionUICustomUILists = listOf(
        listOf(
            SimpleInteractionUICustomUI(
                "inv_resources",
                "Ressources",
                "Ouvrir l'inventaire des ressources",
                InteractionStyle.NORMAL,
                {
                    playerUI.setLongCustomUI(InvResourcesUI(playerUI));
                    return@SimpleInteractionUICustomUI null
                },
                null
            ),
            SimpleInteractionUICustomUI(
                "inv_items",
                "Items",
                "Ouvrir l'inventaire des items",
                InteractionStyle.NORMAL,
                {
                    playerUI.setLongCustomUI(InvItemsUI(playerUI));
                    return@SimpleInteractionUICustomUI null
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
        val player = currentUI.getPlayer()
        return listOf(
            Pair("Position", player.positionToString()),
            Pair("Niveau", player.level.toString()),
            Pair("Pixel", """
     Compte sur le bot de Sylicium : ${if (player["has_account"] == "1") "oui" else "non"}
     Vérification : ${if (player["is_verify"] == "1") "oui" else "non"}
     Pixel : ${if (player["x"] == "" || player["x"].toInt() == -1) "pixel inconnu" else "[" + player["x"] + ":" + player["y"] + "]"}
     """.trimIndent())
        )
    }

    override fun getLinkedImage(): String {
        return "https://cdn.discordapp.com/attachments/854322477152337920/924612939879702588/unknown.png"
    }

    override fun getBufferedImage(): BufferedImage? {
        return null
    }

    override fun getUnderString(): String? {
        return null
    }
}