package commands.classes

import commands.CommandBot
import org.javacord.api.event.message.MessageCreateEvent

class PlayerShop : CommandBot(
    "Marché des joueurs",
    "playershop",
    "Vous pouvez vendre ici des items pour le prix que vous voulez (playershop sell). C'est ensuite aux autres joueurs de l'acheter (playershop buy et playershop list)"
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {}
}