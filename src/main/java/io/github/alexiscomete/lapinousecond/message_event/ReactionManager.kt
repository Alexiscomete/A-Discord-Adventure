package io.github.alexiscomete.lapinousecond.message_event;

import org.javacord.api.event.message.reaction.ReactionAddEvent;
import org.javacord.api.listener.message.reaction.ReactionAddListener;

import java.util.HashMap;
import java.util.function.Function;

public class ReactionManager implements ReactionAddListener {

    final HashMap<Long, Function<ReactionAddEvent, Boolean>> hashMap = new HashMap<>();


    @Override
    public void onReactionAdd(ReactionAddEvent reactionAddEvent) {
        if (hashMap.containsKey(reactionAddEvent.getMessageId())) {
            if (hashMap.get(reactionAddEvent.getMessageId()).apply(reactionAddEvent)) {
                hashMap.remove(reactionAddEvent.getMessageId());
            }
        }
    }

    public void addListener(long id, Function<ReactionAddEvent, Boolean> eventBot) {
        hashMap.put(id, eventBot);
    }
}
