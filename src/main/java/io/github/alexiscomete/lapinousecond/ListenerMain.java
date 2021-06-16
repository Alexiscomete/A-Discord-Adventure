package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.HashMap;

public class ListenerMain implements MessageCreateListener {

    public static HashMap<String, CommandBot> commands = new HashMap<>();

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if (messageCreateEvent.getMessageContent().startsWith("-")) {
            System.out.println("bug");
            String content = messageCreateEvent.getMessageContent().toLowerCase().substring(1);
            String[] args = content.split(" ");
            CommandBot commandBot = commands.get(args[0]);
            System.out.println("null?");
            if (commandBot != null) {
                System.out.println("no");
                commandBot.execute(messageCreateEvent, content, args);
            }
        }
    }
}
