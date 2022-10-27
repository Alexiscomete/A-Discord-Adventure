package io.github.alexiscomete.lapinousecond.view.message_event

import io.github.alexiscomete.lapinousecond.messagesManager
import io.github.alexiscomete.lapinousecond.worlds.ServerBot
import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import java.util.function.Consumer

class MessagesManager : MessageCreateListener {
    private val consumers = HashMap<TextChannel, HashMap<Long, Consumer<MessageCreateEvent>>>()
    override fun onMessageCreate(messageCreateEvent: MessageCreateEvent) {
        if (consumers.containsKey(messageCreateEvent.channel) && !messageCreateEvent.message.content.startsWith("-")) {
            val hashMap = consumers[messageCreateEvent.channel]!!
            if (messageCreateEvent.messageAuthor.isUser && hashMap.containsKey(messageCreateEvent.messageAuthor.id)) {
                val messageCreateEventConsumer = hashMap[messageCreateEvent.messageAuthor.id]!!
                hashMap.remove(messageCreateEvent.messageAuthor.id)
                try {
                    messageCreateEventConsumer.accept(messageCreateEvent)
                } catch (e: Exception) {
                    messageCreateEvent.message.reply("Une erreur est survenue : " + e.message)
                }
            }
        }
    }

    fun addListener(textChannel: TextChannel, id: Long, messageCreateEventConsumer: Consumer<MessageCreateEvent>) {
        if (consumers.containsKey(textChannel)) {
            consumers[textChannel]!![id] = messageCreateEventConsumer
        } else {
            val hashMap = HashMap<Long, Consumer<MessageCreateEvent>>()
            hashMap[id] = messageCreateEventConsumer
            consumers[textChannel] = hashMap
        }
    }

    fun setValueAndRetry(
        textChannel: TextChannel,
        id: Long,
        prog_name: String,
        message: String,
        len: Int,
        serverBot: ServerBot,
        ex: Runnable
    ) {
        messagesManager.addListener(textChannel, id, object : Consumer<MessageCreateEvent> {
            override fun accept(msgE: MessageCreateEvent) {
                if (msgE.messageContent.length <= len) {
                    serverBot[prog_name] = msgE.messageContent
                    msgE.message.reply(message)
                    ex.run()
                } else {
                    textChannel.sendMessage("Taille maximale : " + len + ". Votre taille : " + msgE.messageContent.length)
                    messagesManager.addListener(textChannel, id, this)
                }
            }
        })
    }

    fun setValueAndRetry(
        textChannel: TextChannel,
        id: Long,
        prog_name: String,
        embedBuilder: EmbedBuilder,
        len: Int,
        serverBot: ServerBot,
        ex: Runnable
    ) {
        messagesManager.addListener(textChannel, id, object : Consumer<MessageCreateEvent> {
            override fun accept(msgE: MessageCreateEvent) {
                if (msgE.messageContent.length <= len) {
                    serverBot[prog_name] = msgE.messageContent
                    msgE.message.reply(embedBuilder)
                    ex.run()
                } else {
                    textChannel.sendMessage("Taille maximale : " + len + ". Votre taille : " + msgE.messageContent.length)
                    messagesManager.addListener(textChannel, id, this)
                }
            }
        })
    }
}