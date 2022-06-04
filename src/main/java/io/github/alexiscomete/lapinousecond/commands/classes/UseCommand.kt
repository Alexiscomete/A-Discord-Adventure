package io.github.alexiscomete.lapinousecond.commands.classes

import io.github.alexiscomete.lapinousecond.commands.CommandBot
import org.javacord.api.event.message.MessageCreateEvent
import io.github.alexiscomete.lapinousecond.*


class UseCommand : CommandBot(
    "Permet d'utiliser un objet",
    "use",
    "Utilisez use [name] pour utilisez l'objet nommé name, vous pouvez voir vos objets avec inv"
) {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {
        val p = saveManager?.players?.get(messageCreateEvent.messageAuthor.id)
        if (p == null) {
            messageCreateEvent.message.reply("Vous devez avoir un compte pour continuer")
            return
        }
        if (args.size < 2) {
            messageCreateEvent.message.reply("Vous devez avant tout indiquer le nom de l'item à utliser")
            return
        }
        for (item in p.items) {
            if (item.name.equals(name, ignoreCase = true)) {
                if (item.use(messageCreateEvent, content, args, p)) {
                    p.items.remove(item)
                    messageCreateEvent.message.reply("L'objet a été consommé !")
                }
                return
            }
        }
    }
}