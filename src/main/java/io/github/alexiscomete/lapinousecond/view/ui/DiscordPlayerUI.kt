package io.github.alexiscomete.lapinousecond.view.ui

import io.github.alexiscomete.lapinousecond.entity.Player
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.Interaction
import org.javacord.api.interaction.InteractionBase
import org.javacord.api.interaction.MessageComponentInteractionBase
import java.awt.image.BufferedImage

class DiscordPlayerUI(private val player: Player, val interaction: Interaction) : PlayerUI {

    // --- methods ---

    fun update() {

    }

    fun update(messageComponentInteractionBase: MessageComponentInteractionBase) {

    }

    fun send(interactionBase: InteractionBase) {
        // note : max 10 embeds and 6000 characters
        val sum = 0
        val mainEmbed = EmbedBuilder()
        if (bufferedImage != null) {
            mainEmbed.setImage(bufferedImage)
        } else if (linkedImage != null) {
            mainEmbed.setImage(linkedImage)
        }
        val embeds = mutableListOf<EmbedBuilder>()
        embeds.add(mainEmbed)
        interactionBase.createImmediateResponder()
            .addEmbeds(embeds)
            .respond()
    }

    // --- Content ---
    // images
    private var linkedImage: String? = null
    private var bufferedImage: BufferedImage? = null


    override fun addMessage(message: String): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun addMessage(title: String, content: String): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun addDialogue(dialogue: Dialogue): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun addInteraction(id: String, interaction: InteractionUI): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun respondToInteraction(id: String): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun respondToInteractionWithArgument(id: String, argument: String): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun removeInteraction(id: String): PlayerUI {
        TODO("Not yet implemented")
    }

    override fun clear(): PlayerUI {
        bufferedImage = null
        linkedImage = null
        return this
    }

    override fun updateOrSend(): PlayerUI {
        if (interaction.asModalInteraction().isPresent) {
            send(interaction.asModalInteraction().get())
        } else if (interaction.asSlashCommandInteraction().isPresent) {
            send(interaction.asSlashCommandInteraction().get())
        } else if (interaction.asMessageComponentInteraction().isPresent) {
            update(interaction.asMessageComponentInteraction().get())
        } else {
            update()
        }
        return this
    }

    override fun setImage(image: BufferedImage): PlayerUI {
        bufferedImage = image
        return this
    }

    override fun setImage(link: String): PlayerUI {
        linkedImage = link
        return this
    }

    override fun hasInteraction(id: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasDialogue(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasMessage(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasBufferedImage(): Boolean {
        return bufferedImage != null
    }

    override fun hasLinkedImage(): Boolean {
        return linkedImage != null
    }

    override fun getInteraction(id: String): InteractionUI? {
        TODO("Not yet implemented")
    }

    override fun getBufferedImage(): BufferedImage {
        return bufferedImage!!
    }

    override fun getLinkedImage(): String {
        return linkedImage!!
    }

    override fun getInteractions(): Map<String, InteractionUI> {
        TODO("Not yet implemented")
    }

    override fun getMessages(): List<String> {
        TODO("Not yet implemented")
    }

    override fun getDialogues(): List<Dialogue> {
        TODO("Not yet implemented")
    }

    override fun getPlayer(): Player {
        return player
    }
}