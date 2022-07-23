package io.github.alexiscomete.lapinousecond.commands.withslash.classes

import io.github.alexiscomete.lapinousecond.commands.withslash.Command
import io.github.alexiscomete.lapinousecond.commands.withslash.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.commands.withslash.SubCommand
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.resources.ResourceManager
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.*

private const val buyCoef = 1.1
private const val sellCoef = 0.9

private fun resource(arguments: MutableList<SlashCommandInteractionOption>): Resource {
    val resourceArgument = arguments.first { it.name == "name" }
    val opStringResource = resourceArgument.stringValue
    if (!opStringResource.isPresent) {
        throw IllegalArgumentException("Missing resource name")
    }
    val resourceStr = opStringResource.get().uppercase()
    val resource = try {
        Resource.valueOf(resourceStr)
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Unknown resource name")
    }
    return resource
}

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
                        arrayList.add(SlashCommandOptionChoice.create(it.progName, it.name))
                    }
                    return@run arrayList
                }
            ),
            SlashCommandOption.createLongOption("quantity", "Quantité à acheter", false, 1, 8007199254740991)
        )
    ),
    ExecutableWithArguments {
    override val fullName: String
        get() = "shop buy"
    override val botPerms: Array<String>?
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        val arguments = slashCommand.arguments
        val resource = resource(arguments)
        var quantity = 1
        val quantityArgument = arguments.first { it.name == "quantity" }

        val opQuantity = quantityArgument.longValue
        if (opQuantity.isPresent) {
            quantity = opQuantity.get().toInt()
            if (quantity < 1) {
                throw IllegalArgumentException("Quantity must be greater than 0")
            }
        }

        val player = getAccount(slashCommand)

        val price: Double = quantity * resource.price * buyCoef
        if (player["bal"].toDouble() >= price) {
            player["bal"] = (player["bal"].toDouble() - price).toString()
            var resourceManager = player.resourceManagers[resource]
            if (resourceManager == null) {
                resourceManager = ResourceManager(resource, quantity)
                player.resourceManagers[resource] = resourceManager
            } else {
                resourceManager.quantity = resourceManager.quantity + quantity
            }
            player.updateResources()
            slashCommand.createImmediateResponder()
                .setContent("Achat réussi ! Vous avez payé ${price.toInt()} ${Resource.RABBIT_COIN.name_}")
                .respond()
        } else {
            throw IllegalStateException("La transaction de " + price + " ${Resource.RABBIT_COIN.name_} pour " + quantity + " " + resource.name_ + " n'a pas pu être effectuée car vous n'aviez pas assez de ${Resource.RABBIT_COIN.name_}")
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
                        arrayList.add(SlashCommandOptionChoice.create(it.progName, it.name))
                    }
                    return@run arrayList
                }
            ),
            SlashCommandOption.createLongOption("quantity", "Quantité à vendre", false, 1, 8007199254740991)
        )
    ),
    ExecutableWithArguments {
    override val fullName: String
        get() = "shop sell"
    override val botPerms: Array<String>?
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        val arguments = slashCommand.arguments
        val resource = resource(arguments)
        var quantity = 1
        val quantityArgument = arguments.first { it.name == "quantity" }

        val opQuantity = quantityArgument.longValue
        if (opQuantity.isPresent) {
            quantity = opQuantity.get().toInt()
            if (quantity < 1) {
                throw IllegalArgumentException("Quantity must be greater than 0")
            }
        }

        val player = getAccount(slashCommand)

        val price: Double = quantity * resource.price * sellCoef
        var resourceManager = player.resourceManagers[resource]
        if (resourceManager == null) {
            resourceManager = ResourceManager(resource, 0)
            player.resourceManagers[resource] = resourceManager
            player.updateResources()
            throw IllegalStateException("Transaction impossible car vous n'avez pas les ressources demandées")
        } else {
            if (resourceManager.quantity >= quantity) {
                player["bal"] = (player["bal"].toDouble() + price).toString()
                resourceManager.quantity = resourceManager.quantity - quantity
                player.updateResources()
                slashCommand.createImmediateResponder()
                    .setContent("Vente réussie ! Vous avez gagné ${price.toInt()} ${Resource.RABBIT_COIN.name_}")
                    .respond()
            } else {
                throw IllegalStateException("La transaction de " + quantity + " " + resource.name_ + " pour " + price + " ${Resource.RABBIT_COIN.name_} n'a pas pu être effectuée car vous n'aviez pas assez de " + resource.name_)
            }
        }
    }
}

class ShopListCommand :
    SubCommand(
        "list",
        "Lister les items / resources disponibles"
    ),
    ExecutableWithArguments {
    override val fullName: String
        get() = "shop list"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        val player = getAccount(slashCommand)
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
        val responder = slashCommand.createImmediateResponder()
            .addEmbed(embedBuilder)
        if (player["tuto"].toInt() == 5) {
            player["tuto"] = "6"
            responder.setContent("Je vous laisse découvrir la suite tout seul ...")
        }
        responder.respond()
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
                        arrayList.add(SlashCommandOptionChoice.create(it.progName, it.name))
                    }
                    return@run arrayList
                }
            )
        )
    ),
    ExecutableWithArguments {
    override val fullName: String
        get() = "shop info"
    override val botPerms: Array<String>?
        get() = null

    override fun execute(slashCommand: SlashCommandInteraction) {
        val arguments = slashCommand.arguments
        val resource = resource(arguments)
        val player = getAccount(slashCommand)
        val resourceManager = player.resourceManagers[resource]
        // on calcul combien le joueur a de cette resource
        val quantity = resourceManager?.quantity ?: 0
        val embedBuilder = EmbedBuilder()
            .setTitle("Informations sur " + resource.name_)
            .addField("Quantité sur vous", quantity.toString(), true)
            .addField("Prix d'achat", (resource.price * buyCoef).toString() + Resource.RABBIT_COIN.name_, true)
            .addField("Prix de vente", (resource.price * sellCoef).toString() + Resource.RABBIT_COIN.name_, true)
            .addField("Prix réel", resource.price.toString() + Resource.RABBIT_COIN.name_, true)
        val responder = slashCommand.createImmediateResponder()
            .addEmbed(embedBuilder)
        responder.respond()
    }
}