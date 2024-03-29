package io.github.alexiscomete.lapinousecond.view.discord.commands.classes

import io.github.alexiscomete.lapinousecond.data.TutoSteps
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.Resource
import io.github.alexiscomete.lapinousecond.entity.concrete.resources.ResourceManager
import io.github.alexiscomete.lapinousecond.view.discord.commands.Command
import io.github.alexiscomete.lapinousecond.view.discord.commands.ExecutableWithArguments
import io.github.alexiscomete.lapinousecond.view.discord.commands.SubCommand
import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import io.github.alexiscomete.lapinousecond.view.exceptions.InvalidUserActionException
import io.github.alexiscomete.lapinousecond.view.exceptions.UserCanSolveException
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.*
import java.awt.Color
import kotlin.math.round

private const val BUY_COEF = 1.1
private const val SELL_COEF = 0.9
const val MAX_SHOP_QUANTITY = Int.MAX_VALUE
const val MIN_SHOP_QUANTITY = 1
const val ROUND_PRECISION = 1000

// TODO : round prices

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
                    Resource.entries.forEach {
                        arrayList.add(SlashCommandOptionChoice.create(it.name, it.name))
                    }
                    return@run arrayList
                }
            ),
            SlashCommandOption.createLongOption(
                "quantity",
                "Quantité à acheter",
                false,
                MIN_SHOP_QUANTITY.toLong(),
                MAX_SHOP_QUANTITY.toLong()
            )
        )
    ),
    ExecutableWithArguments {
    override val fullName: String
        get() = "shop buy"
    override val botPerms: Array<String>
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        val arguments = slashCommand.arguments
        val resource = resource(arguments)
        var quantity = MIN_SHOP_QUANTITY
        val quantityArgument = arguments.first { it.name == "quantity" }

        val opQuantity = quantityArgument.longValue
        if (opQuantity.isPresent) {
            quantity = opQuantity.get().toInt()
            if (quantity < MIN_SHOP_QUANTITY) {
                throw IllegalArgumentException("Quantity must be greater than ${MIN_SHOP_QUANTITY - 1}")
            }
        }

        val player = getAccount(slashCommand)
        val playerData = player.playerData

        val price: Double = quantity * resource.price * BUY_COEF
        if (playerData["bal"].toDouble() >= price) {
            playerData["bal"] = (playerData["bal"].toDouble() - price).toString()
            var resourceManager = player.ownerManager.resourceManagers[resource]
            if (resourceManager == null) {
                resourceManager = ResourceManager(resource, quantity)
                player.ownerManager.resourceManagers[resource] = resourceManager
            } else {
                resourceManager.quantity += quantity
            }
            playerData.updateResources(player.ownerManager.resourceManagers)
            slashCommand.createImmediateResponder()
                .setContent("Achat réussi ! Vous avez payé ${price.toInt()} ${Resource.RABBIT_COIN.show}")
                .respond()
        } else {
            throw IllegalStateException("La transaction de " + price + " ${Resource.RABBIT_COIN.show} pour " + quantity + " " + resource.show + " n'a pas pu être effectuée car vous n'aviez pas assez de ${Resource.RABBIT_COIN.show}")
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
                    Resource.entries.forEach {
                        arrayList.add(SlashCommandOptionChoice.create(it.name, it.name))
                    }
                    return@run arrayList
                }
            ),
            SlashCommandOption.createLongOption(
                "quantity",
                "Quantité à vendre",
                false,
                MIN_SHOP_QUANTITY.toLong(),
                MAX_SHOP_QUANTITY.toLong()
            )
        )
    ),
    ExecutableWithArguments {
    override val fullName: String
        get() = "shop sell"
    override val botPerms: Array<String>
        get() = arrayOf("PLAY")

    override fun execute(slashCommand: SlashCommandInteraction) {
        val arguments = slashCommand.arguments
        val resource = resource(arguments)
        var quantity = MIN_SHOP_QUANTITY
        val quantityArgument = arguments.first { it.name == "quantity" }

        val opQuantity = quantityArgument.longValue
        if (opQuantity.isPresent) {
            quantity = opQuantity.get().toInt()
            if (quantity < MIN_SHOP_QUANTITY) {
                throw UserCanSolveException("Quantity must be greater than ${MIN_SHOP_QUANTITY - 1}")
            }
        }

        val player = getAccount(slashCommand)
        val playerData = player.playerData

        val price: Double = quantity * resource.price * SELL_COEF
        var resourceManager = player.ownerManager.resourceManagers[resource]
        if (resourceManager == null) {
            resourceManager = ResourceManager(resource, 0)
            player.ownerManager.resourceManagers[resource] = resourceManager
            playerData.updateResources(player.ownerManager.resourceManagers)
            throw InvalidUserActionException("Transaction impossible car vous n'avez pas les ressources demandées")
        } else {
            if (resourceManager.quantity >= quantity) {
                playerData["bal"] = (playerData["bal"].toDouble() + price).toString()
                resourceManager.quantity -= quantity
                playerData.updateResources(player.ownerManager.resourceManagers)
                slashCommand.createImmediateResponder()
                    .setContent("Vente réussie ! Vous avez gagné ${price.toInt()} ${Resource.RABBIT_COIN.show}")
                    .respond()
            } else {
                throw InvalidUserActionException("La transaction de " + quantity + " " + resource.show + " pour " + price + " ${Resource.RABBIT_COIN.show} n'a pas pu être effectuée car vous n'aviez pas assez de " + resource.show)
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
        val playerData = player.playerData
        val stringBuilder = StringBuilder()
            .append("Resource -> prix d'achat; prix de vente; prix réel; nom à entrer")
        for (r in Resource.entries) {
            stringBuilder
                .append("\n")
                .append(r.show)
                .append(" -> `")
                .append(round(r.price * BUY_COEF * ROUND_PRECISION) / ROUND_PRECISION)
                .append("`; `")
                .append(round(r.price * SELL_COEF * ROUND_PRECISION) / ROUND_PRECISION)
                .append("`; `")
                .append(r.price)
                .append("`; `")
                .append(r.name)
                .append("`")
        }
        val embedBuilder = EmbedBuilder()
            .setTitle("Liste du shop")
            .setFooter("Ceci est temporaire en attendant que les marchés qui pourront être construits dans les serveurs ou les villes")
            .addField("Resources", stringBuilder.toString())
        val responder = slashCommand.createImmediateResponder()
            .addEmbed(embedBuilder)
        if (playerData["tuto"] == TutoSteps.STEP_SHOP.number) {
            playerData["tuto"] = TutoSteps.STEP_SHOP.nextStepNum
            responder.setContent("> (Aurimezi) : Voici la liste des ressources disponibles dans le magasin ! Bon faut pas le dire mais ils prennent un pourcentage sur chaque vente donc il vaut mieux que tu passes par le marché ! Mais il faut vite avancer et une offre au marché peut rester plusieurs jours. Je te laisse faire tes achats avec `/shop sell` et `/shop buy`, moi je me prépare à partir à l'aventure ! Utilises `/map` quand tu est prêt. Sélectionnes `Cartes` puis `Ma position`.")
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
                    Resource.entries.forEach {
                        arrayList.add(SlashCommandOptionChoice.create(it.name, it.name))
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
        val resourceManager = player.ownerManager.resourceManagers[resource]
        // on calcul combien le joueur a de cette resource
        val quantity = resourceManager?.quantity ?: 0
        val embedBuilder = EmbedBuilder()
            .setFooter("Quantité sur vous : $quantity")
            .addField("Prix d'achat", (resource.price * BUY_COEF).toString() + Resource.RABBIT_COIN.show, true)
            .addField("Prix de vente", (resource.price * SELL_COEF).toString() + Resource.RABBIT_COIN.show, true)
            .addField("Prix réel", resource.price.toString() + Resource.RABBIT_COIN.show, true)
            .setColor(Color.GREEN)
            .setTitle(resource.show + " " + resource.name)
        val responder = slashCommand.createImmediateResponder()
            .addEmbed(embedBuilder)
        responder.respond()
    }
}
