package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.buttonsManager
import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandWithAccount
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.message_event.EmbedPages
import io.github.alexiscomete.lapinousecond.messagesManager
import io.github.alexiscomete.lapinousecond.selectMenuManager
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.worlds.*
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.component.SelectMenu
import org.javacord.api.entity.message.component.SelectMenuOption
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.event.interaction.SelectMenuChooseEvent
import org.javacord.api.event.message.MessageCreateEvent
import java.awt.Color
import kotlin.math.min

class PlaceCommand : CommandWithAccount(
    "Commande des lieux, configuration + description",
    "place",
    "Salut ! Je suis une commande. Pour créer un lieu faites place create_new_place, place list pour la list des lieux du serveur"
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {
        if (messageCreateEvent.message.isServerMessage) {
            val serverBot = servers[messageCreateEvent.server.get().id]
            if (serverBot == null) {
                messageCreateEvent.message.reply("Utilisez d'abord le -config")
            } else {
                if (args.size > 1) {
                    when (args[1]) {
                        "create_new_place" -> if (messageCreateEvent.messageAuthor.canManageRolesOnServer() && messageCreateEvent.messageAuthor.canManageServer() && messageCreateEvent.messageAuthor.canCreateChannelsOnServer()) {
                            if (messageCreateEvent.message.content.endsWith((p.id - 42).toString())) {
                                when (serverBot.getString("world")) {
                                    "NORMAL" -> createNormalPlace(messageCreateEvent, serverBot, p)
                                    "DIBIMAP" -> createWorldPlace(messageCreateEvent, serverBot, p, args)
                                    "TUTO" -> messageCreateEvent.message.reply("Le monde du tutoriel est privé")
                                    else -> messageCreateEvent.message.reply("Impossible de créer un lieu officiel pour ce monde")
                                }
                            } else {
                                messageCreateEvent.message.reply("**En créant un lieu**, vous garantissez que votre serveur est le **serveur officiel** de **ce lieu**. Si ce n' est pas le cas les modérateurs du bot pourront supprimer le lieu et infliger une pénalité pour le **serveur** sur le bot (ou même une **réinitialisation**). Il existe **d' autres façon** de créer un lieu **non officiel**. Tapez **" + (p.id - 42) + "** à la fin de la **même commande** pour valider")
                                messageCreateEvent.message.delete()
                            }
                        } else {
                            messageCreateEvent.message.reply("Vous devez avoir la permission de gérer les rôles, le serveur et les salons pour utiliser cette commande")
                        }
                        "list" -> {
                            val places = Place.getPlacesWithWorld(serverBot.getString("world"))
                            val messageBuilder = MessageBuilder()
                            val builder = EmbedBuilder()
                            setPlaceEmbed(builder, 0, 11, places)
                            val embedPages1 = EmbedPages(
                                builder,
                                places
                            ) { embedBuilder: EmbedBuilder, min: Int, max: Int, places: ArrayList<Place> ->
                                setPlaceEmbed(
                                    embedBuilder,
                                    min,
                                    max,
                                    places
                                )
                            }
                            messageBuilder.addComponents(embedPages1.components)
                            messageBuilder.setEmbed(builder)
                            embedPages1.register()
                            messageBuilder.send(messageCreateEvent.channel)
                        }
                        "links" -> {
                            messageCreateEvent.message.reply("Tout les lieux qui ont un lien de voyage avec le lieu de votre serveur")
                            val place = places[serverBot.getString("places").toLong()]
                            val places1 = Place.toPlaces(place!!.getString("connections"))
                            val messageBuilder1 = MessageBuilder()
                            val builder1 = EmbedBuilder()
                            setPlaceEmbed(builder1, 0, min(places1.size, 11), places1)
                            val embedPages2 = EmbedPages(
                                builder1,
                                places1
                            ) { embedBuilder: EmbedBuilder, min: Int, max: Int, places: ArrayList<Place> ->
                                setPlaceEmbed(
                                    embedBuilder,
                                    min,
                                    max,
                                    places
                                )
                            }
                            messageBuilder1.addComponents(embedPages2.components)
                            messageBuilder1.setEmbed(builder1)
                            embedPages2.register()
                            messageBuilder1.send(messageCreateEvent.channel)
                        }
                        "add_link" -> if (args.size > 2) {
                            try {
                                val place1 = places[serverBot.getString("places").toLong()]
                                val place2 = places[args[2].toLong()]
                                if (place1!!.getString("world") == place2!!.getString("world")) {
                                    if (place1.getString("world") == "NORMAL") {
                                        if (p["bal"] == "") {
                                            p["bal"] = "0.0"
                                        }
                                        val bal = p["bal"].toDouble()
                                        if (bal < 500) {
                                            messageCreateEvent.message.reply("Impossible de créer un lien : vous devez avoir au minimum 500 rb")
                                            return
                                        }
                                        val connections1 = Place.toPlaces(place1.getString("connections"))
                                        val connections2 = Place.toPlaces(place2.getString("connections"))
                                        if (connections1.contains(place2)) {
                                            messageCreateEvent.message.reply("Cette connection existe déjà")
                                            return
                                        }
                                        p["bal"] = (bal - 500).toString()
                                        connections1.add(place2)
                                        connections2.add(place1)
                                        place1["connections"] = placesToString(connections1)
                                        place2["connections"] = placesToString(connections2)
                                    } else {
                                        messageCreateEvent.message.reply("Impossible pour ce monde pour le moment")
                                    }
                                } else {
                                    messageCreateEvent.message.reply("Ce lieu n'est pas dans le même monde, donc pas de route entre les 2")
                                }
                            } catch (e: NumberFormatException) {
                                messageCreateEvent.message.reply("Ceci n'est pas un nombre valide (arg 2)")
                            }
                        } else {
                            messageCreateEvent.message.reply("Action impossible : précisez l'id du lieu pour créer un lien")
                        }
                        "add_zone" -> {
                            val placeParent = getPlaceParent(serverBot)
                            require(placeParent.getString("world") == "DIBIMAP") { "Ce monde ne prend pas en charge les zones" }
                            require(args.size >= 6) { "Il manque des arguments pour créer une zone : x1 y1 x2 y2" }
                            require(
                                !(isNotNumeric(args[2]) || isNotNumeric(args[3]) || isNotNumeric(args[4]) || isNotNumeric(
                                    args[5]
                                ))
                            ) { "Les coordonnées doivent être des nombres" }
                            var x1 = args[2].toInt()
                            var y1 = args[3].toInt()
                            var x2 = args[4].toInt()
                            var y2 = args[5].toInt()
                            if (x1 > x2) {
                                val tmp = x1
                                x1 = x2
                                x2 = tmp
                            }
                            if (y1 > y2) {
                                val tmp = y1
                                y1 = y2
                                y2 = tmp
                            }
                            require(!(x1 < 0 || y1 < 0)) { "Les coordonnées doivent être positives" }
                            require(!(WorldEnum.DIBIMAP.world.mapHeight < y2 || WorldEnum.DIBIMAP.world.mapWidth < x2)) { "Les coordonnées sont en dehors de la carte" }
                            val placeZones = PlaceZones(placeParent.id)
                            val zone = Zone(x1, y1, x2, y2)
                            placeZones.addZone(zone)
                            messageCreateEvent.message.reply("Zone ajoutée")
                        }
                        "del_zone" -> {
                            val placeParentDel = getPlaceParent(serverBot)
                            require(placeParentDel.getString("world") == "DIBIMAP") { "Ce monde ne prend pas en charge les zones" }
                            val placeZonesDel = PlaceZones(placeParentDel.id)
                            val options = ArrayList<SelectMenuOption>()
                            for ((i, zoneDel) in placeZonesDel.zones.withIndex()) {
                                options.add(SelectMenuOption.create(i.toString(), zoneDel.toString()))
                            }
                            val id = generateUniqueID()
                            val actionRow = ActionRow.of(SelectMenu.create(id.toString(), "Zone à supprimer", options))
                            MessageBuilder()
                                .setContent("Zone à supprimer")
                                .addComponents(actionRow)
                                .send(messageCreateEvent.channel)
                            selectMenuManager
                                .add(id) { messageComponentCreateEvent: SelectMenuChooseEvent ->
                                    val mci = messageComponentCreateEvent.selectMenuInteraction
                                    if (mci.user.id == messageCreateEvent.messageAuthor.id) {
                                        val index = mci.chosenOptions[0].label.toInt()
                                        placeZonesDel.removeZone(index)
                                        messageCreateEvent.message.reply("Zone supprimée")
                                    }
                                }
                        }
                        "modify_zone" -> {
                            val placeParentModify = getPlaceParent(serverBot)
                            require(placeParentModify.getString("world") == "DIBIMAP") { "Ce monde ne prend pas en charge les zones" }
                            val placeZonesModify = PlaceZones(placeParentModify.id)
                            val optionsModify = ArrayList<SelectMenuOption>()
                            for ((iModify, zoneModify) in placeZonesModify.zones.withIndex()) {
                                optionsModify.add(SelectMenuOption.create(iModify.toString(), zoneModify.toString()))
                            }
                            val idModify = generateUniqueID()
                            val actionRowModify =
                                ActionRow.of(SelectMenu.create(idModify.toString(), "Zone à modifier", optionsModify))
                            MessageBuilder()
                                .setContent("Zone à modifier")
                                .addComponents(actionRowModify)
                                .send(messageCreateEvent.channel)
                            selectMenuManager
                                .add(idModify) { messageComponentCreateEvent: SelectMenuChooseEvent ->
                                    val mci = messageComponentCreateEvent.selectMenuInteraction
                                    if (mci.user.id == messageCreateEvent.messageAuthor.id) {
                                        val index = mci.chosenOptions[0].label.toInt()
                                        val zoneModify = placeZonesModify.zones[index]
                                        messageCreateEvent.message.reply("Zone à modifier : $zoneModify")
                                        // changement des coordonnées de la zone x1, y1, x2, y2
                                        messageCreateEvent.message.reply("Nouvelles coordonnées : ")
                                        messagesManager.addListener(
                                            messageCreateEvent.channel,
                                            messageCreateEvent.messageAuthor.id
                                        ) { messageCreateEvent1: MessageCreateEvent ->
                                            val split = messageCreateEvent1.message.content.split(" ".toRegex())
                                                .dropLastWhile { it.isEmpty() }
                                                .toTypedArray()
                                            require(split.size >= 4) { "Vous devez fournir 4 coordonnées : x1, y1, x2, y2" }
                                            val x1_ = split[0].toInt()
                                            val y1_ = split[1].toInt()
                                            val x2_ = split[2].toInt()
                                            val y2_ = split[3].toInt()
                                            zoneModify.x1 = x1_
                                            zoneModify.y1 = y1_
                                            zoneModify.x2 = x2_
                                            zoneModify.y2 = y2_
                                            placeZonesModify.updateBDD()
                                        }
                                    }
                                }
                        }
                        else -> messageCreateEvent.message.reply("Action inconnue")
                    }
                } else {
                    messageCreateEvent.message.reply("Actions possibles : create_new_place, links, add_link, list")
                }
            }
        }
    }

    private fun createNormalPlace(messageCreateEvent: MessageCreateEvent, serverBot: ServerBot, p: Player) {
        if (serverBot.getArray("places").isEmpty() || serverBot.getArray("places")[0] == "") {
            serverPlace(messageCreateEvent, serverBot, p)
        } else {
            messageCreateEvent.message.reply("Impossible : un serveur du monde normal ne peut avoir qu' un seul lieu")
        }
    }

    private fun serverPlace(
        messageCreateEvent: MessageCreateEvent,
        serverBot: ServerBot,
        p: Player,
        doAfter: () -> Unit = {}
    ) {
        val place = Place()
            .setAndGet("name", serverBot.getString("namerp"))
            .setAndGet("world", serverBot.getString("world"))
            .setAndGet("serv", serverBot.id.toString())
            .setAndGet("type", "server")
            .setAndGet("train", serverBot.getString("welcome"))
            .setAndGet("descr", serverBot.getString("descr"))
        messageCreateEvent.message.reply(place.placeEmbed)
        serverBot["places"] = place.id.toString()
        messageCreateEvent.message.reply("Message de départ du lieu :")
        messagesManager.setValueAndRetry(
            messageCreateEvent.channel,
            p.id,
            "traout",
            "Message de sortie mit à jour, configuration terminée du lieu de serveur. Comment voyager vers d' autres lieux dans le monde NORMAL ? Dans ce monde les joueurs dans un serveur peuvent payer pour créer une connection (nom RP à trouver) entre 2 lieux. Dans le lieu DIBIMAP aucune connection existe, ce sont directement des déplacements.",
            1500,
            serverBot
        ) { doAfter() }
    }

    private fun createWorldPlace(
        messageCreateEvent: MessageCreateEvent,
        serverBot: ServerBot,
        p: Player,
        args: Array<String>
    ) {
        val doAfter: () -> Unit = {
            messageCreateEvent.message.reply("ATTENTION : la création d'un lieu dans ce monde est long. Vous devez indiquer x et y pour le lieu dans la commande, ajouter le code à la fin du message et ajouter bien configurer les zones.\nContinuer ?")
            val yes = generateUniqueID()
            val no = generateUniqueID()
            buttonsManager.addButton(yes) { messageComponentCreateEvent: ButtonClickEvent ->
                if (messageComponentCreateEvent.buttonInteraction.user.id == p.id) {
                    if (serverBot.getArray("places").size == 1 && serverBot.getArray("places")[0] == "") {
                        serverPlace(messageCreateEvent, serverBot, p)
                    } else {
                        // récupération du lieu parent
                        val placeParent = getPlaceParent(serverBot)
                        val placeZones = PlaceZones(placeParent.id)
                        if (args.size < 4) {
                            sendArgs(messageCreateEvent, p)
                            return@addButton
                        }
                        if (isNotNumeric(args[2])) {
                            sendNumberEx(messageCreateEvent, p, 2)
                            return@addButton
                        }
                        if (isNotNumeric(args[3])) {
                            sendNumberEx(messageCreateEvent, p, 3)
                            return@addButton
                        }
                        val x = args[2].toInt()
                        val y = args[3].toInt()
                        if (!placeZones.isInZones(x, y)) {
                            throw RuntimeException("Impossible de créer un lieu dans cet emplacement : votre serveur n'a pas de zone à cet emplacement")
                        }
                        val place = Place()
                            .setAndGet("world", serverBot.getString("world"))
                            .setAndGet("serv", serverBot.id.toString())
                            .setAndGet("type", "city")
                            .setAndGet("city_size", "1")
                            .setAndGet("x", args[2])
                            .setAndGet("y", args[3])
                        messageCreateEvent.message.reply(place.placeEmbed)
                        serverBot["places"] = place.id.toString()
                        messageCreateEvent.message.reply("Message de départ du lieu :")
                        messagesManager.setValueAndRetry(
                            messageCreateEvent.channel,
                            p.id,
                            "traout",
                            "Message de sortie mit à jour. Message d'arrivée du lieu :",
                            1500,
                            serverBot
                        ) {
                            messagesManager.setValueAndRetry(
                                messageCreateEvent.channel,
                                p.id,
                                "train",
                                "Message d'arrivée du lieu mit à jour. Nom du lieu :",
                                1500,
                                serverBot
                            ) {
                                messagesManager.setValueAndRetry(
                                    messageCreateEvent.channel,
                                    p.id,
                                    "name",
                                    "Nom du lieu mit à jour. Description du lieu :",
                                    1500,
                                    serverBot
                                ) {
                                    messagesManager.setValueAndRetry(
                                        messageCreateEvent.channel,
                                        p.id,
                                        "descr",
                                        "Description du lieu mit à jour. Configuration terminée pour cette ville.",
                                        1500,
                                        serverBot
                                    ) {}
                                }
                            }
                        }
                        // fin de la configuration des villes
                    }
                }
            }
            buttonsManager.addButton(no) { messageComponentCreateEvent: ButtonClickEvent ->
                if (messageComponentCreateEvent.buttonInteraction.user.id == p.id) {
                    messageComponentCreateEvent.buttonInteraction.message.delete()
                }
            }
            val messageBuilder = MessageBuilder()
            messageBuilder.addEmbed(
                EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setTitle("Oui ou non ?")
            )
            messageBuilder.addComponents(
                ActionRow.of(
                    Button.success(yes.toString(), "Oui"),
                    Button.danger(no.toString(), "Non")
                )
            )
            messageBuilder.send(messageCreateEvent.channel)
        }

        if (serverBot.getArray("places").isEmpty() || serverBot.getArray("places")[0] == "") {
            serverPlace(messageCreateEvent, serverBot, p, doAfter)
        } else {
            doAfter()
        }

    }

    private fun placesToString(places: ArrayList<Place>): String {
        if (places.size == 0) {
            return ""
        }
        val builder = StringBuilder()
            .append(places[0].id)
        for (i in 1 until places.size) {
            builder.append(";")
                .append(places[i])
        }
        return builder.toString()
    }

    private fun setPlaceEmbed(embedBuilder: EmbedBuilder, min: Int, max: Int, places: ArrayList<Place>) {
        embedBuilder
            .setTitle("Liste des lieux de $min à $max")
            .setColor(Color.ORANGE)
            .setDescription("Utilisez les boutons pour voir les autres pages")
        for (i in min until max) {
            val place = places[i]
            embedBuilder.addField(
                if (place.getString("name") == "") "Nom invalide" else place.getString("name"),
                place.id.toString() + " -> " + place.getString("descr")
            )
        }
    }

    private fun getPlaceParent(serverBot: ServerBot): Place {
        var placeParent: Place? = null
        for (placeID in serverBot.getArray("places")) {
            try {
                val place = places[placeID.toLong()]
                if (place != null && place.getString("type") == "server") {
                    placeParent = place
                }
            } catch (ignored: Exception) {
            }
        }
        if (placeParent == null) {
            throw RuntimeException("Impossible de trouver le lieu parent.")
        }
        return placeParent
    }
}