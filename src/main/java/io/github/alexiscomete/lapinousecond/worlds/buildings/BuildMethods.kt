package io.github.alexiscomete.lapinousecond.worlds.buildings

import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.message_event.MenuBuilder
import org.javacord.api.entity.message.embed.EmbedBuilder

interface BuildMethods {
    fun getCompleteInfos(p: Player): MenuBuilder
    fun configBuilding()
    fun interpret(args: Array<String>)
    val help: String?
    val usage: String?
    fun descriptionShort(): String
    fun title(): String
}