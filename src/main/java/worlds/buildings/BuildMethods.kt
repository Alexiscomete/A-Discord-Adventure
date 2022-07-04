package worlds.buildings

import entity.Player
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.embed.EmbedBuilder

interface BuildMethods {
    fun getInfos(p: Player?): EmbedBuilder?
    fun getCompleteInfos(p: Player?): MessageBuilder?
    fun configBuilding()
    fun interpret(args: Array<String>)
    val help: String?
    val usage: String?
}