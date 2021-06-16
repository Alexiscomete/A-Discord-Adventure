package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.HashMap;

public class ListenerMain implements MessageCreateListener {

    public static HashMap<String, CommandBot> commands = new HashMap<>();

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if (messageCreateEvent.getMessageContent().startsWith("-")) {
            String content = messageCreateEvent.getMessageContent().substring(1);
            String[] args = content.split(" ");
            CommandBot commandBot = commands.get(args[0]);
            if (commandBot != null) {
                commandBot.execute(messageCreateEvent, content, args);
            }
        }
    }
}
