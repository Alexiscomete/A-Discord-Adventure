package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.SubCommand
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.SlashCommandInteraction
import java.awt.Color

class HelpCommandBase : Command(
    "help",
    "Aide interactive pour le bot, pas seulement les commandes",
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
                    .addField("Configurer le bot sur votre serveur", "Il suffit de faire `/config` ! C'est tout ! Pour modifier la configuration il suffit de faire à nouveau `/config`. Si vous êtes un département alors vous devez faire `/config` pour ajouter des villes.")
                    .addField("Pour commencer", "Utiliser la commande `/account start` commencera le tutoriel. Suivez les instructions jusqu'au bout ! Pensez à régulièrement utiliser la commande `/work all` pour faire gagner les ressources basiques du bot.")
                    .addField("Economie", "L'économie est équilibrée. `/work` et ses sous commandes font entrer de l'argent dans l'économie. `/shop`, qui permet d'échanger de les ressources de base, mais `/market` permet d'échanger de plus de façons différentes et avec les autres joueurs. Cette dernière commande permet aussi d'échapper aux impôts de Lapinou second.")
                    .addField("Voyage", "Il est important de voyager dans ce bot. De nombreuses fonctionnalités sont à venir avec ce système. Tout ce qui est en rapport avec le voyage est dans `/map`. map > voyager propose les options pour voyager (Aller à ou Mondes) et map > Cartes > ma position permet de se repérer et de voir les villes. Utilisez ensuite `/interact`.")
            )
            .respond()
    }
}