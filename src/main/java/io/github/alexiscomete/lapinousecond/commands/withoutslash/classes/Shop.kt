package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandInServer
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.resources.ResourceManager
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.message.MessageCreateEvent
import java.util.*

class Shop : CommandInServer(
    "Acheter / vendre un item",
    "shop",
    "Vous permet d'acheter ou de vendre des items / ressources classiques :\n- 'list' pour voir la liste des items\n- 'buy [name] <quantité>' pour acheter et 'sell [name] <quantité>' pour vendre\n- 'info [name]' pour avoir les informations sur un item\n"
) {
    private val buyCoef = 1.1
    private val sellCoef = 0.9
    override fun executeC(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {
        if (args.size < 2 || args[1] == "list") {
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
        } else if (args.size > 2) {
            if (args[1] == "buy") {
                try {
                    val resource = Resource.valueOf(args[2].uppercase(Locale.getDefault()))
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
            } else if (args[1] == "sell") {
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
            } else {
                messageCreateEvent.message.reply("Pas encore disponible pour le moment")
            }
        } else {
            messageCreateEvent.message.reply("Pas encore disponible pour le moment")
        }
    }
}