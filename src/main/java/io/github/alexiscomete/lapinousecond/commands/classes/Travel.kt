package io.github.alexiscomete.lapinousecond.commands.classes

import io.github.alexiscomete.lapinousecond.Main
import io.github.alexiscomete.lapinousecond.commands.CommandInServer
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.save.SaveLocation.Companion.generateUniqueID
import io.github.alexiscomete.lapinousecond.worlds.Place
import io.github.alexiscomete.lapinousecond.worlds.ServerBot
import io.github.alexiscomete.lapinousecond.worlds.map.Map
import io.github.alexiscomete.lapinousecond.worlds.map.Pixel
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

class Travel : CommandInServer("Vous permet de voyager vers un serveur", "travel", "travel [server id]") {
    override fun executeC(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {

        // On explique au joueur qu'il manque des arguments : le lieu vers lequel il veut se rendre
        if (args.size < 2 || args[1] == "list") {
            messageCreateEvent.message.reply("Utilisez place links pour voir les possibilités de voyage, si aucune ne vous convient tentez le create_link pour le monde NORMAL")
            return
        }

        // On récupère le monde du joueur
        var world = p.getString("current_world")
        if (world == "") { // si le monde est vide alors on le met à NORMAL
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
        // On récupère le lieu du joueur dans le monde
        var placeID = p.getString("place_NORMAL")
        if (placeID == "") { // si le lieu est vide alors on l'initialise au lieu de départ : le serveur A Discord Adventure
            placeID = ServerBot(854288660147994634L).getString("places")
            p["place_NORMAL"] = placeID
        }

        // On récupère le lieu du joueur sous forme d'objet avec l'id
        val place = saveManager.places[placeID.toLong()]

        // si le lieu n'existe pas on l'indique au joueur et on propose d'utiliser -hub
        if (place == null) {
            println(placeID)
            messageCreateEvent.message.reply("Votre lieu est introuvable, utilisez -hub pour revenir au spawn.")
            return
        }

        // on recupère les lieux dans lesquels le joueur peut se rendre depuis le lieu actuel
        val places = Place.toPlaces(place.getString("connections"))

        // On récupère le serveur dans lequel le joueur veut se rendre
        val dest: Place
        dest = try {
            Place(args[1].toLong())
        } catch (e: IllegalArgumentException) {
            messageCreateEvent.message.reply("SVP ne jouez pas à entrer autre chose que des nombres")
            return
        }

        // On vérifie les liens entre les deux lieux
        if (!places.contains(dest)) {
            messageCreateEvent.message.reply("Il n' existe pas de route entre votre lieu et votre destination")
            return
        }

        // On regarde si le joueur a assez d'argent pour se rendre à la destination
        val bal = p.getBal()
        if (bal < 100) {
            messageCreateEvent.message.reply("Il vous faut 100 rb pour voyager dans le monde normal")
            return
        }

        // on récupère le serveur discord de destinatione en optionnel
        val serverOp = Main.api.getServerById(dest.getString("serv").toLong())
        if (serverOp.isEmpty) { // si le serveur n'existe pas (ou que le bot n'a pas l'accès) on l'indique au joueur
            messageCreateEvent.message.reply("Voyage vers cette destination impossible : le serveur discord est inconnu. Possibilités : bot kick, serveur supprimé, corruption du lieu")
            return
        }

        // on récupère le serveur discord de destination
        val server = serverOp.get()

        // message de confirmation
        if (args.size < 3) {
            messageCreateEvent.message.reply("Prix pour aller dans ce serveur : 100. Tapez la même commande avec oui à la fin pour confirmer votre choix (ce dernier est irrévocable)")
            return
        }

        // on récupère les salons de discussion du serveur
        var channels = server.channels
        channels = channels.stream().filter { serverChannel: ServerChannel -> serverChannel.type.isTextChannelType }
            .collect(Collectors.toList())
        if (channels.size == 0) { // si on ne trouve pas de salon de discussion on l'indique au joueur et au propriétaire du serveur
            messageCreateEvent.message.reply("Bon je pense que ce serveur ne vaux pas la peine : il n'y aucun salon !! Je ne peux même pas vous inviter.")
            server.owner.get()
                .sendMessage("Bon ... si il n'y a même pas de salon dans votre serveur je ne peux rien faire.")
            return
        }

        // on récupère le serveur de destination sous forme d'objet personnalisé
        val nextServer = saveManager.servers[dest.getString("serv").toLong()]

        // on regarde si le serveur est bien enregistré dans la base de données
        if (nextServer == null) {
            messageCreateEvent.message.reply("Voyage impossible : le serveur n' est pas configuré")
            return
        }

        // création de l'invitation vers le serveur
        val inv = InviteBuilder(channels[0])
        println(channels[0].name)
        try {

            // on envoie l'invitation après avoir récupéré l'utilisateur
            val user = messageCreateEvent.messageAuthor.asUser().get()
            user.sendMessage(inv.create().get().url.toString())
            user.sendMessage(dest.getString("train"))

            // on set les valeurs dans la base de données
            p.setServer(nextServer.id)
            p.setBal(bal - 100)
            p["place_NORMAL"] = dest.id.toString()

            // on envoie un message de confirmation
            messageCreateEvent.message.reply("Dans le monde NORMAL le voyage est instantané, au revoir !")
        } catch (e: InterruptedException) {
            // on envoie un message d'erreur
            messageCreateEvent.message.reply("Une erreur est survenue lors de la création de l'invitation.")
            e.printStackTrace()
        } catch (e: ExecutionException) {
            messageCreateEvent.message.reply("Une erreur est survenue lors de la création de l'invitation.")
            e.printStackTrace()
        }
    }

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
            extracted("coos", { messageComponentCreateEvent1: MessageComponentCreateEvent ->
                // on récupère le lieu en demandant à l'utilisateur de le rentrer
                val tc1 = messageCreateEvent.message.channel
                val userId1 = messageCreateEvent.message.author.id
                messageComponentCreateEvent1.messageComponentInteraction.createImmediateResponder()
                    .setContent("Entrez le nom du lieu :").respond()
                Main.messagesManager.addListener(tc1, userId1) { messageCreateEvent11: MessageCreateEvent ->
                    // on récupère le lieu
                    val place = messageCreateEvent11.message.content
                    // on récupère le lieu
                    try {
                        val placeO = Main.saveManager.places[place.toLong()]
                            ?: throw IllegalArgumentException("Lieu introuvable")
                        messageCreateEvent11.message.reply("Calcul du trajet en cours ....")
                        val path1 = Map.findPath(
                            Map.getNode(x, y, ArrayList()),
                            Map.getNode(placeO.getString("x").toInt(), placeO.getString("y").toInt(), ArrayList()),
                            messageCreateEvent.channel
                        )
                        askHow2(messageCreateEvent, p, path1, placeO)
                    } catch (e: NumberFormatException) {
                        // on envoie un message d'erreur
                        sendNumberEx(messageCreateEvent11, p, -1)
                    }
                }
            }) { messageComponentCreateEvent: MessageComponentCreateEvent ->
                // on récupère les coordonnées où le joueur souhaite aller
                val tc = messageCreateEvent.message.channel
                val userId = messageCreateEvent.message.author.id
                messageComponentCreateEvent.messageComponentInteraction.createImmediateResponder()
                    .setContent("Entrez les coordonnées de la destination :").respond()
                Main.messagesManager.addListener(tc, userId) { messageCreateEvent1: MessageCreateEvent ->
                    val coords = getCoosDest(messageCreateEvent1)
                    val xDest = coords[0]
                    val yDest = coords[1]
                    messageCreateEvent1.message.reply("Calcul du trajet en cours ....")
                    val path = Map.findPath(
                        Map.getNode(x, y, ArrayList()),
                        Map.getNode(xDest, yDest, ArrayList()),
                        messageCreateEvent.channel
                    )
                    askHow1(messageCreateEvent1, p, path, xDest, yDest)
                }
            }
        } else if (placeType == "place") {
            // on récupère le lieu
            val place0 = p.getString("place_DIBIMAP_place")
            // on récupère le lieu dans la base de données
            val place1 = Main.saveManager.places[place0.toLong()]

            // on sépare le cas où le joueur veut aller dans un lieu et celui où il veut aller sur des coos
            // création du menu
            extracted("city", { messageComponentCreateEvent1: MessageComponentCreateEvent ->
                // on récupère le lieu en demandant à l'utilisateur de le rentrer
                val tc1 = messageCreateEvent.message.channel
                val userId1 = messageCreateEvent.message.author.id
                messageComponentCreateEvent1.messageComponentInteraction.createImmediateResponder()
                    .setContent("Entrez le nom du lieu :").respond()
                Main.messagesManager.addListener(tc1, userId1) { messageCreateEvent11: MessageCreateEvent ->
                    // on récupère le lieu
                    val place = messageCreateEvent11.message.content
                    // on récupère le lieu
                    try {
                        val placeO = Main.saveManager.places[place.toLong()]
                            ?: throw IllegalArgumentException("Lieu introuvable")
                        messageCreateEvent11.message.reply("Calcul du trajet en cours ....")
                        val path1 = Map.findPath(
                            Map.getNode(
                                place1!!.getString("x").toInt(), place1.getString("y").toInt(), ArrayList()
                            ),
                            Map.getNode(placeO.getString("x").toInt(), placeO.getString("y").toInt(), ArrayList()),
                            messageCreateEvent.channel
                        )
                        askHow2(messageCreateEvent11, p, path1, placeO)
                    } catch (e: NumberFormatException) {
                        // on envoie un message d'erreur
                        sendNumberEx(messageCreateEvent11, p, -1)
                    }
                }
            }) { messageComponentCreateEvent: MessageComponentCreateEvent ->
                // on récupère les coordonnées où le joueur souhaite aller
                val tc = messageCreateEvent.message.channel
                val userId = messageCreateEvent.message.author.id
                messageComponentCreateEvent.messageComponentInteraction.createImmediateResponder()
                    .setContent("Entrez les coordonnées de la destination :").respond()
                Main.messagesManager.addListener(tc, userId) { messageCreateEvent1: MessageCreateEvent ->
                    val coords = getCoosDest(messageCreateEvent1)
                    val xDest = coords[0]
                    val yDest = coords[1]
                    messageCreateEvent1.message.reply("Calcul du trajet en cours ....")
                    val path = Map.findPath(
                        Map.getNode(
                            place1!!.getString("x").toInt(), place1.getString("y").toInt(), ArrayList()
                        ), Map.getNode(xDest, yDest, ArrayList()), messageCreateEvent.channel
                    )
                    askHow1(messageCreateEvent1, p, path, xDest, yDest)
                }
            }
        } else {
            // on envoie un message d'erreur
            messageCreateEvent.message.reply("Etrange, ce type de lieu n'existe pas. Je vais donc vous téléporter. Retentez votre commande.")
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
        Main.buttonsManager.addButton(id1, c1)
        Main.buttonsManager.addButton(id2, c2)
    }

    private fun sendPath(messageCreateEvent: MessageCreateEvent, path: ArrayList<Pixel?>) {
        val sb = StringBuilder()
        for (pixel in path) {
            sb.append(pixel)
        }
        messageCreateEvent.message.reply(sb.toString())
        val messageBuilder3 = MessageBuilder()
        messageBuilder3.addAttachment(Map.drawPath(path), "path.png")
        messageBuilder3.send(messageCreateEvent.channel)
    }

    private fun verifButton(p: Player, state: Int, messageButtonEvent: MessageComponentCreateEvent) {
        val messageComponentInteraction = messageButtonEvent.messageComponentInteraction
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
        path: ArrayList<Pixel?>,
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
        Main.buttonsManager.addButton(id3) { messageButtonEvent: MessageComponentCreateEvent ->
            verifButton(p, state, messageButtonEvent)

            // on ajoute le chemin au joueur avec pour type "default_time"
            p.setPath(path, "default_time")

            // on envoie le message de confirmation
            messageButtonEvent.messageComponentInteraction.createImmediateResponder()
                .setContent("Vous avez commencé votre trajet pour aller en [$xDest:$yDest] en ${timeMillisToTravel / 1000} secondes.")
                .respond()
        }
        Main.buttonsManager.addButton(id4) { messageButtonEvent: MessageComponentCreateEvent ->
            verifButton(p, state, messageButtonEvent)
            val bal = p.getBal()
            check(bal >= priceToTravel) { "Vous n'avez pas assez de rb pour ce trajet" }
            p.setBal(bal - priceToTravel)

            // il a payé donc on téléporte le joueur
            p["place_DIBIMAP_type"] = "coos"
            p["place_DIBIMAP_x"] = xDest.toString()
            p["place_DIBIMAP_y"] = yDest.toString()

            // on dit au joueur qu'il est téléporté
            messageButtonEvent.messageComponentInteraction.createImmediateResponder()
                .setContent("Vous avez été téléporté en [$xDest:$yDest] car vous avez payé $priceToTravel rb.")
                .respond()
        }
    }

    private fun askHow2(messageCreateEvent: MessageCreateEvent, p: Player, path: ArrayList<Pixel?>, placeO: Place) {
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
        Main.buttonsManager.addButton(id3) { messageButtonEvent: MessageComponentCreateEvent ->
            verifButton(p, state, messageButtonEvent)

            // on ajoute le chemin au joueur avec pour type "default_time"
            p.setPath(path, "default_time")

            // on envoie le message de confirmation
            messageButtonEvent.messageComponentInteraction.createImmediateResponder()
                .setContent("Vous avez commencé votre trajet pour aller à " + placeO.getString("name") + " en " + timeMillisToTravel / 1000 + " secondes.")
                .respond()
        }
        Main.buttonsManager.addButton(id4) { messageButtonEvent: MessageComponentCreateEvent ->
            verifButton(p, state, messageButtonEvent)
            val bal = p.getBal()
            check(bal >= priceToTravel) { "Vous n'avez pas assez de rb pour ce trajet" }
            p.setBal(bal - priceToTravel)

            // il a payé donc on téléporte le joueur
            p["place_DIBIMAP_type"] = "place"
            p["place_DIBIMAP_id"] = placeO.id.toString()
            p["place_DIBIMAP_x"] = placeO.getString("x")
            p["place_DIBIMAP_y"] = placeO.getString("y")

            // on dit au joueur qu'il est téléporté
            messageButtonEvent.messageComponentInteraction.createImmediateResponder()
                .setContent("Vous avez été téléporté à " + placeO.getString("name") + " car vous avez payé " + priceToTravel + " rb.")
                .respond()
        }
    }

    // set coordonnées par défaut
    private fun setCoos(p: Player) {
        // on met les coordonnées par défaut
        p["place_DIBIMAP_x"] = "400"
        p["place_DIBIMAP_y"] = "250"
        // et le type de lieu par défaut
        p["place_DIBIMAP_type"] = "coos"
    }
}