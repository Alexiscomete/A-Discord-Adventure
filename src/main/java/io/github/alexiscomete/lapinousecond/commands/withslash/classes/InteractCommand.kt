package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.api
import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import io.github.alexiscomete.lapinousecond.useful.managesave.saveManager
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum
import io.github.alexiscomete.lapinousecond.worlds.places
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.server.invite.Invite
import org.javacord.api.entity.server.invite.InviteBuilder
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
                // on regarde si il existe une ville à l'endroit où le joueur est
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
                        Color.BLUE
                    )
                        .addButton("Entrer dans la ville", "La ville ${place["nameRP"]} est ici ! Vous pouvez y entrer en cliquant sur ce bouton. Description : ${place["description"]}") {
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
                        }
                } else {
                    slashCommand.createImmediateResponder()
                        .setContent("Il n'y a rien ici. De nouvelles interactions arriveront à l'avenir, pour le moment allez vers une ville")
                        .setFlags(MessageFlag.EPHEMERAL)
                        .respond()
                }
            }
            "building" -> {
                val building = places[player["place_${world.progName}_id"].toLong()]
                    ?: throw IllegalArgumentException("Building not found")
                MenuBuilder(
                    "Interactions sur le bâtiment ${building["nameRP"]} du monde ${world.nameRP}",
                    "Liste de toutes vos possibilités dans la version actuelle du bot.",
                    Color.BLUE
                )
                    .addButton("Sortir du bâtiment", "Vous sortez du bâtiment ${building["nameRP"]} et retournez dans la ville.") {
                        player["place_${world.progName}_type"] = "city"
                        it.buttonInteraction.createImmediateResponder()
                            .setContent("Vous êtes maintenant dans la ville ${building["nameRP"]} !")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond()
                    }
                    .addButton("Informations", "Vous regardez les informations du bâtiment ${building["nameRP"]}.") {
                        it.buttonInteraction.createImmediateResponder()
                            .setContent("Le bâtiment ${building["nameRP"]} est un bâtiment ${building["type"]}. Description : ${building["description"]}")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond()
                    }
            }
            "city" -> {
                val place = places[player["place_${world.progName}_id"].toLong()]
                    ?: throw IllegalArgumentException("Place not found")
                MenuBuilder("Interactions dans la ville ${place["nameRP"]}", "Liste de toutes vos possibilités dans la version actuelle du bot.", Color.BLUE)
                    .addButton("Quitter la ville", "Vous quittez la ville ${place["nameRP"]} et retournez dans la nature") {
                        player["place_${world.progName}_type"] = "coos"
                        player["place_${world.progName}_id"] = "0"
                        it.buttonInteraction.createImmediateResponder()
                            .setContent("Vous êtes maintenant dans la nature !")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond()
                    }
                    .addButton("Voir les bâtiments", "Vous pouvez voir les bâtiments de la ville ${place["nameRP"]} en cliquant sur ce bouton et interagir avec eux") {
                        // TODO : interactions avec les bâtiments
                        it.buttonInteraction.createImmediateResponder()
                            .setContent("Les bâtiments de la ville ${place["nameRP"]} sont : ${place["buildings"]}")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond()
                    }
                    .addButton("Vos bâtiments", "Vous pouvez voir vos bâtiments en cliquant sur ce bouton et interagir avec eux") {
                        // TODO : interactions avec les bâtiments
                        it.buttonInteraction.createImmediateResponder()
                            .setContent("Vos bâtiments sont : ${player["buildings"]}")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond()
                    }
                    .addButton("Informations sur la ville", "Vous pouvez voir les informations sur la ville ${place["nameRP"]} en cliquant sur ce bouton") {
                        it.buttonInteraction.createImmediateResponder()
                            .setContent("La ville ${place["nameRP"]} est une ville aux coordonnées ${place["x"]} ${place["y"]} du monde ${world.nameRP}. Description : ${place["description"]}")
                            .setFlags(MessageFlag.EPHEMERAL)
                            .respond()
                    }
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
