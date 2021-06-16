package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public abstract class CommandBot {
    abstract void execute(MessageCreateEvent messageCreateEvent, String content, String[] args);
}
