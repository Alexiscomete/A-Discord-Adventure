package io.github.alexiscomete.lapinousecond.reactions;

import org.javacord.api.event.message.reaction.ReactionAddEvent;

@FunctionalInterface
public interface AddReactionEventBot {
    boolean reactionAdd(ReactionAddEvent event);
}
