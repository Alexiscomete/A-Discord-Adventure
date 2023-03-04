package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.api
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.entity.resources.Resource
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.contextmanager.ModalContextManager
import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import io.github.alexiscomete.lapinousecond.view.ui.longuis.EmbedPagesWithInteractions
import io.github.alexiscomete.lapinousecond.view.ui.longuis.MenuBuilderUI
import io.github.alexiscomete.lapinousecond.view.ui.old.MenuBuilder
import io.github.alexiscomete.lapinousecond.view.ui.playerui.*
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building
import io.github.alexiscomete.lapinousecond.worlds.buildings.Buildings
import io.github.alexiscomete.lapinousecond.worlds.buildings.Buildings.Companion.load
import io.github.alexiscomete.lapinousecond.worlds.places
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.server.invite.Invite
import org.javacord.api.entity.server.invite.InviteBuilder
import org.javacord.api.event.interaction.ModalSubmitEvent
import org.javacord.api.interaction.Interaction
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
        val context = contextFor(getAccount(slashCommand.user))
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
                        context
                    )
                        .addButton(
                            "Entrer dans la ville",
                            "La ville ${place["nameRP"]} est ici ! Vous pouvez y entrer en cliquant sur ce bouton. Description : ${place["description"]}"
                        ) { it, _, _ ->
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
                    context
                )
                    .addButton(
                        "Sortir du bâtiment",
                        "Vous sortez du bâtiment ${building["nameRP"]} et retournez dans la ville."
                    ) { it, _, _ ->
                        player["place_${world.progName}_type"] = "city"
                        it.buttonInteraction.createImmediateResponder()
                            .setContent("Vous êtes maintenant dans la ville ${building["nameRP"]} !")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond()
                    }
                    .addButton(
                        "Informations",
                        "Vous regardez les informations du bâtiment ${building["nameRP"]}."
                    ) { it, _, _ ->
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
                fun enterInBuilding(building: Building, buttonClickEvent: PlayerUI) {
                    player["place_${world.progName}_type"] = "building"
                    player["place_${world.progName}_building_id"] = building.id.toString()
                    buttonClickEvent.addMessage(Message("Vous êtes maintenant dans le bâtiment ${building["nameRP"]} !"))
                }

                fun helpBuilding(building: Building, playerUI: PlayerUI): Question {
                    // Etape 1 : demander au joueur le montant à entrer en montrant le montant nécessaire
                    // Etape 2 : vérifier que le joueur a assez d'argent et que le montant ne dépasse pas le montant nécessaire
                    // Etape 3 : retirer l'argent du joueur et ajouter l'argent au bâtiment

                    // Etape 1

                    return Question(
                        "Actuellement ${building["collect_value"]} / ${building["collect_target"]} rb",
                        QuestionField(
                            "Montant à donner",
                            shortAnswer = true,
                            required = true
                        )
                    ) {
                        // Etape 2
                        val opMoney = it.field0.answer
                        if (opMoney != "") {
                            throw IllegalArgumentException("Money not found")
                        }

                        var money = opMoney.toDouble()
                        if (money > player.getMoney()) {
                            throw IllegalArgumentException("You don't have enough money")
                        }

                        if (money > building["collect_target"].toDouble() - building["collect_value"].toDouble()) {
                            money = (building["collect_target"].toDouble() - building["collect_value"].toDouble())
                        }

                        // Etape 3
                        player.removeMoney(money)
                        building.addMoney(money)

                        playerUI.addMessage(
                            Message(
                                "Vous avez donné $money ${Resource.RABBIT_COIN.show} au bâtiment ${building["nameRP"]} ! <@${building["owner"]}> peut vous remerciez !"
                            )
                        )
                        return@Question null
                    }
                }

                fun sendBuildingsInEmbed(
                    title: String,
                    description: String,
                    buildings: ArrayList<Building>,
                    context: Context,
                    city: Interaction
                ) {
                    val ui = DiscordPlayerUI(context, city)
                    ui.setLongCustomUI(
                        EmbedPagesWithInteractions(
                            buildings,
                            { start: Int, max: Int, buildingsL: ArrayList<Building> ->
                                return@EmbedPagesWithInteractions (start until max).map { i ->
                                    val building = buildingsL[i]
                                    Pair(building.title(), building.descriptionShort())
                                }
                            },
                            { building: Building, playerUI: PlayerUI ->
                                if (building["build_status"] == "building") {
                                    helpBuilding(building, playerUI)
                                } else {
                                    enterInBuilding(building, playerUI)
                                    null
                                }
                            },
                            null,
                            null,
                            title,
                            description,
                            ui
                        )
                    )
                    ui.updateOrSend()
                    context.ui(ui)
                }

                MenuBuilder(
                    "Interactions dans la ville ${place["nameRP"]}",
                    "Liste de toutes vos possibilités dans la version actuelle du bot.",
                    Color.BLUE,
                    context
                )
                    .addButton(
                        "Quitter la ville",
                        "Vous quittez la ville ${place["nameRP"]} et retournez dans la nature"
                    ) { it, _, _ ->
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
                    ) { it, _, _ ->
                        it.buttonInteraction.createImmediateResponder()
                            .setContent("La ville ${place["nameRP"]} est une ville aux coordonnées ${place["x"]} ${place["y"]} du monde ${world.nameRP}. Description : ${place["description"]}")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond()
                    }
                    .addButton(
                        "Interactions avec les bâtiments",
                        "Vous pouvez entrer dans un bâtiment, en financer un, ou simplement voir les vôtres."
                    ) { bat, c1, _ ->
                        MenuBuilder(
                            "Bâtiments",
                            "Interactions avec les bâtiments",
                            Color.BLUE,
                            c1
                        )
                            .addButton(
                                "Voir les bâtiments",
                                "Vous pouvez voir les bâtiments de la ville ${place["nameRP"]} en cliquant sur ce bouton et interagir avec eux"
                            ) { it, _, _ ->
                                val buildings = Buildings.loadBuildings(place["buildings"])
                                sendBuildingsInEmbed(
                                    "Bâtiments de la ville ${place["nameRP"]}",
                                    "Liste de tous les bâtiments de la ville ${place["nameRP"]}.",
                                    buildings,
                                    c1,
                                    it.interaction
                                )
                            }
                            .addButton(
                                "Vos bâtiments",
                                "Vous pouvez voir vos bâtiments en cliquant sur ce bouton et interagir avec eux"
                            ) { yours, c2, _ ->
                                val buildings = Buildings.loadBuildings(place["buildings"])
                                // remove if not with this owner
                                buildings.removeIf { building -> building["owner"] != player.id.toString() }
                                val ui = DiscordPlayerUI(context, yours.interaction)
                                ui.setLongCustomUI(
                                    EmbedPagesWithInteractions(
                                        buildings,
                                        { start: Int, max: Int, buildingsL: ArrayList<Building> ->
                                            return@EmbedPagesWithInteractions (start until max).map { i ->
                                                val building = buildingsL[i]
                                                Pair(building.title(), building.descriptionShort())
                                            }
                                        },
                                        { building: Building, playerUI: PlayerUI ->
                                            if (building["build_status"] == "building") {
                                                // annuler ou aider le bâtiment
                                                playerUI.setLongCustomUI(
                                                    MenuBuilderUI(
                                                        "Bâtiment ${building["nameRP"]}",
                                                        "Que voulez-vous faire avec le bâtiment ${building["nameRP"]} ?",
                                                        playerUI
                                                    )
                                                        .addButton(
                                                            "Annuler le bâtiment",
                                                            "Vous annulez le bâtiment ${building["nameRP"]}"
                                                        ) { playerUI1: PlayerUI ->
                                                            // on le retire de la bdd
                                                            saveManager.execute(
                                                                "DELETE FROM buildings WHERE id = ${building.id}",
                                                                true
                                                            )
                                                            playerUI1.addMessage(Message("Vous avez annulé le bâtiment !"))
                                                            return@addButton null
                                                        }
                                                        .addButton(
                                                            "Aider le bâtiment",
                                                            "Vous aidez le bâtiment ${building["nameRP"]}"
                                                        ) { playerUI1: PlayerUI ->
                                                            helpBuilding(building, playerUI1)
                                                        }
                                                )
                                            } else {
                                                enterInBuilding(building, playerUI)
                                            }
                                            return@EmbedPagesWithInteractions null
                                        },
                                        null,
                                        null,
                                        "Vos bâtiments de la ville ${place["nameRP"]}",
                                        "Liste de tous vos bâtiments de la ville ${place["nameRP"]}.",
                                        ui
                                    )
                                )
                                ui.updateOrSend()
                                c2.ui(ui)
                            }
                            .addButton(
                                "Bâtiments en construction",
                                "Vous pouvez voir les bâtiments en construction dans la ville en cliquant sur ce bouton et interagir avec eux"
                            ) { it, eyufg, _ ->
                                val buildings = Buildings.loadBuildings(place["buildings"])
                                // remove if not in construction
                                buildings.removeIf { building -> building["build_status"] != "building" }
                                sendBuildingsInEmbed(
                                    "Bâtiments en construction de la ville ${place["nameRP"]}",
                                    "Liste de tous les bâtiments en construction de la ville ${place["nameRP"]}.",
                                    buildings,
                                    eyufg,
                                    it.interaction
                                )
                            }
                            .addButton(
                                "Construire un bâtiment",
                                "Vous pouvez construire un bâtiment dans la ville ${place["nameRP"]} en cliquant sur ce bouton"
                            ) { again, c2, _ ->
                                val buildsTypes = arrayListOf(*Buildings.values())
                                val ui = DiscordPlayerUI(context, again.interaction)
                                ui.setLongCustomUI(
                                    EmbedPagesWithInteractions(
                                        buildsTypes,
                                        { start: Int, max: Int, buildsTypesL: ArrayList<Buildings> ->
                                            return@EmbedPagesWithInteractions (start until max).map { i ->
                                                val buildType = buildsTypesL[i]
                                                Pair(
                                                    buildType.name.lowercase(),
                                                    buildType.basePrice.toString() + " " + Resource.RABBIT_COIN.show + " (Peut être construit : " + buildType.isBuild + ")"
                                                )
                                            }
                                        },
                                        { buildType: Buildings, playerUI: PlayerUI ->
                                            if (buildType.isBuild && buildType.buildingAutorisations?.isAutorise(player) == true) {
                                                val place1 = player.place
                                                    ?: throw IllegalArgumentException("Le joueur n'est pas dans une ville")
                                                val building2 = Building(buildType, player, place1)
                                                playerUI.addMessage(
                                                    Message(
                                                        building2.title(),
                                                        building2.descriptionShort()
                                                    )
                                                )
                                                return@EmbedPagesWithInteractions null
                                            } else {
                                                throw IllegalArgumentException("Vous ne pouvez pas construire ce bâtiment")
                                            }
                                        },
                                        null,
                                        null,
                                        "Bâtiments disponibles",
                                        "Liste de tous les bâtiments disponibles.",
                                        ui
                                    )
                                )
                                ui.updateOrSend()
                                c2.ui(ui)
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
