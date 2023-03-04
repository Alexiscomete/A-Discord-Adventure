package io.github.alexiscomete.lapinousecond.view.ui.longuis.inv

import io.github.alexiscomete.lapinousecond.entity.resources.Resource
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.SimpleInteractionUICustomUI
import io.github.alexiscomete.lapinousecond.view.ui.longuis.StaticUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.Message
import io.github.alexiscomete.lapinousecond.view.ui.playerui.PlayerUI
import java.awt.image.BufferedImage

class InvResourcesUI(playerUI: PlayerUI) : StaticUI(
    interactionUICustomUILists = listOf(
        listOf(
            SimpleInteractionUICustomUI(
                "inv_infos",
                "Informations",
                "Ouvrir l'inventaire des informations",
                InteractionStyle.NORMAL,
                {
                    playerUI.setLongCustomUI(InvInfosUI(playerUI));
                    return@SimpleInteractionUICustomUI null
                },
                null
            )
        )
    ),
    playerUI
) {
    override fun getTitle(): String {
        val tuto = currentUI.getPlayer()["tuto"].toInt()
        if (tuto == 1) {
            currentUI.addMessage(
                Message(
                    "> (Aurimezi) : Vide ?! Comment tu as fait pour acheter un inventaire sans argent ?\n" +
                            "\n" +
                            "> (Vous) : Je ne me souviens de rien. Depuis quand un inventaire parle ?\n" +
                            "\n" +
                            "> (Aurimezi) : Bon je crois que je vais devoir un peu te guider ...\n" +
                            "\n" +
                            "Utilisez `/work all`\n"
                )
            )
            currentUI.getPlayer()["tuto"] = "3"
        } else if (tuto == 4) {
            currentUI.addMessage(
                Message(
                    "> (Aurimezi) : Ca fait du bien de ne pas se sentir vide ... maintenant achetons ou vendons des ressources. Regardons ce qu'on a au magasin\n" +
                            "\n" +
                            "Utilisez `/shop list`\n"
                )
            )
            currentUI.getPlayer()["tuto"] = "5"
        }
        return "Inventaire : ressources et argent"
    }

    override fun getDescription(): String? {
        return null
    }

    override fun getFields(): List<Pair<String, String>> {
        val re = StringBuilder().append("Cliquez sur une resource (emoji) pour voir son nom\n")
        for (reM in currentUI.getPlayer().resourceManagers.values) {
            re
                .append(reM.resource.show)
                .append(" ")
                .append(reM.quantity)
                .append("\n")
        }
        return listOf(
            Pair("Rabbitcoins", currentUI.getPlayer()["bal"] + Resource.RABBIT_COIN.show),
            Pair("Ressources", re.toString())
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