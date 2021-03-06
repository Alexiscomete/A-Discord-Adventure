package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.api
import io.github.alexiscomete.lapinousecond.buttonsManager
import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandInServer
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.worlds.Place
import io.github.alexiscomete.lapinousecond.worlds.ServerBot
import io.github.alexiscomete.lapinousecond.worlds.map.Map
import io.github.alexiscomete.lapinousecond.worlds.map.Pixel
import io.github.alexiscomete.lapinousecond.worlds.places
import io.github.alexiscomete.lapinousecond.messagesManager
import io.github.alexiscomete.lapinousecond.worlds.servers
import org.javacord.api.entity.channel.ServerChannel
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.server.invite.InviteBuilder
import org.javacord.api.event.interaction.MessageComponentCreateEvent
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import java.util.concurrent.ExecutionException
import java.util.function.Consumer
import java.util.stream.Collectors

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
            messageCreateEvent.message.reply("Utilisez place links pour voir les possibilit??s de voyage, si aucune ne vous convient tentez le create_link pour le monde NORMAL")
            return
        }

        // On r??cup??re le monde du joueur
        var world = p.getString("current_world")
        if (world == "") { // si le monde est vide alors on le met ?? NORMAL
            world = "NORMAL"
            p["current_world"] = "NORMAL"
        }
        if (world == "NORMAL") {
            travelWorldNormal(messageCreateEvent, args, p)
        } else if (world == "DIBIMAP") {
            travelWorldDibimap(messageCreateEvent, args, p)
        }
    }
    // --------------------------------------------------
    // --------- EN FONCTION DU MONDE -------------------
    // --------------------------------------------------
    /**
     * Si le monde est le monde est le monde normal
     */
    private fun travelWorldNormal(messageCreateEvent: MessageCreateEvent, args: Array<String>, p: Player) {
        // On r??cup??re le lieu du joueur dans le monde
        var placeID = p.getString("place_NORMAL")
        if (placeID == "") { // si le lieu est vide alors on l'initialise au lieu de d??part : le serveur A Discord Adventure
            placeID = ServerBot(854288660147994634L).getString("places")
            p["place_NORMAL"] = placeID
        }

        // On r??cup??re le lieu du joueur sous forme d'objet avec l'id
        val place = places[placeID.toLong()]

        // si le lieu n'existe pas on l'indique au joueur et on propose d'utiliser -hub
        if (place == null) {
            println(placeID)
            messageCreateEvent.message.reply("Votre lieu est introuvable, utilisez -hub pour revenir au spawn.")
            return
        }

        // on recup??re les lieux dans lesquels le joueur peut se rendre depuis le lieu actuel
        val places = Place.toPlaces(place.getString("connections"))

        // On r??cup??re le serveur dans lequel le joueur veut se rendre
        val dest: Place = try {
            Place(args[1].toLong())
        } catch (e: IllegalArgumentException) {
            messageCreateEvent.message.reply("SVP ne jouez pas ?? entrer autre chose que des nombres")
            return
        }

        // On v??rifie les liens entre les deux lieux
        if (!places.contains(dest)) {
            messageCreateEvent.message.reply("Il n' existe pas de route entre votre lieu et votre destination")
            return
        }

        // On regarde si le joueur a assez d'argent pour se rendre ?? la destination
        val bal = p["bal"].toDouble()
        if (bal < 100) {
            messageCreateEvent.message.reply("Il vous faut 100 rb pour voyager dans le monde normal")
            return
        }

        // on r??cup??re le serveur discord de destinatione en optionnel
        val serverOp = api.getServerById(dest.getString("serv").toLong())
        if (serverOp.isEmpty) { // si le serveur n'existe pas (ou que le bot n'a pas l'acc??s) on l'indique au joueur
            messageCreateEvent.message.reply("Voyage vers cette destination impossible : le serveur discord est inconnu. Possibilit??s : bot kick, serveur supprim??, corruption du lieu")
            return
        }

        // on r??cup??re le serveur discord de destination
        val server = serverOp.get()

        // message de confirmation
        if (args.size < 3) {
            messageCreateEvent.message.reply("Prix pour aller dans ce serveur : 100. Tapez la m??me commande avec oui ?? la fin pour confirmer votre choix (ce dernier est irr??vocable)")
            return
        }

        // on r??cup??re les salons de discussion du serveur
        var channels = server.channels
        channels = channels.stream().filter { serverChannel: ServerChannel -> serverChannel.type.isTextChannelType }
            .collect(Collectors.toList())
        if (channels.size == 0) { // si on ne trouve pas de salon de discussion on l'indique au joueur et au propri??taire du serveur
            messageCreateEvent.message.reply("Bon je pense que ce serveur ne vaux pas la peine : il n'y aucun salon !! Je ne peux m??me pas vous inviter.")
            server.owner.get()
                .sendMessage("Bon ... si il n'y a m??me pas de salon dans votre serveur je ne peux rien faire.")
            return
        }

        // on r??cup??re le serveur de destination sous forme d'objet personnalis??
        val nextServer = servers[dest.getString("serv").toLong()]

        // on regarde si le serveur est bien enregistr?? dans la base de donn??es
        if (nextServer == null) {
            messageCreateEvent.message.reply("Voyage impossible : le serveur n' est pas configur??")
            return
        }

        // cr??ation de l'invitation vers le serveur
        val inv = InviteBuilder(channels[0])
        println(channels[0].name)
        try {

            // on envoie l'invitation apr??s avoir r??cup??r?? l'utilisateur
            val user = messageCreateEvent.messageAuthor.asUser().get()
            user.sendMessage(inv.create().get().url.toString())
            user.sendMessage(dest.getString("train"))

            // on set les valeurs dans la base de donn??es
            p["serv"] = (nextServer.id).toString()
            p["bal"] = (bal - 100).toString()
            p["place_NORMAL"] = dest.id.toString()

            // on envoie un message de confirmation
            messageCreateEvent.message.reply("Dans le monde NORMAL le voyage est instantan??, au revoir !")
        } catch (e: InterruptedException) {
            // on envoie un message d'erreur
            messageCreateEvent.message.reply("Une erreur est survenue lors de la cr??ation de l'invitation.")
            e.printStackTrace()
        } catch (e: ExecutionException) {
            messageCreateEvent.message.reply("Une erreur est survenue lors de la cr??ation de l'invitation.")
            e.printStackTrace()
        }
    }

    // si le joueur est dans le monde Dibimap
    private fun travelWorldDibimap(messageCreateEvent: MessageCreateEvent, args: Array<String>, p: Player) {
        // on r??cup??re le lieu dans le monde Dibimap
        val placeType = p.getString("place_DIBIMAP_type")
        // je regarde si les arguments sont bien pr??sents
        if (args.size < 2) {
            // si non on envoie un message d'erreur car il n'a pas donn?? le type de voyage
            sendArgs(messageCreateEvent, p)
            return
        }
        // je s??pare en 2 cas : si le type est coos ou si le type est un lieu
        if (placeType == "coos") {
            // on r??cup??re les coordonn??es
            val x = p.getString("place_DIBIMAP_x").toInt()
            val y = p.getString("place_DIBIMAP_y").toInt()
            // on ne r??cup??re pas le lieu, car on ne connait pas le lieu
            // on s??pare le cas o?? le joueur veut aller dans un lieu et celui o?? il veut aller sur des coos

            // cr??ation du menu
            extracted("coos", { messageComponentCreateEvent1: MessageComponentCreateEvent ->
                // on r??cup??re le lieu en demandant ?? l'utilisateur de le rentrer
                val tc1 = messageCreateEvent.message.channel
                val userId1 = messageCreateEvent.message.author.id
                messageComponentCreateEvent1.messageComponentInteraction.createImmediateResponder()
                    .setContent("Entrez le nom du lieu :").respond()
                messagesManager.addListener(tc1, userId1) { messageCreateEvent11: MessageCreateEvent ->
                    // on r??cup??re le lieu
                    val place = messageCreateEvent11.message.content
                    // on r??cup??re le lieu
                    try {
                        val placeO = places[place.toLong()]
                            ?: throw IllegalArgumentException("Lieu introuvable")
                        messageCreateEvent11.message.reply("Calcul du trajet en cours ....")
                        val path1 = Map.getNode(x, y, ArrayList()).let {
                            Map.getNode(placeO.getString("x").toInt(), placeO.getString("y").toInt(), ArrayList())
                                .let { it1 ->
                                    Map.findPath(
                                        it,
                                        it1,
                                        messageCreateEvent.channel
                                    )
                                }
                        }
                        askHow2(messageCreateEvent, p, path1, placeO)
                    } catch (e: NumberFormatException) {
                        // on envoie un message d'erreur
                        sendNumberEx(messageCreateEvent11, p, -1)
                    }
                }
            }) { messageComponentCreateEvent: MessageComponentCreateEvent ->
                // on r??cup??re les coordonn??es o?? le joueur souhaite aller
                val tc = messageCreateEvent.message.channel
                val userId = messageCreateEvent.message.author.id
                messageComponentCreateEvent.messageComponentInteraction.createImmediateResponder()
                    .setContent("Entrez les coordonn??es de la destination :").respond()
                messagesManager.addListener(tc, userId) { messageCreateEvent1: MessageCreateEvent ->
                    val coords = getCoosDest(messageCreateEvent1)
                    val xDest = coords[0]
                    val yDest = coords[1]
                    messageCreateEvent1.message.reply("Calcul du trajet en cours ....")
                    val path = Map.getNode(x, y, ArrayList()).let {
                        Map.getNode(xDest, yDest, ArrayList()).let { it1 ->
                            Map.findPath(
                                it,
                                it1,
                                messageCreateEvent.channel
                            )
                        }
                    }
                    askHow1(messageCreateEvent1, p, path, xDest, yDest)
                }
            }
        } else if (placeType == "place") {
            // on r??cup??re le lieu
            val place0 = p.getString("place_DIBIMAP_place")
            // on r??cup??re le lieu dans la base de donn??es
            val place1 = places[place0.toLong()]

            // on s??pare le cas o?? le joueur veut aller dans un lieu et celui o?? il veut aller sur des coos
            // cr??ation du menu
            extracted("city", { messageComponentCreateEvent1: MessageComponentCreateEvent ->
                // on r??cup??re le lieu en demandant ?? l'utilisateur de le rentrer
                val tc1 = messageCreateEvent.message.channel
                val userId1 = messageCreateEvent.message.author.id
                messageComponentCreateEvent1.messageComponentInteraction.createImmediateResponder()
                    .setContent("Entrez le nom du lieu :").respond()
                messagesManager.addListener(tc1, userId1) { messageCreateEvent11: MessageCreateEvent ->
                    // on r??cup??re le lieu
                    val place = messageCreateEvent11.message.content
                    // on r??cup??re le lieu
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
                                        it1,
                                        messageCreateEvent.channel
                                    )
                                }
                        }
                        askHow2(messageCreateEvent11, p, path1, placeO)
                    } catch (e: NumberFormatException) {
                        // on envoie un message d'erreur
                        sendNumberEx(messageCreateEvent11, p, -1)
                    }
                }
            }) { messageComponentCreateEvent: MessageComponentCreateEvent ->
                // on r??cup??re les coordonn??es o?? le joueur souhaite aller
                val tc = messageCreateEvent.message.channel
                val userId = messageCreateEvent.message.author.id
                messageComponentCreateEvent.messageComponentInteraction.createImmediateResponder()
                    .setContent("Entrez les coordonn??es de la destination :").respond()
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
                                it, it1, messageCreateEvent.channel
                            )
                        }
                    }
                    askHow1(messageCreateEvent1, p, path, xDest, yDest)
                }
            }
        } else {
            // on envoie un message d'erreur
            messageCreateEvent.message.reply("Etrange, ce type de lieu n'existe pas. Je vais donc vous t??l??porter. Retentez votre commande.")
            setCoos(p)
        }
    }

    private fun extracted(
        type: String,
        c1: Consumer<MessageComponentCreateEvent>,
        c2: Consumer<MessageComponentCreateEvent>
    ) {
        val mb = MessageBuilder()
        val eb = EmbedBuilder()
        eb.setTitle("Voyage dans le monde Dibimap")
        eb.setDescription("Vous ??tes " + (if (type == "city") "dans une ville" else "en dehors d'une ville") + ", vous pouvez choisir entre aller dans un lieu ou sur des coos.")
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

    private fun verifButton(p: Player, state: Int, messageButtonEvent: MessageComponentCreateEvent) {
        val messageComponentInteraction = messageButtonEvent.messageComponentInteraction
        check(messageComponentInteraction.user.id == p.id) { "Ce bouton n'est pas pour vous" }
        messageComponentInteraction.message.delete()
        check(p.state == state) { "Ce bouton n'est plus valide" }
        p.state++
    }

    private fun getCoosDest(messageCreateEvent: MessageCreateEvent): IntArray {
        // on r??cup??re le message
        val message = messageCreateEvent.message.content

        // les coordonn??es sont s??par??es par un espace
        val coords = message.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        check(coords.size == 2) {
            // on envoie un message d'erreur
            "Vous devez entrer les coordonn??es de la destination s??par??es par un espace."
        }

        // on v??rifie que les coordonn??es sont bien des nombres
        check(!(isNotNumeric(coords[0]) || isNotNumeric(coords[1]))) {
            // on envoie un message d'erreur
            "Les coordonn??es doivent ??tre des nombres."
        }

        // on r??cup??re les coordonn??es
        val xDest = coords[0].toInt()
        val yDest = coords[1].toInt()

        // on v??rifie les bornes
        check(!(xDest < 0 || xDest >= Map.MAP_WIDTH || yDest < 0 || yDest >= Map.MAP_HEIGHT)) {
            // on envoie un message d'erreur
            "Les coordonn??es doivent ??tre comprises entre 0 et " + (Map.MAP_WIDTH - 1) + " et " + (Map.MAP_HEIGHT - 1) + "."
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
        buttonsManager.addButton(id3) { messageButtonEvent: MessageComponentCreateEvent ->
            verifButton(p, state, messageButtonEvent)

            // on ajoute le chemin au joueur avec pour type "default_time"
            p.setPath(path, "default_time")

            // on envoie le message de confirmation
            messageButtonEvent.messageComponentInteraction.createImmediateResponder()
                .setContent("Vous avez commenc?? votre trajet pour aller en [$xDest:$yDest] en ${timeMillisToTravel / 1000} secondes.")
                .respond()
        }
        buttonsManager.addButton(id4) { messageButtonEvent: MessageComponentCreateEvent ->
            verifButton(p, state, messageButtonEvent)
            val bal = p["bal"].toDouble()
            check(bal >= priceToTravel) { "Vous n'avez pas assez de rb pour ce trajet" }
            p["bal"] = (bal - priceToTravel).toString()

            // il a pay?? donc on t??l??porte le joueur
            p["place_DIBIMAP_type"] = "coos"
            p["place_DIBIMAP_x"] = xDest.toString()
            p["place_DIBIMAP_y"] = yDest.toString()

            // on dit au joueur qu'il est t??l??port??
            messageButtonEvent.messageComponentInteraction.createImmediateResponder()
                .setContent("Vous avez ??t?? t??l??port?? en [$xDest:$yDest] car vous avez pay?? $priceToTravel rb.")
                .respond()
        }
    }

    private fun askHow2(messageCreateEvent: MessageCreateEvent, p: Player, path: ArrayList<Pixel>, placeO: Place) {
        sendPath(messageCreateEvent, path)
        val timeMillisToTravel = path.size * 10000L
        val priceToTravel = path.size * 0.5
        // on indique le temps de trajet ou le prix que cela peut lui couter en fonction du nombre de pixels de trajet
        messageCreateEvent.message.reply("Vous allez ?? " + placeO.getString("name") + " en " + timeMillisToTravel + " millisecondes ou " + priceToTravel + " rb.")
        val id3 = generateUniqueID()
        val id4 = generateUniqueID()
        MessageBuilder().setEmbed(
            EmbedBuilder().setTitle("Vous allez ?? " + placeO.getString("name"))
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
        buttonsManager.addButton(id3) { messageButtonEvent: MessageComponentCreateEvent ->
            verifButton(p, state, messageButtonEvent)

            // on ajoute le chemin au joueur avec pour type "default_time"
            p.setPath(path, "default_time")

            // on envoie le message de confirmation
            messageButtonEvent.messageComponentInteraction.createImmediateResponder()
                .setContent("Vous avez commenc?? votre trajet pour aller ?? " + placeO.getString("name") + " en " + timeMillisToTravel / 1000 + " secondes.")
                .respond()
        }
        buttonsManager.addButton(id4) { messageButtonEvent: MessageComponentCreateEvent ->
            verifButton(p, state, messageButtonEvent)
            val bal = p["bal"].toDouble()
            check(bal >= priceToTravel) { "Vous n'avez pas assez de rb pour ce trajet" }
            p["bal"] = (bal - priceToTravel).toString()

            // il a pay?? donc on t??l??porte le joueur
            p["place_DIBIMAP_type"] = "place"
            p["place_DIBIMAP_id"] = placeO.id.toString()
            p["place_DIBIMAP_x"] = placeO.getString("x")
            p["place_DIBIMAP_y"] = placeO.getString("y")

            // on dit au joueur qu'il est t??l??port??
            messageButtonEvent.messageComponentInteraction.createImmediateResponder()
                .setContent("Vous avez ??t?? t??l??port?? ?? " + placeO.getString("name") + " car vous avez pay?? " + priceToTravel + " rb.")
                .respond()
        }
    }

    // set coordonn??es par d??faut
    private fun setCoos(p: Player) {
        // on met les coordonn??es par d??faut
        p["place_DIBIMAP_x"] = "400"
        p["place_DIBIMAP_y"] = "250"
        // et le type de lieu par d??faut
        p["place_DIBIMAP_type"] = "coos"
    }
}