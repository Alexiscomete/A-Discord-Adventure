package io.github.alexiscomete.lapinousecond.commands.withoutslash.classes

import io.github.alexiscomete.lapinousecond.config
import io.github.alexiscomete.lapinousecond.commands.withoutslash.CommandBot
import org.javacord.api.event.message.MessageCreateEvent
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import io.github.alexiscomete.lapinousecond.entity.players

class Verify :
    CommandBot("Permet de vérifier votre compte", "verify", "Permet de vérifier votre compte grâce au bot de l'ORU") {
    override fun execute(messageCreateEvent: MessageCreateEvent, content: String, args: Array<String>) {

    }


}