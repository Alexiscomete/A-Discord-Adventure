package io.github.alexiscomete.lapinousecond.view.ui

import io.github.alexiscomete.lapinousecond.commands.withslash.getAccount
import io.github.alexiscomete.lapinousecond.entity.Player
import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.ui.dialogue.Dialogue
import io.github.alexiscomete.lapinousecond.view.ui.dialogue.DialoguePart
import org.javacord.api.entity.message.component.ActionRow
import org.javacord.api.entity.message.component.Button
import org.javacord.api.entity.message.component.HighLevelComponent
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.Interaction
import org.javacord.api.interaction.InteractionBase
import org.javacord.api.interaction.MessageComponentInteractionBase

class DiscordPlayerUI(private val player: Player, var interaction: Interaction) : PlayerUI {

    // --- methods ---

    private fun update(messageComponentInteractionBase: MessageComponentInteractionBase) {
        // note : max 10 embeds and 6000 characters
        val embeds = mutableListOf<EmbedBuilder>()
        if (messages.isNotEmpty()) {
            val messageEmbed = messages(messageComponentInteractionBase)
            messageComponentInteractionBase.createOriginalMessageUpdater()
                .removeAllEmbeds()
                .removeAllComponents()
                .addEmbeds(messageEmbed)
                .addComponents(
                    ActionRow.of(
                        Button.primary("just_update", "Messages suivants / actualiser")
                    )
                )
                .update()
            val context = contextFor(getAccount(messageComponentInteractionBase.user))
            context.ui(this)
            return
        } else if (dialoguePart != null) {
            showDialogueUpdate(messageComponentInteractionBase)
            return
        } else if (dialogues.isNotEmpty()) {
            val dialogue = dialogues.first()
            dialoguePart = dialogue.getFirst()
            dialogueTitle = dialogue.getTitle()
            dialogues.removeAt(0)
            if (dialoguePart != null) {
                showDialogue(messageComponentInteractionBase)
                return
            } else {
                messageComponentInteractionBase.createOriginalMessageUpdater()
                    .setContent("Le dialogue est vide")
                    .addComponents(
                        ActionRow.of(
                            Button.primary("just_update", "Actualiser")
                        )
                    )
                    .update()
                return
            }
        }else if (longCustomUI != null) {
            val mainEmbed = EmbedBuilder()
            if (longCustomUI!!.getTitle() != null) {
                mainEmbed.setTitle(longCustomUI!!.getTitle())
            }
            if (longCustomUI!!.getDescription() != null) {
                mainEmbed.setDescription(longCustomUI!!.getDescription())
            }
            if (longCustomUI!!.getUnderString() != null) {
                mainEmbed.setFooter(longCustomUI!!.getUnderString())
            }
            if (longCustomUI!!.getBufferedImage() != null) {
                mainEmbed.setImage(longCustomUI!!.getBufferedImage())
            } else if (longCustomUI!!.getLinkedImage() != null) {
                mainEmbed.setImage(longCustomUI!!.getLinkedImage())
            }
            embeds.add(mainEmbed)
            messageComponentInteractionBase.createOriginalMessageUpdater()
                .addEmbeds(embeds)
                .update()
            return
        }
        val mainEmbed = EmbedBuilder()
            .setTitle("Rien à afficher")
            .setDescription("Vous n'avez rien à afficher")
        messageComponentInteractionBase.createOriginalMessageUpdater()
            .addEmbeds(mainEmbed)
            .update()
    }

    private fun messages(interactionBase: InteractionBase): EmbedBuilder? {
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
        return messageEmbed
    }

    private fun showDialogue(interactionBase: InteractionBase) {
        val (messageEmbedBuilder, actionRow) = dialogue()
        interactionBase.createImmediateResponder()
            .addEmbeds(messageEmbedBuilder)
            .addComponents(actionRow)
            .respond()
        val context = contextFor(getAccount(interactionBase.user))
        context.ui(this)
    }

    private fun showDialogueUpdate(messageComponentInteractionBase: MessageComponentInteractionBase) {
        val (messageEmbedBuilder, actionRow) = dialogue()
        messageComponentInteractionBase.createOriginalMessageUpdater()
            .addEmbeds(messageEmbedBuilder)
            .addComponents(actionRow)
            .update()
        val context = contextFor(getAccount(messageComponentInteractionBase.user))
        context.ui(this)
    }

    private fun dialogue(): Pair<EmbedBuilder, ActionRow> {
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
        return Pair(messageEmbedBuilder, actionRow)
    }

