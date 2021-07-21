package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

@FunctionalInterface
public interface UseItem {
    boolean use(MessageCreateEvent event, String content, String[] args, Player ownerOfItem);
}
