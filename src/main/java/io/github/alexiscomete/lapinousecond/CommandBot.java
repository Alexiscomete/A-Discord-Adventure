package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public abstract class CommandBot {

    String description, name, totalDescription;

    public CommandBot(String description, String name, String totalDescription) {
        this.description = description;
        this.name = name;
        this.totalDescription = totalDescription;
    }

    abstract void execute(MessageCreateEvent messageCreateEvent, String content, String[] args);
}
