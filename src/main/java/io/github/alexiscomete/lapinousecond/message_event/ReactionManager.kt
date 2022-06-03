package io.github.alexiscomete.lapinousecond.message_event

import org.javacord.api.event.message.reaction.ReactionAddEvent
import org.javacord.api.listener.message.reaction.ReactionAddListener
import java.util.function.Function

class ReactionManager : ReactionAddListener {
    private val hashMap = HashMap<Long, Function<ReactionAddEvent, Boolean>>()
    override fun onReactionAdd(reactionAddEvent: ReactionAddEvent) {
        if (hashMap.containsKey(reactionAddEvent.messageId)) {
            if (hashMap[reactionAddEvent.messageId]!!.apply(reactionAddEvent)) {
                hashMap.remove(reactionAddEvent.messageId)
            }
        }
    }

    fun addListener(id: Long, eventBot: Function<ReactionAddEvent, Boolean>) {
        hashMap[id] = eventBot
    }
}