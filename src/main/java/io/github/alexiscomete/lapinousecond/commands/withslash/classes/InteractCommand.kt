package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.api
import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.view.ui.EmbedPagesWithInteractions
import io.github.alexiscomete.lapinousecond.view.ui.MenuBuilder
import io.github.alexiscomete.lapinousecond.modalManager
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.useful.managesave.generateUniqueID
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building
import io.github.alexiscomete.lapinousecond.worlds.buildings.Buildings
import io.github.alexiscomete.lapinousecond.worlds.buildings.Buildings.Companion.load
import io.github.alexiscomete.lapinousecond.worlds.places
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.TextInput
import org.javacord.api.entity.message.component.TextInputStyle
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.entity.server.invite.Invite
import org.javacord.api.entity.server.invite.InviteBuilder
import org.javacord.api.event.interaction.ButtonClickEvent
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color

class InteractCommandBase : Command(
    "interact",
    "Interact with your environment",
    "interact"
), ExecutableWithArguments {

    override val fullName: String
        get() = "interact"
    override val botPerms: Array<String>
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {

        val player = getAccount(slashCommand)
        val world = WorldEnum.valueOf(player["world"])
        when (player["place_${world.progName}_type"]) {
            "coos" -> {
                // on regarde s'il existe une ville à l'endroit où le joueur est
                val x = player["place_${world.progName}_x"].toInt()
                val y = player["place_${world.progName}_y"].toInt()
                val resultSet = saveManager.executeQuery(
                    "SELECT * FROM places WHERE x = $x AND y = $y AND world = '${world.progName}'",
                    true
                )
                    ?: throw IllegalArgumentException("Error while executing query")
                if (resultSet.next()) {
                    val place = places[resultSet.getString("id").toLong()]
                        ?: throw IllegalArgumentException("Place not found")
                    MenuBuilder(
                        "Interactions sur la case $x $y du monde ${world.nameRP}",
                        "Liste de toutes vos possibilités dans la version actuelle du bot. Les sorts ne sont pas compris.",
                        Color.BLUE,
                        slashCommand.user.id
                    )
                        .addButton(
                            "Entrer dans la ville",
                            "La ville ${place["nameRP"]} est ici ! Vous pouvez y entrer en cliquant sur ce bouton. Description : ${place["description"]}"
                        ) {
                            fun errorWithSuccess() {
                                it.buttonInteraction.createImmediateResponder()
                                    .setContent("Vous êtes maintenant dans la ville ${place["nameRP"]} ! Invitation impossible, le serveur n'est pas accessible.")
                                    .setFlags(MessageFlag.EPHEMERAL)
                                    .respond()
                            }

                            player["place_${world.progName}_type"] = "city"
                            player["place_${world.progName}_id"] = place["id"]
                            val serverId = place["server"]
                            val op = api.getServerById(serverId)
                            if (!op.isPresent) {
                                errorWithSuccess()
                                return@addButton
                            }
                            val server = op.get()
                            val firstChannel = server.textChannels.firstOrNull()
                            if (firstChannel == null) {
                                errorWithSuccess()
                                return@addButton
                            }
                            val invite: Invite = InviteBuilder(firstChannel)
                                .setMaxUses(42)
                                .setNeverExpire()
                                .create()
                                .join()

                            it.buttonInteraction.createImmediateResponder()
                                .setContent("Vous êtes maintenant dans la ville ${place["nameRP"]} ! Voici l'invitation pour rejoindre le serveur : ${invite.url}")
                                .setFlags(MessageFlag.EPHEMERAL)
                                .respond()

                            player["serv"] = serverId
                        }
                        .responder(slashCommand)
                } else {
                    slashCommand.createImmediateResponder()
                        .setContent("Il n'y a rien ici. De nouvelles interactions arriveront à l'avenir, pour le moment allez vers une ville")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond()
                }
            }

            "building" -> {
                val building = load(player["place_${world.progName}_id"])
                    ?: throw IllegalArgumentException("Building not found")
                MenuBuilder(
                    "Interactions sur le bâtiment ${building["nameRP"]} du monde ${world.nameRP}",
                    "Liste de toutes vos possibilités dans la version actuelle du bot.",
                    Color.BLUE,
                    slashCommand.user.id
                )
                    .addButton(
                        "Sortir du bâtiment",
                        "Vous sortez du bâtiment ${building["nameRP"]} et retournez dans la ville."
                    ) {
                        player["place_${world.progName}_type"] = "city"
                        it.buttonInteraction.createImmediateResponder()
                            .setContent("Vous êtes maintenant dans la ville ${building["nameRP"]} !")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond()
                    }
                    .addButton("Informations", "Vous regardez les informations du bâtiment ${building["nameRP"]}.") {
                        it.buttonInteraction.createImmediateResponder()
                            .setContent("${building.title()}\n${building.descriptionShort()}")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond()
                    }
                    .responder(slashCommand)
            }

            "city" -> {
                val place = places[player["place_${world.progName}_id"].toLong()]
                    ?: throw IllegalArgumentException("Place not found")

                /**
                 * It sets the player's place type to "building" and the player's place building id to the building's id,
                 * and then it sends a message to the player saying that they are now in the building
                 *
                 * @param building Building - The building that the player is entering
                 * @param buttonClickEvent The event that triggered the button click.
                 */
                fun enterInBuilding(building: Building, buttonClickEvent: ButtonClickEvent) {
                    player["place_${world.progName}_type"] = "building"
                    player["place_${world.progName}_building_id"] = building.id.toString()
                    buttonClickEvent.buttonInteraction.createImmediateResponder()
                        .setContent("Vous êtes maintenant dans le bâtiment ${building["nameRP"]} !")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond()
                }

                fun helpBuilding(building: Building, buttonClickEvent: ButtonClickEvent) {
                    // Etape 1 : demander au joueur le montant à entrer en montrant le montant nécessaire
                    // Etape 2 : vérifier que le joueur a assez d'argent et que le montant ne dépasse pas le montant nécessaire
                    // Etape 3 : retirer l'argent du joueur et ajouter l'argent au bâtiment

                    // Etape 1
                    val id = generateUniqueID()
                    val idMoney = generateUniqueID()

                    buttonClickEvent.buttonInteraction.respondWithModal(
                        id.toString(),
                        "Actuellement ${building["collect_value"]} / ${building["collect_target"]} rb",
                        ActionRow.of(
                            TextInput.create(
                                TextInputStyle.SHORT,
                                idMoney.toString(),
                                "Montant à donner"
                            )
                        )
                    )

                    modalManager.add(id) {
                        // Etape 2
                        val opMoney = it.modalInteraction.getTextInputValueByCustomId(idMoney.toString())
                        if (!opMoney.isPresent) {
                            throw IllegalArgumentException("Money not found")
                        }

                        var money = opMoney.get().toDouble()
                        if (money > player.getMoney()) {
                            throw IllegalArgumentException("You don't have enough money")
                        }

                        if (money > building["collect_target"].toDouble() - building["collect_value"].toDouble()) {
                            money = (building["collect_target"].toDouble() - building["collect_value"].toDouble())
                        }

                        // Etape 3
                        player.removeMoney(money)
                        building.addMoney(money)

                        it.modalInteraction.createImmediateResponder()
                            .setContent("Vous avez donné $money ${Resource.RABBIT_COIN.name_} au bâtiment ${building["nameRP"]} ! <@${building["owner"]}> peut vous remerciez !")
                            .respond()
                    }
                }

                fun sendBuildingsInEmbed(
                    embedBuilder: EmbedBuilder,
                    buildings: ArrayList<Building>,
                    it: ButtonClickEvent
                ) {
                    val embedPagesWithInteractions = EmbedPagesWithInteractions(
                        embedBuilder,
                        buildings,
                        { builder: EmbedBuilder, start: Int, max: Int, buildingsL: ArrayList<Building> ->
                            for (i in start until max) {
                                val building = buildingsL[i]
                                builder.addField(
                                    building.title(),
                                    building.descriptionShort()
                                )
                            }
                        }
                    ) { building: Building, buttonClickEvent: ButtonClickEvent ->
                        if (building["build_status"] == "building") {
                            helpBuilding(building, buttonClickEvent)
                        } else {
                            enterInBuilding(building, buttonClickEvent)
                        }
                    }
                    embedPagesWithInteractions.register()
                    it.buttonInteraction.createImmediateResponder()
                        .addEmbed(embedBuilder)
                        .addComponents(
                            ActionRow.of(embedPagesWithInteractions.buttons),
                            embedPagesWithInteractions.components
                        )
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond()
                }

                println("place: $place")

                MenuBuilder(
                    "Interactions dans la ville ${place["nameRP"]}",
                    "Liste de toutes vos possibilités dans la version actuelle du bot.",
                    Color.BLUE,
                    slashCommand.user.id
                )
                    .addButton(
                        "Quitter la ville",
                        "Vous quittez la ville ${place["nameRP"]} et retournez dans la nature"
                    ) {
                        player["place_${world.progName}_type"] = "coos"
                        player["place_${world.progName}_id"] = "0"
                        it.buttonInteraction.createImmediateResponder()
                            .setContent("Vous êtes maintenant dans la nature !")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond()
                    }
                    .addButton(
                        "Informations sur la ville",
                        "Vous pouvez voir les informations sur la ville ${place["nameRP"]} en cliquant sur ce bouton"
                    ) {
                        it.buttonInteraction.createImmediateResponder()
                            .setContent("La ville ${place["nameRP"]} est une ville aux coordonnées ${place["x"]} ${place["y"]} du monde ${world.nameRP}. Description : ${place["description"]}")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond()
                    }
                    .addButton(
                        "Interactions avec les bâtiments",
                        "Vous pouvez entrer dans un bâtiment, en financer un, ou simplement voir les vôtres."
                    ) { bat ->
                        MenuBuilder(
                            "Bâtiments",
                            "Interactions avec les bâtiments",
                            Color.BLUE,
                            bat.buttonInteraction.user.id
                        )
                            .addButton(
                                "Voir les bâtiments",
                                "Vous pouvez voir les bâtiments de la ville ${place["nameRP"]} en cliquant sur ce bouton et interagir avec eux"
                            ) {
                                val buildings = Buildings.loadBuildings(place["buildings"])
                                val embedBuilder = EmbedBuilder()
                                    .setTitle("Bâtiments de la ville ${place["nameRP"]}")
                                    .setDescription("Liste de tous les bâtiments de la ville ${place["nameRP"]}.")
                                    .setColor(Color.BLUE)
                                sendBuildingsInEmbed(embedBuilder, buildings, it)
                            }
                            .addButton(
                                "Vos bâtiments",
                                "Vous pouvez voir vos bâtiments en cliquant sur ce bouton et interagir avec eux"
                            ) { yours ->
                                val buildings = Buildings.loadBuildings(place["buildings"])
                                // remove if not with this owner
                                buildings.removeIf { building -> building["owner"] != player.id.toString() }
                                val embedBuilder = EmbedBuilder()
                                    .setTitle("Vos bâtiments de la ville ${place["nameRP"]}")
                                    .setDescription("Liste de tous vos bâtiments de la ville ${place["nameRP"]}.")
                                    .setColor(Color.BLUE)
                                val embedPagesWithInteractions = EmbedPagesWithInteractions(
                                    embedBuilder,
                                    buildings,
                                    { builder: EmbedBuilder, start: Int, max: Int, buildingsL: ArrayList<Building> ->
                                        for (i in start until max) {
                                            val building = buildingsL[i]
                                            builder.addField(
                                                building.title(),
                                                building.descriptionShort()
                                            )
                                        }
                                    }
                                ) { building: Building, buttonClickEvent: ButtonClickEvent ->
                                    if (building["build_status"] == "building") {
                                        // annuler ou aider le bâtiment
                                        MenuBuilder(
                                            "Bâtiment ${building["nameRP"]}",
                                            "Que voulez-vous faire avec le bâtiment ${building["nameRP"]} ?",
                                            Color.BLUE,
                                            buttonClickEvent.buttonInteraction.user.id
                                        )
                                            .addButton(
                                                "Annuler le bâtiment",
                                                "Vous annulez le bâtiment ${building["nameRP"]}"
                                            ) {
                                                // on le retire de la bdd
                                                saveManager.execute(
                                                    "DELETE FROM buildings WHERE id = ${building.id}",
                                                    true
                                                )
                                                it.buttonInteraction.createImmediateResponder()
                                                    .setContent("Vous avez annulé le bâtiment !")
                                                    .setFlags(MessageFlag.EPHEMERAL)
                                                    .respond()
                                            }
                                            .addButton(
                                                "Aider le bâtiment",
                                                "Vous aidez le bâtiment ${building["nameRP"]}"
                                            ) {
                                                helpBuilding(building, it)
                                            }
                                            .modif(buttonClickEvent)
                                    } else {
                                        enterInBuilding(building, buttonClickEvent)
                                    }
                                }
                                embedPagesWithInteractions.register()
                                yours.buttonInteraction.createImmediateResponder()
                                    .addEmbed(embedBuilder)
                                    .addComponents(
                                        ActionRow.of(embedPagesWithInteractions.buttons),
                                        embedPagesWithInteractions.components
                                    )
                                    .setFlags(MessageFlag.EPHEMERAL)
                                    .respond()
                            }
                            .addButton(
                                "Bâtiments en construction",
                                "Vous pouvez voir les bâtiments en construction dans la ville en cliquant sur ce bouton et interagir avec eux"
                            ) {
                                val buildings = Buildings.loadBuildings(place["buildings"])
                                // remove if not in construction
                                buildings.removeIf { building -> building["build_status"] != "building" }
                                val embedBuilder = EmbedBuilder()
                                    .setTitle("Bâtiments en construction de la ville ${place["nameRP"]}")
                                    .setDescription("Liste de tous les bâtiments en construction de la ville ${place["nameRP"]}.")
                                    .setColor(Color.BLUE)
                                sendBuildingsInEmbed(embedBuilder, buildings, it)
                            }
                            .addButton(
                                "Construire un bâtiment",
                                "Vous pouvez construire un bâtiment dans la ville ${place["nameRP"]} en cliquant sur ce bouton"
                            ) { again ->
                                val buildsTypes = arrayListOf(*Buildings.values())
                                val embedBuilder = EmbedBuilder()
                                    .setTitle("Bâtiments disponibles")
                                    .setDescription("Liste de tous les bâtiments disponibles.")
                                    .setColor(Color.BLUE)
                                val embedPagesWithInteractions = EmbedPagesWithInteractions(
                                    embedBuilder,
                                    buildsTypes,
                                    { builder: EmbedBuilder, start: Int, max: Int, buildsTypesL: ArrayList<Buildings> ->
                                        for (i in start until max) {
                                            val buildType = buildsTypesL[i]
                                            builder.addField(
                                                buildType.name_,
                                                buildType.basePrice.toString() + " " + Resource.RABBIT_COIN.name_ + " (Peut être construit : " + buildType.isBuild + ")"
                                            )
                                        }
                                    }
                                ) { buildType: Buildings, buttonClickEvent: ButtonClickEvent ->
                                    if (buildType.isBuild && buildType.buildingAutorisations?.isAutorise(player) == true) {
                                        val place1 = player.place
                                            ?: throw IllegalArgumentException("Le joueur n'est pas dans une ville")
                                        val building2 = Building(buildType, player, place1)
                                        val builder = EmbedBuilder()
                                            .setTitle(building2.title())
                                            .setDescription(building2.descriptionShort())
                                        buttonClickEvent.buttonInteraction.createImmediateResponder()
                                            .addEmbed(builder)
                                            .setFlags(MessageFlag.EPHEMERAL)
                                            .respond()
                                    } else {
                                        throw IllegalArgumentException("Vous ne pouvez pas construire ce bâtiment")
                                    }
                                }
                                embedPagesWithInteractions.register()
                                again.buttonInteraction.createImmediateResponder()
                                    .addEmbed(embedBuilder)
                                    .addComponents(
                                        ActionRow.of(embedPagesWithInteractions.buttons),
                                        embedPagesWithInteractions.components
                                    )
                                    .setFlags(MessageFlag.EPHEMERAL)
                                    .respond()
                            }
                            .modif(bat)
                    }
                    .responder(slashCommand)
            }

            else -> {
                slashCommand.createImmediateResponder()
                    .setContent("Aucune interaction possible ici")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond()
            }
        }
    }

}
