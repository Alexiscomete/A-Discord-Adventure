package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.SubCommand
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.resources.ResourceManager
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.SlashCommandInteraction
import org.javacord.api.interaction.SlashCommandOption
import org.javacord.api.interaction.SlashCommandOptionChoice
import org.javacord.api.interaction.SlashCommandOptionType
import java.util.*

private val buyCoef = 1.1
private val sellCoef = 0.9

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
        val arguments = slashCommand.arguments
        val resourceArgument = arguments.first { it.name == "name" }
            ?: throw IllegalArgumentException("Missing resource name")
        val opStringResource = resourceArgument.stringValue
        if (!opStringResource.isPresent) {
            throw IllegalArgumentException("Missing resource name")
        }
        val resource = opStringResource.get().uppercase()
        try {
            var quantity = 1
            if (args.size > 3) {
                quantity = args[3].toInt()
            }
            var price: Double = quantity * resource.price * buyCoef
            if (quantity <= 0) {
                messageCreateEvent.message.reply("Evitez ce genre de transactions ... petite pénalité")
                price *= -1
                quantity = -1
            }
            if (p["bal"].toDouble() >= price) {
                p["bal"] = (p["bal"].toDouble() - price).toString()
                var resourceManager = p.resourceManagers[resource]
                if (resourceManager == null) {
                    resourceManager = ResourceManager(resource, quantity)
                    p.resourceManagers[resource] = resourceManager
                } else {
                    resourceManager.quantity = resourceManager.quantity + quantity
                }
                p.updateResources()
                messageCreateEvent.message.reply("Transaction effectuée")
            } else {
                messageCreateEvent.message.reply("La transaction de " + price + " rc pour " + quantity + " " + resource.name_ + " n'a pas pu être effectuée car vous n'aviez pas assez de rc")
            }
        } catch (e: IllegalArgumentException) {
            messageCreateEvent.message.reply("Cette resource n'existe pas ou la quantité entrée est invalide")
        }
    }

}

class ShopSellCommand :
    SubCommand(
        "sell",
        "Vendre des items / resources pour un mauvais prix",
        arrayListOf(
            SlashCommandOption.createWithChoices(
                SlashCommandOptionType.STRING,
                "name",
                "Nom de l'item à vendre",
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
        try {
            val resource = Resource.valueOf(args[2].uppercase(Locale.getDefault()))
            var quantity = 1
            if (args.size > 3) {
                quantity = args[3].toInt()
            }
            var price: Double = quantity * resource.price * sellCoef
            if (quantity <= 0) {
                messageCreateEvent.message.reply("Evitez ce genre de transactions ... petite pénalité")
                price = -1.0
                quantity *= -1
            }
            var resourceManager = p.resourceManagers[resource]
            if (resourceManager == null) {
                resourceManager = ResourceManager(resource, 0)
                p.resourceManagers[resource] = resourceManager
                messageCreateEvent.message.reply("Transaction impossible car vous n'avez pas les ressources demandées")
                p.updateResources()
            } else {
                if (resourceManager.quantity >= quantity) {
                    p["bal"] = (p["bal"].toDouble() + price).toString()
                    resourceManager.quantity = resourceManager.quantity - quantity
                    p.updateResources()
                    messageCreateEvent.message.reply("Transaction effectuée")
                } else {
                    messageCreateEvent.message.reply("La transaction de " + quantity + " " + resource.name_ + " pour " + price + " rc n'a pas pu être effectuée car vous n'aviez pas assez de " + resource.name_)
                }
            }
        } catch (e: IllegalArgumentException) {
            messageCreateEvent.message.reply("Cette resource n'existe pas ou la quantité entrée est invalide")
        }
    }
}

class ShopListCommand :
    SubCommand(
        "list",
        "Lister les items / resources disponibles"
    ),
    ExecutableWithArguments {

    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        val stringBuilder = StringBuilder()
            .append("Resource -> prix d'achat; prix de vente; prix réel; nom à entrer")
        for (r in Resource.values()) {
            stringBuilder.append("\n").append(r.name_).append(" -> ").append(r.price * buyCoef).append("; ")
                .append(r.price * sellCoef).append("; ").append(r.price).append("; ").append(r.progName)
        }
        val embedBuilder = EmbedBuilder()
            .setTitle("Liste du shop")
            .setFooter("Ceci est temporaire en attendant que les marchés qui pourront être contruits dans les serveurs ou les villes")
            .addField("Resources", stringBuilder.toString())
        messageCreateEvent.message.reply(embedBuilder)
        if (p["tuto"].toInt() == 5) {
            messageCreateEvent.message.reply("Je vous laisse découvrir la suite tout seul ...")
            p["tuto"] = "6"
        }
    }
}

class ShopInfoCommand :
    SubCommand(
        "info",
        "Afficher les informations d'un item / resource",
        arrayListOf(
            SlashCommandOption.createWithChoices(
                SlashCommandOptionType.STRING,
                "name",
                "Nom de l'item / resource",
                true,
                run {
                    val arrayList = arrayListOf<SlashCommandOptionChoice>()
                    Resource.values().forEach {
                        arrayList.add(SlashCommandOptionChoice.create(it.name, it.progName))
                    }
                    return@run arrayList
                }
            )
        )
    ),
    ExecutableWithArguments {

    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        TODO("Not yet implemented")
    }
}