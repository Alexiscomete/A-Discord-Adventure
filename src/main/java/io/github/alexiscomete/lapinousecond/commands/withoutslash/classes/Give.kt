package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandWithAccount
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.entity.players
import io.github.alexiscomete.lapinousecond.resources.Resource
import io.github.alexiscomete.lapinousecond.useful.transactions.giveFromTo
import org.javacord.api.event.message.MessageCreateEvent

class Give : CommandWithAccount("description", "give", "totalDescription") {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>, p: Player) {
        // Fonctionne de la façon suivante : -give <type d'owner> <owner> <ressource> <quantité>
        // Exemple : -give PLAYER Jean WOOD 10
        // Exemple : -give CITY HeyCity STONE 10

        // On récupère le premier argument (le type d'owner)
        val type = args[0]

        // On récupère le second argument (le nom de l'owner)
        val owner = args[1]

        // On récupère le troisième argument (la ressource)
        val ressource = args[2]

        // On récupère le quatrième argument (la quantité)
        val quantity = args[3]

        // On vérifie que le type d'owner est bien un type d'owner valide
        if (type.uppercase() != "PLAYER") {
            throw IllegalArgumentException("Le type d'owner doit être valide (ex : PLAYER)")
        }

        // on convertit l'owner en Player
        val player = players[owner.toLong()] ?: throw IllegalArgumentException("Le joueur n'existe pas")

        // On vérifie que la ressource est bien une ressource valide
        val resource = Resource.valueOf(ressource.uppercase())

        // On vérifie que la quantité est bien un nombre
        val quantityDouble = quantity.toDouble()

        // On vérifie que la quantité est bien positive
        if (quantityDouble <= 0) {
            throw IllegalArgumentException("La quantité doit être positive")
        }

        // on fait la transaction sécurisée avec GiveFromTo
        giveFromTo(p, player, quantityDouble, resource)
    }
}