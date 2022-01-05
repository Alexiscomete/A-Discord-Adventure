package io.github.alexiscomete.lapinousecond.reactions;

import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

import java.util.HashMap;

public class ReactionManager implements ReactionAddListener {

    HashMap<Long, AddReactionEventBot> hashMap = new HashMap<>();


    @Override
    public void onReactionAdd(ReactionAddEvent reactionAddEvent) {
        if (hashMap.containsKey(reactionAddEvent.getMessageId())) {
            if (hashMap.get(reactionAddEvent.getMessageId()).reactionAdd(reactionAddEvent)) {
                hashMap.remove(reactionAddEvent.getMessageId());
            }
        }
    }

    public void addListener(long id, AddReactionEventBot eventBot) {
        hashMap.put(id, eventBot);
    }
}
