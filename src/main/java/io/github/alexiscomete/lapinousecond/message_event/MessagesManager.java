package io.github.alexiscomete.lapinousecond.message_event;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.HashMap;
import java.util.function.Consumer;

public class MessagesManager implements MessageCreateListener {
    HashMap<TextChannel, HashMap<Long, Consumer<MessageCreateEvent>>> consumers = new HashMap<>();

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if (consumers.containsKey(messageCreateEvent.getChannel()) && !messageCreateEvent.getMessage().getContent().startsWith("-")) {
            HashMap<Long, Consumer<MessageCreateEvent>> hashMap = consumers.get(messageCreateEvent.getChannel());
            if (messageCreateEvent.getMessageAuthor().isUser() && hashMap.containsKey(messageCreateEvent.getMessageAuthor().getId())) {
                Consumer<MessageCreateEvent> messageCreateEventConsumer = hashMap.get(messageCreateEvent.getMessageAuthor().getId());
                hashMap.remove(messageCreateEvent.getMessageAuthor().getId());
                messageCreateEventConsumer.accept(messageCreateEvent);
            }
        }
    }

    public void addListener(TextChannel textChannel, Long id, Consumer<MessageCreateEvent> messageCreateEventConsumer) {
        if (consumers.containsKey(textChannel)) {
            consumers.get(textChannel).put(id, messageCreateEventConsumer);
        } else {
            HashMap<Long, Consumer<MessageCreateEvent>> hashMap = new HashMap<>();
            hashMap.put(id, messageCreateEventConsumer);
            consumers.put(textChannel, hashMap);
        }
    }

    public void setValueAndRetry(TextChannel textChannel, Long id, String prog_name, String message, int len, ServerBot serverBot, Runnable ex) {
        Main.getMessagesManager().addListener(textChannel, id, new Consumer<MessageCreateEvent>() {
            @Override
            public void accept(MessageCreateEvent msgE) {
                if (msgE.getMessageContent().length() <= len) {
                    serverBot.set(prog_name, msgE.getMessageContent());
                    msgE.getMessage().reply(message);
                    ex.run();
                } else {
                    textChannel.sendMessage("Taille maximale : " + len + ". Votre taille : " + msgE.getMessageContent().length());
                    Main.getMessagesManager().addListener(textChannel, id, this);
                }
            }
        });
    }

    public void setValueAndRetry(TextChannel textChannel, Long id, String prog_name, EmbedBuilder message, int len, ServerBot serverBot, Runnable ex) {
        Main.getMessagesManager().addListener(textChannel, id, new Consumer<MessageCreateEvent>() {
            @Override
            public void accept(MessageCreateEvent msgE) {
                if (msgE.getMessageContent().length() <= len) {
                    serverBot.set(prog_name, msgE.getMessageContent());
                    msgE.getMessage().reply(message);
                    ex.run();
                } else {
                    textChannel.sendMessage("Taille maximale : " + len + ". Votre taille : " + msgE.getMessageContent().length());
                    Main.getMessagesManager().addListener(textChannel, id, this);
                }
            }
        });
    }
}
