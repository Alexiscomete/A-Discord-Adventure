package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.buttonsManager
import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandInServer
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.worlds.Place
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.map.Pixel
import io.github.alexiscomete.lapinousecond.worlds.places
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color

fun sendPath(messageCreateEvent: MessageCreateEvent, path: ArrayList<Pixel>) {
    val sb = StringBuilder()
    for (pixel in path) {
        sb.append(pixel)
    }
    messageCreateEvent.message.reply(sb.toString())
    val messageBuilder3 = MessageBuilder()
    messageBuilder3.addAttachment(WorldEnum.DIBIMAP.world.drawPath(path), "path.png")
    messageBuilder3.send(messageCreateEvent.channel)
}

class Travel : CommandInServer("Vous permet de voyager vers un serveur", "travel", "travel [server id]") {
    override fun executeC(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {

        // On explique au joueur qu'il manque des arguments : le lieu vers lequel il veut se rendre
        if (args.size < 2 || args[1] == "list") {
            messageCreateEvent.message.reply("Utilisez place links pour voir les possibilités de voyage, si aucune ne vous convient tentez le create_link pour le monde NORMAL")
            return
        }

        // On récupère le monde du joueur
        val world = p.getString("current_world")
        if (world == "") { // si le monde est vide alors on le met à TUTO
            p["world"] = "TUTO"
        }

        travelWorldDibimap(messageCreateEvent, args, p)
    }
    // --------------------------------------------------
    // --------- EN FONCTION DU MONDE -------------------
    // --------------------------------------------------

    // si le joueur est dans le monde Dibimap
    private fun travelWorldDibimap(messageCreateEvent: MessageCreateEvent, args: Array<String>, p: Player) {
        // on récupère le lieu dans le monde Dibimap
        val placeType = p.getString("place_DIBIMAP_type")
        // je regarde si les arguments sont bien présents
        if (args.size < 2) {
            // si non on envoie un message d'erreur car il n'a pas donné le type de voyage
            sendArgs(messageCreateEvent, p)
            return
        }
        // je sépare en 2 cas : si le type est coos ou si le type est un lieu
        if (placeType == "coos") {
            // on récupère les coordonnées
            val x = p.getString("place_DIBIMAP_x").toInt()
            val y = p.getString("place_DIBIMAP_y").toInt()
            // on ne récupère pas le lieu, car on ne connait pas le lieu
            // on sépare le cas où le joueur veut aller dans un lieu et celui où il veut aller sur des coos

            // création du menu
        } else if (placeType == "place") {
            // on récupère le lieu
            val place0 = p.getString("place_DIBIMAP_place")
            // on récupère le lieu dans la base de données
            val place1 = places[place0.toLong()]

            // on sépare le cas où le joueur veut aller dans un lieu et celui où il veut aller sur des coos
            // création du menu

        }
    }

    private fun getCoosDest(messageCreateEvent: MessageCreateEvent): IntArray {
        val coords = messageCreateEvent.message.content.split(" ")
        return intArrayOf(coords[0].toInt(), coords[1].toInt())
    }

    private fun askHow1(
        messageCreateEvent: MessageCreateEvent,
        p: Player,
        path: ArrayList<Pixel>,
        xDest: Int,
        yDest: Int
    ) {
        sendPath(messageCreateEvent, path)
        val timeMillisToTravel = path.size * 10000L
        val priceToTravel = path.size * 0.5
        // on indique le temps de trajet ou le prix que cela peut lui couter en fonction du nombre de pixels de trajet
        messageCreateEvent.message.reply("Vous allez en [$xDest:$yDest] en $timeMillisToTravel millisecondes ou $priceToTravel rb.")
        val id3 = generateUniqueID()
        val id4 = generateUniqueID()
        MessageBuilder().setEmbed(
            EmbedBuilder().setTitle("Vous allez en [$xDest:$yDest]")
                .setDescription("Avec " + path.size + " pixels de trajet en " + timeMillisToTravel + " millisecondes ou " + priceToTravel + " rb.")
                .setImage(
                    WorldEnum.DIBIMAP.world.drawPath(path), "path.png"
                ).setColor(Color.GREEN)
        ).addComponents(
            ActionRow.of(
                Button.success(id3.toString(), "Temps de trajet"),
                Button.success(id4.toString(), "Prix de trajet")
            )
        ).send(messageCreateEvent.channel)
        buttonsManager.addButton(id3) { messageButtonEvent: ButtonClickEvent ->

            // on ajoute le chemin au joueur avec pour type "default_time"
            p.setPath(path, "default_time")

            // on envoie le message de confirmation
            messageButtonEvent.buttonInteraction.createImmediateResponder()
                .setContent("Vous avez commencé votre trajet pour aller en [$xDest:$yDest] en ${timeMillisToTravel / 1000} secondes.")
                .respond()
        }
        buttonsManager.addButton(id4) { messageButtonEvent: ButtonClickEvent ->
            val bal = p["bal"].toDouble()
            check(bal >= priceToTravel) { "Vous n'avez pas assez de rb pour ce trajet" }
            p["bal"] = (bal - priceToTravel).toString()

            // il a payé donc on téléporte le joueur
            p["place_DIBIMAP_type"] = "coos"
            p["place_DIBIMAP_x"] = xDest.toString()
            p["place_DIBIMAP_y"] = yDest.toString()

            // on dit au joueur qu'il est téléporté
            messageButtonEvent.buttonInteraction.createImmediateResponder()
                .setContent("Vous avez été téléporté en [$xDest:$yDest] car vous avez payé $priceToTravel rb.")
                .respond()
        }
    }

    private fun askHow2(messageCreateEvent: MessageCreateEvent, p: Player, path: ArrayList<Pixel>, placeO: Place) {
        sendPath(messageCreateEvent, path)
        val timeMillisToTravel = path.size * 10000L
        val priceToTravel = path.size * 0.5
        // on indique le temps de trajet ou le prix que cela peut lui couter en fonction du nombre de pixels de trajet
        messageCreateEvent.message.reply("Vous allez à " + placeO.getString("name") + " en " + timeMillisToTravel + " millisecondes ou " + priceToTravel + " rb.")
        val id3 = generateUniqueID()
        val id4 = generateUniqueID()
        MessageBuilder().setEmbed(
            EmbedBuilder().setTitle("Vous allez à " + placeO.getString("name"))
                .setDescription("Avec " + path.size + " pixels de trajet en " + timeMillisToTravel + " millisecondes ou " + priceToTravel + " rb.")
                .setImage(
                    WorldEnum.DIBIMAP.world.drawPath(path), "path.png"
                ).setColor(Color.GREEN)
        ).addComponents(
            ActionRow.of(
                Button.success(id3.toString(), "Temps de trajet"),
                Button.success(id4.toString(), "Prix de trajet")
            )
        ).send(messageCreateEvent.channel)
        buttonsManager.addButton(id3) { messageButtonEvent: ButtonClickEvent ->

            // on ajoute le chemin au joueur avec pour type "default_time"
            p.setPath(path, "default_time")

            // on envoie le message de confirmation
            messageButtonEvent.buttonInteraction.createImmediateResponder()
                .setContent("Vous avez commencé votre trajet pour aller à " + placeO.getString("name") + " en " + timeMillisToTravel / 1000 + " secondes.")
                .respond()
        }
        buttonsManager.addButton(id4) { messageButtonEvent: ButtonClickEvent ->
            val bal = p["bal"].toDouble()
            check(bal >= priceToTravel) { "Vous n'avez pas assez de rb pour ce trajet" }
            p["bal"] = (bal - priceToTravel).toString()

            // il a payé donc on téléporte le joueur
            p["place_DIBIMAP_type"] = "place"
            p["place_DIBIMAP_id"] = placeO.id.toString()
            p["place_DIBIMAP_x"] = placeO.getString("x")
            p["place_DIBIMAP_y"] = placeO.getString("y")

            // on dit au joueur qu'il est téléporté
            messageButtonEvent.buttonInteraction.createImmediateResponder()
                .setContent("Vous avez été téléporté à " + placeO.getString("name") + " car vous avez payé " + priceToTravel + " rb.")
                .respond()
        }
    }
}