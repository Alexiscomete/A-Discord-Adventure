package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.buttonsManager
import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandInServer
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.messagesManager
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.worlds.Place
import io.github.alexiscomete.lapinousecond.worlds.map.Map
import io.github.alexiscomete.lapinousecond.worlds.map.Pixel
import io.github.alexiscomete.lapinousecond.worlds.places
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.util.function.Consumer

fun sendPath(messageCreateEvent: MessageCreateEvent, path: ArrayList<Pixel>) {
    val sb = StringBuilder()
    for (pixel in path) {
        sb.append(pixel)
    }
    messageCreateEvent.message.reply(sb.toString())
    val messageBuilder3 = MessageBuilder()
    messageBuilder3.addAttachment(Map.drawPath(path), "path.png")
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
            extracted("coos", { messageComponentCreateEvent1: ButtonClickEvent ->
                // on récupère le lieu en demandant à l'utilisateur de le rentrer
                val tc1 = messageCreateEvent.message.channel
                val userId1 = messageCreateEvent.message.author.id
                messageComponentCreateEvent1.buttonInteraction.createImmediateResponder()
                    .setContent("Entrez le nom du lieu :").respond()
                messagesManager.addListener(tc1, userId1) { messageCreateEvent11: MessageCreateEvent ->
                    // on récupère le lieu
                    val place = messageCreateEvent11.message.content
                    // on récupère le lieu
                    try {
                        val placeO = places[place.toLong()]
                            ?: throw IllegalArgumentException("Lieu introuvable")
                        messageCreateEvent11.message.reply("Calcul du trajet en cours ....")
                        val path1 = Map.getNode(x, y, ArrayList()).let {
                            Map.getNode(placeO.getString("x").toInt(), placeO.getString("y").toInt(), ArrayList())
                                .let { it1 ->
                                    Map.findPath(
                                        it,
                                        it1
                                    )
                                }
                        }
                        askHow2(messageCreateEvent, p, path1, placeO)
                    } catch (e: NumberFormatException) {
                        // on envoie un message d'erreur
                        sendNumberEx(messageCreateEvent11, p, -1)
                    }
                }
            }) { messageComponentCreateEvent: ButtonClickEvent ->
                // on récupère les coordonnées où le joueur souhaite aller
                val tc = messageCreateEvent.message.channel
                val userId = messageCreateEvent.message.author.id
                messageComponentCreateEvent.buttonInteraction.createImmediateResponder()
                    .setContent("Entrez les coordonnées de la destination :").respond()
                messagesManager.addListener(tc, userId) { messageCreateEvent1: MessageCreateEvent ->
                    val coords = getCoosDest(messageCreateEvent1)
                    val xDest = coords[0]
                    val yDest = coords[1]
                    messageCreateEvent1.message.reply("Calcul du trajet en cours ....")
                    val path = Map.getNode(x, y, ArrayList()).let {
                        Map.getNode(xDest, yDest, ArrayList()).let { it1 ->
                            Map.findPath(
                                it,
                                it1
                            )
                        }
                    }
                    askHow1(messageCreateEvent1, p, path, xDest, yDest)
                }
            }
        } else if (placeType == "place") {
            // on récupère le lieu
            val place0 = p.getString("place_DIBIMAP_place")
            // on récupère le lieu dans la base de données
            val place1 = places[place0.toLong()]

            // on sépare le cas où le joueur veut aller dans un lieu et celui où il veut aller sur des coos
            // création du menu
            extracted("city", { messageComponentCreateEvent1: ButtonClickEvent ->
                // on récupère le lieu en demandant à l'utilisateur de le rentrer
                val tc1 = messageCreateEvent.message.channel
                val userId1 = messageCreateEvent.message.author.id
                messageComponentCreateEvent1.buttonInteraction.createImmediateResponder()
                    .setContent("Entrez le nom du lieu :").respond()
                messagesManager.addListener(tc1, userId1) { messageCreateEvent11: MessageCreateEvent ->
                    // on récupère le lieu
                    val place = messageCreateEvent11.message.content
                    // on récupère le lieu
                    try {
                        val placeO = places[place.toLong()]
                            ?: throw IllegalArgumentException("Lieu introuvable")
                        messageCreateEvent11.message.reply("Calcul du trajet en cours ....")
                        val path1 = Map.getNode(
                            place1!!.getString("x").toInt(), place1.getString("y").toInt(), ArrayList()
                        ).let {
                            Map.getNode(placeO.getString("x").toInt(), placeO.getString("y").toInt(), ArrayList())
                                .let { it1 ->
                                    Map.findPath(
                                        it,
                                        it1
                                    )
                                }
                        }
                        askHow2(messageCreateEvent11, p, path1, placeO)
                    } catch (e: NumberFormatException) {
                        // on envoie un message d'erreur
                        sendNumberEx(messageCreateEvent11, p, -1)
                    }
                }
            }) { messageComponentCreateEvent: ButtonClickEvent ->
                // on récupère les coordonnées où le joueur souhaite aller
                val tc = messageCreateEvent.message.channel
                val userId = messageCreateEvent.message.author.id
                messageComponentCreateEvent.buttonInteraction.createImmediateResponder()
                    .setContent("Entrez les coordonnées de la destination :").respond()
                messagesManager.addListener(tc, userId) { messageCreateEvent1: MessageCreateEvent ->
                    val coords = getCoosDest(messageCreateEvent1)
                    val xDest = coords[0]
                    val yDest = coords[1]
                    messageCreateEvent1.message.reply("Calcul du trajet en cours ....")
                    val path = Map.getNode(
                        place1!!.getString("x").toInt(), place1.getString("y").toInt(), ArrayList()
                    ).let {
                        Map.getNode(xDest, yDest, ArrayList()).let { it1 ->
                            Map.findPath(
                                it, it1
                            )
                        }
                    }
                    askHow1(messageCreateEvent1, p, path, xDest, yDest)
                }
            }
        } else {
            // on envoie un message d'erreur
            messageCreateEvent.message.reply("Etrange, ce type de lieu n'existe pas. Je vais donc vous téléporter. Retentez votre commande.")
        }
    }

    private fun extracted(
        type: String,
        c1: Consumer<ButtonClickEvent>,
        c2: Consumer<ButtonClickEvent>
    ) {
        val mb = MessageBuilder()
        val eb = EmbedBuilder()
        eb.setTitle("Voyage dans le monde Dibimap")
        eb.setDescription("Vous êtes " + (if (type == "city") "dans une ville" else "en dehors d'une ville") + ", vous pouvez choisir entre aller dans un lieu ou sur des coos.")
        eb.setColor(Color.BLUE)
        mb.append(eb)
        val id1 = generateUniqueID()
        val id2 = generateUniqueID()

        // ajout des choix
        mb.addComponents(
            ActionRow.of(
                Button.success(id1.toString(), "Aller dans un lieu"),
                Button.success(id2.toString(), "Aller sur des coos")
            )
        )

        // ajout des actions
        buttonsManager.addButton(id1, c1)
        buttonsManager.addButton(id2, c2)
    }

    private fun verifButton(p: Player, state: Int, messageButtonEvent: ButtonClickEvent) {
        val messageComponentInteraction = messageButtonEvent.buttonInteraction
        check(messageComponentInteraction.user.id == p.id) { "Ce bouton n'est pas pour vous" }
        messageComponentInteraction.message.delete()
        check(p.state == state) { "Ce bouton n'est plus valide" }
        p.state++
    }

    private fun getCoosDest(messageCreateEvent: MessageCreateEvent): IntArray {
        // on récupère le message
        val message = messageCreateEvent.message.content

        // les coordonnées sont séparées par un espace
        val coords = message.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        check(coords.size == 2) {
            // on envoie un message d'erreur
            "Vous devez entrer les coordonnées de la destination séparées par un espace."
        }

        // on vérifie que les coordonnées sont bien des nombres
        check(!(isNotNumeric(coords[0]) || isNotNumeric(coords[1]))) {
            // on envoie un message d'erreur
            "Les coordonnées doivent être des nombres."
        }

        // on récupère les coordonnées
        val xDest = coords[0].toInt()
        val yDest = coords[1].toInt()

        // on vérifie les bornes
        check(!(xDest < 0 || xDest >= Map.MAP_WIDTH || yDest < 0 || yDest >= Map.MAP_HEIGHT)) {
            // on envoie un message d'erreur
            "Les coordonnées doivent être comprises entre 0 et " + (Map.MAP_WIDTH - 1) + " et " + (Map.MAP_HEIGHT - 1) + "."
        }
        return intArrayOf(xDest, yDest)
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
                    Map.drawPath(path), "path.png"
                ).setColor(Color.GREEN)
        ).addComponents(
            ActionRow.of(
                Button.success(id3.toString(), "Temps de trajet"),
                Button.success(id4.toString(), "Prix de trajet")
            )
        ).send(messageCreateEvent.channel)
        val state = p.state
        buttonsManager.addButton(id3) { messageButtonEvent: ButtonClickEvent ->
            verifButton(p, state, messageButtonEvent)

            // on ajoute le chemin au joueur avec pour type "default_time"
            p.setPath(path, "default_time")

            // on envoie le message de confirmation
            messageButtonEvent.buttonInteraction.createImmediateResponder()
                .setContent("Vous avez commencé votre trajet pour aller en [$xDest:$yDest] en ${timeMillisToTravel / 1000} secondes.")
                .respond()
        }
        buttonsManager.addButton(id4) { messageButtonEvent: ButtonClickEvent ->
            verifButton(p, state, messageButtonEvent)
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
                    Map.drawPath(path), "path.png"
                ).setColor(Color.GREEN)
        ).addComponents(
            ActionRow.of(
                Button.success(id3.toString(), "Temps de trajet"),
                Button.success(id4.toString(), "Prix de trajet")
            )
        ).send(messageCreateEvent.channel)
        val state = p.state
        buttonsManager.addButton(id3) { messageButtonEvent: ButtonClickEvent ->
            verifButton(p, state, messageButtonEvent)

            // on ajoute le chemin au joueur avec pour type "default_time"
            p.setPath(path, "default_time")

            // on envoie le message de confirmation
            messageButtonEvent.buttonInteraction.createImmediateResponder()
                .setContent("Vous avez commencé votre trajet pour aller à " + placeO.getString("name") + " en " + timeMillisToTravel / 1000 + " secondes.")
                .respond()
        }
        buttonsManager.addButton(id4) { messageButtonEvent: ButtonClickEvent ->
            verifButton(p, state, messageButtonEvent)
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