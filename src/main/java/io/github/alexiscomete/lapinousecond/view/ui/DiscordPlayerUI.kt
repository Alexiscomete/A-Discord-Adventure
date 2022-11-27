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

    // messages
    private val messages = mutableListOf<Message>()

    // dialogues
    private val dialogues = mutableListOf<Dialogue>()

    // interactions
    private val mainManager = mutableMapOf<String, InteractionManager>()
    private val dialogueManager = mutableMapOf<String, InteractionManager>()
    private val interactions = mutableListOf(mainManager)


    override fun addMessage(message: Message): PlayerUI {
        messages.add(message)
        return this
    }

    override fun addDialogue(dialogue: Dialogue): PlayerUI {
        dialogues.add(dialogue)
        interactions.add(dialogueManager)
        return this
    }

    override fun addInteraction(id: String, interaction: InteractionUI): PlayerUI {
        mainManager[id] = InteractionManager(id, interaction.getTitle(), interaction.getDescription(), { ui ->
            ui.respondToInteraction(id)
        }) { ui, argument ->
            ui.respondToInteractionWithArgument(id, argument)
        }
        return this
    }

    override fun respondToInteraction(id: String): PlayerUI {
        for (interaction in interactions) {
            if (interaction.containsKey(id)) {
                interaction[id]?.execute(this)
                return this
            }
        }
        return this
    }

    override fun respondToInteractionWithArgument(id: String, argument: String): PlayerUI {
        for (interaction in interactions) {
            if (interaction.containsKey(id)) {
                interaction[id]?.executeWithArgument(this, argument)
                return this
            }
        }
        return this
    }

    override fun removeInteraction(id: String): PlayerUI {
        for (interaction in interactions) {
            if (interaction.containsKey(id)) {
                interaction.remove(id)
                return this
            }
        }
        return this
    }

    override fun clear(): PlayerUI {
        bufferedImage = null
        linkedImage = null
        messages.clear()
        dialogues.clear()
        mainManager.clear()
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