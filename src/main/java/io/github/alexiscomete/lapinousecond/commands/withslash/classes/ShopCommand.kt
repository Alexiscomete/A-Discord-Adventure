package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.SubCommand
import io.github.alexiscomete.lapinousecond.resources.Resource
import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionChoice
import org.javacord.api.interaction.SlashCommandOptionType

class ShopCommandBase : Command(
    "shop",
    "Acheter / vendre items et resources pour des mauvais prix",
    "shop [[buy/sell] [name] <quantity>/list/info [name]]",
    inDms = false,
    subCommands = listOf(
        ShopBuyCommand(),
        ShopSellCommand(),
        ShopListCommand(),
        ShopInfoCommand()
    )
)

class ShopBuyCommand :
    SubCommand(
        "buy",
        "Acheter des items / resources pour un mauvais prix",
        arrayListOf(
            SlashCommandOption.createWithChoices(
                SlashCommandOptionType.STRING,
                "name",
                "Nom de l'item à acheter",
                true,
                run {
                    val arrayList = arrayListOf<SlashCommandOptionChoice>()
                    Resource.values().forEach {
                        arrayList.add(SlashCommandOptionChoice.create(it.name, it.progName))
                    }
                    return@run arrayList
                }
            ),
            SlashCommandOption.createDecimalOption("quantity", "Quantité à acheter", false, 1.0, Double.MAX_VALUE)
        )
    ),
    ExecutableWithArguments {

    override val botPerms: Array<String>?
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        TODO("Not yet implemented")
    }

}
