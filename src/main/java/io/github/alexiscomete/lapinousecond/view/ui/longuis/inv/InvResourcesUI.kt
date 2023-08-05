package io.github.alexiscomete.lapinousecond.view.ui.longuis.inv

import io.github.alexiscomete.lapinousecond.data.TutoSteps
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
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
                    playerUI.setLongCustomUI(InvInfosUI(playerUI))
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
        val tuto = currentUI.getPlayer()["tuto"]
        if (tuto == TutoSteps.STEP_INVENTORY_EMPTY.number) {
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
            currentUI.getPlayer()["tuto"] = TutoSteps.STEP_INVENTORY_EMPTY.nextStepNum
        } else if (tuto == TutoSteps.STEP_INVENTORY_FULL.number) {
            currentUI.addMessage(
                Message(
                    "> (Aurimezi) : Ca fait du bien de ne pas se sentir vide ... maintenant achetons ou vendons des ressources. Regardons ce qu'on a au magasin\n" +
                            "\n" +
                            "Utilisez `/shop list`\n"
                )
            )
            currentUI.getPlayer()["tuto"] = TutoSteps.STEP_INVENTORY_FULL.nextStepNum
        }
        return "Inventaire : ressources et argent"
    }

    override fun getDescription(): String? {
        return null
    }

    override fun getFields(): List<Pair<String, String>> {
        val re = StringBuilder().append("Cliquez sur une resource (emoji) pour voir son nom\n")
        for (reM in currentUI.getPlayerManager().playerOwnerManager.resourceManagers.values) {
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
        return INV_IMAGE_URL
    }

    override fun getBufferedImage(): BufferedImage? {
        return null
    }

    override fun getUnderString(): String? {
        return null
    }
}