    private fun send(interactionBase: InteractionBase) {
        // note : max 10 embeds and 6000 characters
        val embeds = mutableListOf<EmbedBuilder>()
        if (messages.isNotEmpty()) {
            val messageEmbed = messages(interactionBase)
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
        } else if (longCustomUI != null) {
            val mainEmbed = EmbedBuilder()
            if (longCustomUI!!.getTitle() != null) {
                mainEmbed.setTitle(longCustomUI!!.getTitle())
            }
            if (longCustomUI!!.getDescription() != null) {
                mainEmbed.setDescription(longCustomUI!!.getDescription())
            }
            if (longCustomUI!!.getUnderString() != null) {
                mainEmbed.setFooter(longCustomUI!!.getUnderString())
            }
            if (longCustomUI!!.getBufferedImage() != null) {
                mainEmbed.setImage(longCustomUI!!.getBufferedImage())
            } else if (longCustomUI!!.getLinkedImage() != null) {
                mainEmbed.setImage(longCustomUI!!.getLinkedImage())
            }
            embeds.add(mainEmbed)
            interactionBase.createImmediateResponder()
                .addEmbeds(embeds)
                .addComponents(*getComponents(longCustomUI!!))
                .respond()
            return
        }
        val mainEmbed = EmbedBuilder()
            .setTitle("Rien à afficher")
            .setDescription("Vous n'avez rien à afficher")
        interactionBase.createImmediateResponder()
            .addEmbeds(mainEmbed)
            .respond()
    }

    private fun getComponents(longCustomUI: LongCustomUI): Array<HighLevelComponent> {
        val components = mutableListOf<HighLevelComponent>()
        for (element in longCustomUI.getInteractionUICustomUILists()) {
            val lowLevelComponents = mutableListOf<Button>()
            for (interactionUICustomUI in element) {
                lowLevelComponents.add(
                    when (interactionUICustomUI.getCustomInteractionStyle()) {
                        InteractionStyle.NORMAL -> Button.primary(
                            interactionUICustomUI.getId(),
                            interactionUICustomUI.getTitle()
                        )
                        InteractionStyle.DANGER -> Button.danger(
                            interactionUICustomUI.getId(),
                            interactionUICustomUI.getTitle()
                        )
                        InteractionStyle.SUCCESS -> Button.success(
                            interactionUICustomUI.getId(),
                            interactionUICustomUI.getTitle()
                        )
                        InteractionStyle.SECONDARY -> Button.secondary(
                            interactionUICustomUI.getId(),
                            interactionUICustomUI.getTitle()
                        )
                        InteractionStyle.NORMAL_DISABLED -> Button.primary(
                            interactionUICustomUI.getId(),
                            interactionUICustomUI.getTitle(),
                            true
                        )
                        InteractionStyle.DANGER_DISABLED -> Button.danger(
                            interactionUICustomUI.getId(),
                            interactionUICustomUI.getTitle(),
                            true
                        )
                        InteractionStyle.SUCCESS_DISABLED -> Button.success(
                            interactionUICustomUI.getId(),
                            interactionUICustomUI.getTitle(),
                            true
                        )
                        InteractionStyle.SECONDARY_DISABLED -> Button.secondary(
                            interactionUICustomUI.getId(),
                            interactionUICustomUI.getTitle(),
                            true
                        )
                    }
                )
            }
            components.add(ActionRow.of(*lowLevelComponents.toTypedArray()))
        }
        return components.toTypedArray()
    }


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

    // long customId
    private var longCustomUI: LongCustomUI? = null

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
        if (id.contains("end_dialogue")) {
            dialoguePart = null
            dialogueTitle = null
        }
        if (id.contains("next_dialogue")) {
            dialoguePart = dialoguePart!!.next()
        }
        if (id.contains("previous_dialogue")) {
            dialoguePart = dialoguePart!!.before()
        }
        if (id.contains("just_update")) {
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
        if (id.contains("just_update")) {
            updateOrSend()
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
        }
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

    override fun getInteraction(id: String): InteractionUI? {
        for (interaction in interactions) {
            if (interaction.containsKey(id)) {
                return interaction[id]
            }
        }
        return null
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

    override fun getLongCustomUI(): LongCustomUI? {
        return longCustomUI
    }

    override fun setLongCustomUI(longCustomUI: LongCustomUI?): PlayerUI {
        this.longCustomUI = longCustomUI
        return this
    }
}