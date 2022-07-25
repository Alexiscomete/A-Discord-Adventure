package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.SubCommand
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color

class HelpCommandBase : Command(
    "help",
    "Aide interactive pour le bot, pas seulement les commandes",
    "help [main]",
    subCommands = listOf(
        HelpCommandMain()
    )
)

class HelpCommandMain : SubCommand(
    "main",
    "Aide interactive pour le bot, pas seulement les commandes"
), ExecutableWithArguments {
    override val fullName: String
        get() = "help main"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        slashCommand.createImmediateResponder()
            .addEmbed(
                EmbedBuilder()
                    .setColor(Color.CYAN)
                    .setDescription("Ici je vais répondre à toutes vos questions. Pour commencer je vous invite à regarder le wiki (https://dirtybiologistan.fandom.com/fr/wiki/Lapinou_second) pour répondre à la plupart de vos questions. C'est plus confortable que lire sur discord. Pour inviter le bot un bouton est sur son profil.")
                    .setTitle("Aide interactive pour le bot, pas seulement les commandes")
                    .addField("Configurer le bot sur votre serveur", "Il suffit de faire .....")
                    .addField("Pour commencer", "Utiliser la commande `/account start` commencera le tutoriel. Suivez les instructions jusqu'au bout ! Pensez à régulièrement utiliser la commande `/work all` pour faire gagner les ressources basiques du bot.")
                    .addField("Economie", "L'économie est équilibrée. `work` et ses sous commandes font entrer de l'argent dans l'économie. `shop`, qui permet d'échanger de nombreuses choses, a un mauvais taut de change pour faire sortir l'argent de l'économie. ......")
                    .addField("Voyage", "Il est important de voyager dans ce bot. C'est encore une mécanique à retravailler .......")
            )
            .respond()
    }
}