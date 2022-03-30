package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.event.message.MessageCreateEvent;

/**
 * Hello command, used to nothing.
 */
public class Hello extends CommandBot {

    public Hello() {
        super("Hello", "hello", "Say Hello World!");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        messageCreateEvent.getMessage().reply("Hello World!");
    }
}
