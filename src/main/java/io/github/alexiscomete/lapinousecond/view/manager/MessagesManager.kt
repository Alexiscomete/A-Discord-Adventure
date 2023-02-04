package io.github.alexiscomete.lapinousecond.view.manager

import org.javacord.api.entity.channel.TextChannel
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener
import java.util.function.Consumer

class MessagesManager : MessageCreateListener {
    private val consumers = HashMap<TextChannel, HashMap<Long, Consumer<MessageCreateEvent>>>()
    override fun onMessageCreate(messageCreateEvent: MessageCreateEvent) {
        if (consumers.containsKey(messageCreateEvent.channel)) {
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

}