package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.api
import io.github.alexiscomete.lapinousecond.data.managesave.saveManager
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import io.github.alexiscomete.lapinousecond.view.ui.longuis.EmbedPagesWithInteractions
import io.github.alexiscomete.lapinousecond.view.ui.longuis.MenuBuilderUI
import io.github.alexiscomete.lapinousecond.view.ui.playerui.*
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building
import io.github.alexiscomete.lapinousecond.worlds.buildings.Buildings
import io.github.alexiscomete.lapinousecond.worlds.buildings.Buildings.Companion.load
import io.github.alexiscomete.lapinousecond.worlds.places
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.server.invite.Invite
import org.javacord.api.entity.server.invite.InviteBuilder
import org.javacord.api.interaction.Interaction
import org.javacord.api.interaction.SlashCommandInteraction

const val MAX_INVITATION_USAGES = 42

class InteractCommandBase : Command(
    "interact",
    "Interact with your environment",
), ExecutableWithArguments {

    override val fullName: String
        get() = "interact"
    override val botPerms: Array<String>
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {

        val player = getAccount(slashCommand)
        val playerData = player.playerData
        val world = WorldEnum.valueOf(playerData["world"])
        val context = contextFor(getAccount(slashCommand.user))
        val ui = DiscordPlayerUI(context, slashCommand as Interaction)

        when (playerData["place_${world.progName}_type"]) {
            "coos" -> {
                // on regarde s'il existe une ville à l'endroit où le joueur est
                val x = playerData["place_${world.progName}_x"].toInt()
                val y = playerData["place_${world.progName}_y"].toInt()
                val resultSet = saveManager.executeQuery(
                    "SELECT * FROM places WHERE x = $x AND y = $y AND world = '${world.progName}'",
                    true
                )
                    ?: throw IllegalArgumentException("Error while executing query")
                if (resultSet.next()) {
                    val place = places[resultSet.getString("id").toLong()]
                        ?: throw IllegalArgumentException("Place not found")
                    ui.setLongCustomUI(MenuBuilderUI(
                        "Interactions sur la case $x $y du monde ${world.nameRP}",
                        "Liste de toutes vos possibilités dans la version actuelle du bot. Les sorts ne sont pas compris.",
                        ui
                    )
                        .addButton(
                            "Entrer dans la ville",
                            "La ville ${place["nameRP"]} est ici ! Vous pouvez y entrer en cliquant sur ce bouton. Description : ${place["description"]}"
                        ) { playerUI ->
                            fun errorWithSuccess() {
                                playerUI.addMessage(
                                    Message("Vous êtes maintenant dans la ville ${place["nameRP"]} ! Invitation impossible, le serveur n'est pas accessible.")
                                )
                            }

                            playerData["place_${world.progName}_type"] = "city"
                            playerData["place_${world.progName}_id"] = place["id"]
                            val serverId = place["server"]
                            val op = api.getServerById(serverId)
                            if (!op.isPresent) {
                                errorWithSuccess()
                                return@addButton null
                            }
                            val server = op.get()
                            val firstChannel = server.textChannels.firstOrNull()
                            if (firstChannel == null) {
                                errorWithSuccess()
                                return@addButton null
                            }
                            val invite: Invite = InviteBuilder(firstChannel)
                                .setMaxUses(MAX_INVITATION_USAGES)
                                .setNeverExpire()
                                .create()
                                .join()

                            playerUI.addMessage(
                                Message("Vous êtes maintenant dans la ville ${place["nameRP"]} ! Voici l'invitation pour rejoindre le serveur : ${invite.url}")
                            )

                            playerData["serv"] = serverId
                            null
                        })
                } else {
                    slashCommand.createImmediateResponder()
                        .setContent("Il n'y a rien ici. De nouvelles interactions arriveront à l'avenir, pour le moment allez vers une ville")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond()
                }
            }

            "building" -> {
                val building = load(playerData["place_${world.progName}_id"])
                    ?: throw IllegalArgumentException("Building not found")
                ui.setLongCustomUI(MenuBuilderUI(
                    "Interactions sur le bâtiment ${building["nameRP"]} du monde ${world.nameRP}",
                    "Liste de toutes vos possibilités dans la version actuelle du bot.",
                    ui
                )
                    .addButton(
                        "Sortir du bâtiment",
                        "Vous sortez du bâtiment ${building["nameRP"]} et retournez dans la ville."
                    ) { playerUI ->
                        playerData["place_${world.progName}_type"] = "city"
                        playerUI.addMessage(
                            Message("Vous êtes maintenant dans la ville ${building["nameRP"]} !")
                        )
                        null
                    }
                    .addButton(
                        "Informations",
                        "Vous regardez les informations du bâtiment ${building["nameRP"]}."
                    ) { playerUI ->
                        playerUI.addMessage(
                            Message("${building.title()}\n${building.descriptionShort()}")
                        )
                        null
                    })
            }

            "city" -> {
                val place = places[playerData["place_${world.progName}_id"].toLong()]
                    ?: throw IllegalArgumentException("Place not found")

                /**
                 * It sets the player's place type to "building" and the player's place building id to the building's id,
                 * and then it sends a message to the player saying that they are now in the building
                 *
                 * @param building Building - The building that the player is entering
                 * @param buttonClickEvent The event that triggered the button click.
                 */
                fun enterInBuilding(building: Building, buttonClickEvent: PlayerUI) {
                    playerData["place_${world.progName}_type"] = "building"
                    playerData["place_${world.progName}_building_id"] = building.id.toString()
                    buttonClickEvent.addMessage(Message("Vous êtes maintenant dans le bâtiment ${building["nameRP"]} !"))
                }

                fun helpBuilding(building: Building, playerUI: PlayerUI): Question =
                // Etape 1 : demander au joueur le montant à entrer en montrant le montant nécessaire
                // Etape 2 : vérifier que le joueur a assez d'argent et que le montant ne dépasse pas le montant nécessaire
                // Etape 3 : retirer l'argent du joueur et ajouter l'argent au bâtiment

                    // Etape 1

                    Question(
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
                        if (money > player.ownerManager.getMoney()) {
                            throw IllegalArgumentException("You don't have enough money")
                        }

                        if (money > building["collect_target"].toDouble() - building["collect_value"].toDouble()) {
                            money = (building["collect_target"].toDouble() - building["collect_value"].toDouble())
                        }

                        // Etape 3
                        player.ownerManager.removeMoney(money)
                        building.addMoney(money)

                        playerUI.addMessage(
                            Message(
                                "Vous avez donné $money ${Resource.RABBIT_COIN.show} au bâtiment ${building["nameRP"]} ! <@${building["owner"]}> peut vous remerciez !"
                            )
                        )
                        null
                    }


                fun sendBuildingsInEmbed(
                    title: String,
                    description: String,
                    buildings: ArrayList<Building>
                ) {
                    ui.setLongCustomUI(
                        EmbedPagesWithInteractions(
                            buildings,
                            { start: Int, max: Int, buildingsL: ArrayList<Building> ->
                                (start until max).map { i ->
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
                }

                MenuBuilderUI(
                    "Interactions dans la ville ${place["nameRP"]}",
                    "Liste de toutes vos possibilités dans la version actuelle du bot.",
                    ui
                )
                    .addButton(
                        "Quitter la ville",
                        "Vous quittez la ville ${place["nameRP"]} et retournez dans la nature"
                    ) { playerUI ->
                        playerData["place_${world.progName}_type"] = "coos"
                        playerData["place_${world.progName}_id"] = "0"
                        playerUI.addMessage(Message("Vous êtes maintenant dans la nature !"))
                        null
                    }
                    .addButton(
                        "Informations sur la ville",
                        "Vous pouvez voir les informations sur la ville ${place["nameRP"]} en cliquant sur ce bouton"
                    ) { playerUI ->
                        playerUI.addMessage(
                            Message("La ville ${place["nameRP"]} est une ville aux coordonnées ${place["x"]} ${place["y"]} du monde ${world.nameRP}. Description : ${place["description"]}")
                        )
                        null
                    }
                    .addButton(
                        "Interactions avec les bâtiments",
                        "Vous pouvez entrer dans un bâtiment, en financer un, ou simplement voir les vôtres."
                    ) { playerUI ->
                        playerUI.setLongCustomUI(
                            MenuBuilderUI(
                                "Bâtiments",
                                "Interactions avec les bâtiments",
                                playerUI
                            )
                                .addButton(
                                    "Voir les bâtiments",
                                    "Vous pouvez voir les bâtiments de la ville ${place["nameRP"]} en cliquant sur ce bouton et interagir avec eux"
                                ) { _ ->
                                    val buildings = Buildings.loadBuildings(place["buildings"])
                                    sendBuildingsInEmbed(
                                        "Bâtiments de la ville ${place["nameRP"]}",
                                        "Liste de tous les bâtiments de la ville ${place["nameRP"]}.",
                                        buildings
                                    )
                                    null
                                }
                                .addButton(
                                    "Vos bâtiments",
                                    "Vous pouvez voir vos bâtiments en cliquant sur ce bouton et interagir avec eux"
                                ) { ui ->
                                    val buildings = Buildings.loadBuildings(place["buildings"])
                                    // remove if not with this owner
                                    buildings.removeIf { building -> building["owner"] != player.playerData.id.toString() }
                                    ui.setLongCustomUI(
                                        EmbedPagesWithInteractions(
                                            buildings,
                                            { start: Int, max: Int, buildingsL: ArrayList<Building> ->
                                                (start until max).map { i ->
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
                                                                null
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
                                                null
                                            },
                                            null,
                                            null,
                                            "Vos bâtiments de la ville ${place["nameRP"]}",
                                            "Liste de tous vos bâtiments de la ville ${place["nameRP"]}.",
                                            ui
                                        )
                                    )
                                    null
                                }
                                .addButton(
                                    "Bâtiments en construction",
                                    "Vous pouvez voir les bâtiments en construction dans la ville en cliquant sur ce bouton et interagir avec eux"
                                ) { _ ->
                                    val buildings = Buildings.loadBuildings(place["buildings"])
                                    // remove if not in construction
                                    buildings.removeIf { building -> building["build_status"] != "building" }
                                    sendBuildingsInEmbed(
                                        "Bâtiments en construction de la ville ${place["nameRP"]}",
                                        "Liste de tous les bâtiments en construction de la ville ${place["nameRP"]}.",
                                        buildings
                                    )
                                    null
                                }
                                .addButton(
                                    "Construire un bâtiment",
                                    "Vous pouvez construire un bâtiment dans la ville ${place["nameRP"]} en cliquant sur ce bouton"
                                ) { ui ->
                                    val buildsTypes = arrayListOf(*Buildings.entries.toTypedArray())
                                    ui.setLongCustomUI(
                                        EmbedPagesWithInteractions(
                                            buildsTypes,
                                            { start: Int, max: Int, buildsTypesL: ArrayList<Buildings> ->
                                                (start until max).map { i ->
                                                    val buildType = buildsTypesL[i]
                                                    Pair(
                                                        buildType.name.lowercase(),
                                                        buildType.basePrice.toString() + " " + Resource.RABBIT_COIN.show + " (Peut être construit : " + buildType.isBuild + ")"
                                                    )
                                                }
                                            },
                                            { buildType: Buildings, playerUI: PlayerUI ->
                                                if (buildType.isBuild && buildType.buildingAutorisations?.isAutorise(
                                                        player.ownerManager
                                                    ) == true
                                                ) {
                                                    val place1 = player.worldManager.entityWorld.place
                                                        ?: throw IllegalArgumentException("Le joueur n'est pas dans une ville")
                                                    val building2 = Building(buildType, player.ownerManager, place1)
                                                    playerUI.addMessage(
                                                        Message(
                                                            building2.title(),
                                                            building2.descriptionShort()
                                                        )
                                                    )
                                                    null
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
                                    null
                                })
                        null
                    }
            }

            else -> {
                slashCommand.createImmediateResponder()
                    .setContent("Aucune interaction possible ici")
                    .setFlags(MessageFlag.EPHEMERAL)
                    .respond()
            }
        }

        ui.updateOrSend()
        context.ui(ui)
    }

}
