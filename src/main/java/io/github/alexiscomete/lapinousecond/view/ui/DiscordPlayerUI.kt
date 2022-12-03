package io.github.alexiscomete.lapinousecond.view.ui

import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.contextFor
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.Interaction
import org.javacord.api.interaction.InteractionBase
import org.javacord.api.interaction.MessageComponentInteractionBase
import java.awt.image.BufferedImage

class DiscordPlayerUI(private val player: Player, var interaction: Interaction) : PlayerUI {

    // --- methods ---

    fun update() {

    }

    fun update(messageComponentInteractionBase: MessageComponentInteractionBase) {

    }

    fun showDialogue(interactionBase: InteractionBase) {
        val messageEmbedBuilder = EmbedBuilder()
            .setAuthor(if (dialogueTitle != null) dialogueTitle else "Dialogue")
            .setDescription(dialoguePart!!.getContent())
        val author = dialoguePart!!.getAuthor()
        messageEmbedBuilder.setTitle(author.getName())
        if (author.hasImageAvatar()) {
            messageEmbedBuilder.setThumbnail(author.getImageAvatar())
        } else if (author.hasLinkAvatar()) {
            messageEmbedBuilder.setThumbnail(author.getLinkAvatar())
        }
        messageEmbedBuilder.setTimestampToNow()
        val lowLevelComponents = mutableListOf<Button>()
        if (!dialoguePart!!.isLast()) {
            lowLevelComponents.add(Button.primary("next_dialogue", "Suite..."))
        }
        if (!dialoguePart!!.isFirst()) {
            lowLevelComponents.add(Button.primary("previous_dialogue", "Retour..."))
        }
        lowLevelComponents.add(Button.primary("end_dialogue", "Passer le dialogue"))
        val actionRow = ActionRow.of(*lowLevelComponents.toTypedArray())
        interactionBase.createImmediateResponder()
            .addEmbeds(messageEmbedBuilder)
            .addComponents(actionRow)
            .respond()
        val context = contextFor(getAccount(interactionBase.user))
        context.ui(this)
    }

    fun send(interactionBase: InteractionBase) {
        // note : max 10 embeds and 6000 characters
        val embeds = mutableListOf<EmbedBuilder>()
        if (messages.isNotEmpty()) {
            val messageEmbed = EmbedBuilder()
                .setTitle("Vous avez reçu des messages")
            var canSendInMp = true
            var currentDescription = ""
            // pour les 11 derniers messages
            for (i in messages.size - 1 downTo messages.size - 11) {
                if (i < 0) break
                val message = messages[i]
                if (message.title != null) {
                    if (message.title!!.length < 180 && message.content.length < 400) {
                        messageEmbed.addField(message.title!!, message.content)
                        messages.removeAt(i)
                    } else if (canSendInMp) {
                        canSendInMp = false
                        interactionBase.user.sendMessage(message.title + "\n" + message.content)
                        messages.removeAt(i)
                    }
                } else {
                    if (message.content.length < 400) {
                        currentDescription += message.content + "\n"
                        messageEmbed.setDescription(currentDescription)
                        messages.removeAt(i)
                    } else if (canSendInMp) {
                        canSendInMp = false
                        interactionBase.user.sendMessage(message.content)
                        messages.removeAt(i)
                    }
                }
            }
            interactionBase.createImmediateResponder()
                .addEmbeds(messageEmbed)
                .addComponents(
                    ActionRow.of(
                        Button.primary("just_update", "Messages suivants / actualiser")
                    )
                )
                .respond()
            val context = contextFor(getAccount(interactionBase.user))
            context.ui(this)
            return
        } else if (dialoguePart != null) {
            showDialogue(interactionBase)
            return
        } else if (dialogues.isNotEmpty()) {
            val dialogue = dialogues.first()
            dialoguePart = dialogue.getFirst()
            dialogueTitle = dialogue.getTitle()
            dialogues.removeAt(0)
            if (dialoguePart != null) {
                showDialogue(interactionBase)
                return
            } else {
                interactionBase.createImmediateResponder()
                    .setContent("Le dialogue est vide")
                    .addComponents(
                        ActionRow.of(
                            Button.primary("just_update", "Actualiser")
                        )
                    )
                    .respond()
                return
            }
        } else {
            val mainEmbed = EmbedBuilder()
            if (bufferedImage != null) {
                mainEmbed.setImage(bufferedImage)
            } else if (linkedImage != null) {
                mainEmbed.setImage(linkedImage)
            }
            embeds.add(mainEmbed)
        }
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
    private var dialoguePart: DialoguePart? = null
    private var dialogueTitle: String? = null

    // interactions
    private val mainManager = mutableMapOf<String, InteractionUI>()
    private val dialogueManager = mutableMapOf<String, InteractionUI>()
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
        mainManager[id] = interaction
        return this
    }

    override fun respondToInteraction(id: String): PlayerUI {
        for (interaction in interactions) {
            if (interaction.containsKey(id)) {
                interaction[id]?.execute(this)
                return this
            }
        }
        if (id == "just_update") {
            updateOrSend()
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
        for (interaction in interactions) {
            if (interaction.containsKey(id)) {
                return true
            }
        }
        return false
    }

    override fun hasDialogue(): Boolean {
        return dialogues.isNotEmpty()
    }

    override fun hasMessage(): Boolean {
        return messages.isNotEmpty()
    }

    override fun hasBufferedImage(): Boolean {
        return bufferedImage != null
    }

    override fun hasLinkedImage(): Boolean {
        return linkedImage != null
    }

    override fun getInteraction(id: String): InteractionUI? {
        for (interaction in interactions) {
            if (interaction.containsKey(id)) {
                return interaction[id]
            }
        }
        return null
    }

    override fun getBufferedImage(): BufferedImage {
        return bufferedImage!!
    }

    override fun getLinkedImage(): String {
        return linkedImage!!
    }

    override fun getInteractions(): Map<String, InteractionUI> {
        val map = mutableMapOf<String, InteractionUI>()
        for (interaction in interactions) {
            for (key in interaction.keys) {
                map[key] = interaction[key]!!
            }
        }
        return map
    }

    override fun getMessages(): List<String> {
        val list = mutableListOf<String>()
        for (message in messages) {
            list.add(message.toString())
        }
        return list
    }

    override fun getDialogues(): List<Dialogue> {
        return dialogues
    }

    override fun getPlayer(): Player {
        return player
    }
}