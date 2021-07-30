package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public abstract class CommandBot {

    String description, name, totalDescription;
    String[] perms;

    public CommandBot(String description, String name, String totalDescription, String... perms) {
        this.description = description;
        this.name = name;
        this.totalDescription = totalDescription;
        this.perms = perms;
    }

    public void checkAndExecute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        if (perms == null) {
            execute(messageCreateEvent, content, args);
            return;
        }
        if (UserPerms.check(messageCreateEvent.getMessageAuthor().getId(), perms)) {
            execute(messageCreateEvent, content, args);
        }
    }

    abstract void execute(MessageCreateEvent messageCreateEvent, String content, String[] args);
}
