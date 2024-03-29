package io.github.alexiscomete.lapinousecond.view.ui.playerui

import io.github.alexiscomete.lapinousecond.entity.entities.PlayerData
import io.github.alexiscomete.lapinousecond.entity.entities.PlayerManager
import io.github.alexiscomete.lapinousecond.view.Context
import io.github.alexiscomete.lapinousecond.view.contextFor
import io.github.alexiscomete.lapinousecond.view.discord.commands.getAccount
import io.github.alexiscomete.lapinousecond.view.ui.dialogue.Dialogue
import io.github.alexiscomete.lapinousecond.view.ui.dialogue.DialoguePart
import io.github.alexiscomete.lapinousecond.view.ui.interactionui.InteractionStyle
import io.github.alexiscomete.lapinousecond.view.ui.longuis.LongCustomUI
import org.javacord.api.entity.message.MessageFlag
import org.javacord.api.entity.message.component.*
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.interaction.Interaction
import org.javacord.api.interaction.InteractionBase
import org.javacord.api.interaction.MessageComponentInteractionBase
import java.awt.Color

class DiscordPlayerUI(private val context: Context, var interaction: Interaction) : PlayerUI {

    // --- methods ---

    private fun update(messageComponentInteractionBase: MessageComponentInteractionBase) {
        // note : max 10 embeds and 6000 characters
        if (context.messages.isNotEmpty()) {
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
        } else if (longCustomUI != null) {
            val mainEmbed = EmbedBuilder()
                .setColor(Color.ORANGE)
                .setTimestampToNow()
            if (longCustomUI!!.getTitle() != null) {
                mainEmbed.setTitle(longCustomUI!!.getTitle())
            }
            if (longCustomUI!!.getDescription() != null) {
                mainEmbed.setDescription(longCustomUI!!.getDescription())
            }
            if (longCustomUI!!.getUnderString() != null) {
                mainEmbed.setFooter(longCustomUI!!.getUnderString())
            }
            if (longCustomUI!!.getFields() != null) {
                for (field in longCustomUI!!.getFields()!!) {
                    mainEmbed.addField(field.first, field.second)
                }
            }
            val bImage = longCustomUI!!.getBufferedImage()
            if (bImage != null) {
                mainEmbed.setImage(bImage)
                val later = messageComponentInteractionBase
                    .respondLater(true)
                later.thenAccept {
                    it
                        .removeAllComponents()
                        .removeAllEmbeds()
                        .addEmbeds(mainEmbed)
                        .addComponents(*getComponents(longCustomUI!!))
                        .setFlags(MessageFlag.EPHEMERAL)
                        .update()
                }
            } else if (longCustomUI!!.getLinkedImage() != null) {
                mainEmbed.setImage(longCustomUI!!.getLinkedImage())
                val later = messageComponentInteractionBase
                    .respondLater(true)
                later.thenAccept {
                    it
                        .removeAllComponents()
                        .removeAllEmbeds()
                        .addEmbeds(mainEmbed)
                        .addComponents(*getComponents(longCustomUI!!))
                        .setFlags(MessageFlag.EPHEMERAL)
                        .update()
                }
            } else {
                messageComponentInteractionBase.createOriginalMessageUpdater()
                    .removeAllComponents()
                    .removeAllEmbeds()
                    .addEmbeds(mainEmbed)
                    .addComponents(*getComponents(longCustomUI!!))
                    .setFlags(MessageFlag.EPHEMERAL)
                    .update()
            }
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
        for (i in context.messages.size - 1 downTo context.messages.size - 11) {
            if (i < 0) break
            val message = context.messages[i]
            if (message.title != null) {
                if (message.title!!.length < 180 && message.content.length < 400) {
                    messageEmbed.addField(message.title!!, message.content)
                    context.messages.removeAt(i)
                } else if (canSendInMp) {
                    canSendInMp = false
                    interactionBase.user.sendMessage(message.title + "\n" + message.content)
                    context.messages.removeAt(i)
                }
            } else {
                if (message.content.length < 400) {
                    currentDescription += message.content + "\n"
                    messageEmbed.setDescription(currentDescription)
                    context.messages.removeAt(i)
                } else if (canSendInMp) {
                    canSendInMp = false
                    interactionBase.user.sendMessage(message.content)
                    context.messages.removeAt(i)
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
        messageEmbedBuilder.setTitle(author.name)
        if (author.hasImageAvatar()) {
            messageEmbedBuilder.setThumbnail(author.imageAvatar)
        } else if (author.hasLinkAvatar()) {
            messageEmbedBuilder.setThumbnail(author.linkAvatar)
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
        if (context.messages.isNotEmpty()) {
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
                .setColor(Color.ORANGE)
                .setTimestampToNow()
            if (longCustomUI!!.getTitle() != null) {
                mainEmbed.setTitle(longCustomUI!!.getTitle())
            }
            if (longCustomUI!!.getDescription() != null) {
                mainEmbed.setDescription(longCustomUI!!.getDescription())
            }
            if (longCustomUI!!.getUnderString() != null) {
                mainEmbed.setFooter(longCustomUI!!.getUnderString())
            }
            if (longCustomUI!!.getFields() != null) {
                for (field in longCustomUI!!.getFields()!!) {
                    mainEmbed.addField(field.first, field.second)
                }
            }
            val bImage = longCustomUI!!.getBufferedImage()
            if (bImage != null) {
                mainEmbed.setImage(bImage)
            } else if (longCustomUI!!.getLinkedImage() != null) {
                mainEmbed.setImage(longCustomUI!!.getLinkedImage())
            }
            embeds.add(mainEmbed)
            interactionBase.createImmediateResponder()
                .addEmbeds(embeds)
                .addComponents(*getComponents(longCustomUI!!))
                .setFlags(MessageFlag.EPHEMERAL)
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

    // messages are in Context

    // dialogues
    private val dialogues = mutableListOf<Dialogue>()
    private var dialoguePart: DialoguePart? = null
    private var dialogueTitle: String? = null

    // long customId
    private var longCustomUI: LongCustomUI? = null

    // question
    private var currentQuestion: Question? = null

    override fun addMessage(message: Message): PlayerUI {
        context.messages.add(message)
        return this
    }

    override fun addDialogue(dialogue: Dialogue): PlayerUI {
        dialogues.add(dialogue)
        return this
    }

    private fun sendQuestion(question: Question) {
        val fields = mutableListOf(
            ActionRow.of(
                TextInput.create(
                    (if (question.field0.shortAnswer) TextInputStyle.SHORT else TextInputStyle.PARAGRAPH),
                    "question_field0",
                    question.field0.title,
                    question.field0.required
                )
            )
        )
        if (question.field1 != null) {
            fields.add(
                ActionRow.of(
                    TextInput.create(
                        (if (question.field1.shortAnswer) TextInputStyle.SHORT else TextInputStyle.PARAGRAPH),
                        "question_field1",
                        question.field1.title,
                        question.field1.required
                    )
                )
            )
        }
        if (question.field2 != null) {
            fields.add(
                ActionRow.of(
                    TextInput.create(
                        (if (question.field2.shortAnswer) TextInputStyle.SHORT else TextInputStyle.PARAGRAPH),
                        "question_field2",
                        question.field2.title,
                        question.field2.required
                    )
                )
            )
        }
        if (question.field3 != null) {
            fields.add(
                ActionRow.of(
                    TextInput.create(
                        (if (question.field3.shortAnswer) TextInputStyle.SHORT else TextInputStyle.PARAGRAPH),
                        "question_field3",
                        question.field3.title,
                        question.field3.required
                    )
                )
            )
        }
        interaction.respondWithModal(
            "modal_question",
            question.name,
            fields as List<HighLevelComponent>?
        )
    }

    override fun respondToInteraction(id: String): PlayerUI {
        if (id.contains("just_update")) {
            updateOrSend()
            return this
        }
        if (interaction.asModalInteraction().isPresent) {
            if (currentQuestion == null) {
                updateOrSend()
            } else {
                val modal = interaction.asModalInteraction().get()
                val f1 = modal.getTextInputValueByCustomId("question_field0")
                val f2 = modal.getTextInputValueByCustomId("question_field1")
                val f3 = modal.getTextInputValueByCustomId("question_field2")
                val f4 = modal.getTextInputValueByCustomId("question_field3")
                if (f1.isPresent) {
                    currentQuestion!!.field0.answer = f1.get()
                }
                if (f2.isPresent && currentQuestion!!.field1 != null) {
                    currentQuestion!!.field1!!.answer = f2.get()
                }
                if (f3.isPresent && currentQuestion!!.field2 != null) {
                    currentQuestion!!.field2!!.answer = f3.get()
                }
                if (f4.isPresent && currentQuestion!!.field3 != null) {
                    currentQuestion!!.field3!!.answer = f4.get()
                }
                val question = currentQuestion!!.doAfter(currentQuestion!!)
                if (question == null) {
                    updateOrSend()
                    currentQuestion = null
                } else {
                    currentQuestion = question
                    interaction.asModalInteraction().get().createImmediateResponder()
                        .addEmbeds(
                            EmbedBuilder()
                                .setTitle("Votre réponse a été reçue")
                                .setDescription("Mais nous avons besoin de d'autres informations pour continuer")
                        )
                        .addComponents(
                            ActionRow.of(
                                Button.primary("send_question", "Actualiser")
                            )
                        )
                        .respond()
                }
            }
            return this
        }
        if (id.contains("send_question") && currentQuestion != null) {
            sendQuestion(currentQuestion!!)
            return this
        }
        if (longCustomUI!!.hasInteractionID(id)) {
            val question = longCustomUI!!.respondToInteraction(id)
            if (question == null) {
                updateOrSend()
            } else {
                currentQuestion = question
                sendQuestion(question)
            }
            return this
        }
        if (id.contains("end_dialogue")) {
            dialoguePart = null
            dialogueTitle = null
        } else if (id.contains("next_dialogue")) {
            dialoguePart = dialoguePart!!.next()
        } else if (id.contains("previous_dialogue")) {
            dialoguePart = dialoguePart!!.before()
        }
        updateOrSend()
        return this
    }

    override fun respondToInteraction(id: String, argument: String): PlayerUI {
        if (id.contains("just_update")) {
            updateOrSend()
            return this
        }
        if (longCustomUI?.hasInteractionID(id) == true) {
            longCustomUI!!.respondToInteraction(id, argument)
            updateOrSend()
            return this
        }
        if (id.contains("end_dialogue")) {
            dialoguePart = null
            dialogueTitle = null
        } else if (id.contains("next_dialogue")) {
            dialoguePart = dialoguePart!!.next()
        } else if (id.contains("previous_dialogue")) {
            dialoguePart = dialoguePart!!.before()
        }
        updateOrSend()
        return this
    }

    override fun canExecute(id: String): Boolean {
        return longCustomUI?.hasInteractionID(id) == true
                || id.contains("just_update")
                || id.contains("end_dialogue")
                || id.contains("next_dialogue")
                || id.contains("previous_dialogue")
                || id.contains("send_question")
                || id.contains("modal_question")
    }

    override fun clear(): PlayerUI {
        context.messages.clear()
        dialogues.clear()
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

    override fun hasDialogue(): Boolean {
        return dialogues.isNotEmpty()
    }

    override fun hasMessage(): Boolean {
        return context.messages.isNotEmpty()
    }

    override fun getMessages(): List<String> {
        val list = mutableListOf<String>()
        for (message in context.messages) {
            list.add(message.toString())
        }
        return list
    }

    override fun getDialogues(): List<Dialogue> {
        return dialogues
    }

    override fun getPlayer(): PlayerData {
        return context.players.player.player
    }

    override fun getPlayerManager(): PlayerManager {
        return context.players.player.playerManager
    }

    override fun getLongCustomUI(): LongCustomUI? {
        return longCustomUI
    }

    override fun setLongCustomUI(longCustomUI: LongCustomUI?): PlayerUI {
        this.longCustomUI = longCustomUI
        return this
    }
}