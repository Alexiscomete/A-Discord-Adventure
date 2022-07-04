package worlds.buildings.interactions

import entity.Player
import worlds.buildings.Building
import worlds.buildings.BuildingInteraction
import org.javacord.api.entity.message.MessageBuilder
import org.javacord.api.entity.message.embed.EmbedBuilder

class Hopital(building: Building?) : BuildingInteraction(building!!) {
    override fun interpret(args: Array<String>) {}
    override val help: String?
        get() = null
    override val usage: String?
        get() = null

    override fun getInfos(p: Player?): EmbedBuilder? {
        return null
    }

    override fun getCompleteInfos(p: Player?): MessageBuilder? {
        return null
    }

    override fun configBuilding() {}
}