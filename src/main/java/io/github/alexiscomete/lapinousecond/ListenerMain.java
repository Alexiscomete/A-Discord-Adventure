package io.github.alexiscomete.lapinousecond;

import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.HashMap;

public class ListenerMain implements MessageCreateListener {

    public static HashMap<String, CommandBot> commands = new HashMap<>();

    /**
     * Quand une personne envoie un message sur un salon visible par le bot
     * @param messageCreateEvent le message
     */
    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if (messageCreateEvent.getMessageContent().startsWith("-")) {
            System.out.println("bug");
            String content = messageCreateEvent.getMessageContent().toLowerCase().substring(1);
            String[] args = content.split(" ");
            CommandBot commandBot = commands.get(args[0]);
            System.out.println("null?");
            if (commandBot != null && !messageCreateEvent.getMessageAuthor().isBotUser()) {
                System.out.println("no");
                commandBot.checkAndExecute(messageCreateEvent, content, args);
            }
        }
    }
}
