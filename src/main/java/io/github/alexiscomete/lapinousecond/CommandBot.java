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
        if (perms == null || perms.length == 0) {
            execute(messageCreateEvent, content, args);
            return;
        }
        if (UserPerms.check(messageCreateEvent.getMessageAuthor().getId(), perms)) {
            execute(messageCreateEvent, content, args);
        } else {
            messageCreateEvent.getMessage().reply("Vous n'avez pas le droit d'exécuter cette commande");
        }
    }

    abstract void execute(MessageCreateEvent messageCreateEvent, String content, String[] args);
}
