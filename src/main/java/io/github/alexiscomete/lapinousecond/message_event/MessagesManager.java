package io.github.alexiscomete.lapinousecond.message_event;

import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.util.HashMap;
import java.util.function.Consumer;

public class MessagesManager implements MessageCreateListener {
    HashMap<TextChannel, HashMap<User, Consumer<MessageCreateEvent>>> consumers = new HashMap<>();

    @Override
    public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
        if (consumers.containsKey(messageCreateEvent.getChannel()) && !messageCreateEvent.getMessage().getContent().startsWith("-")) {
            HashMap<User, Consumer<MessageCreateEvent>> hashMap = consumers.get(messageCreateEvent.getChannel());
            if (messageCreateEvent.getMessageAuthor().isUser() && hashMap.containsKey(messageCreateEvent.getMessageAuthor().asUser().get())) {
                Consumer<MessageCreateEvent> messageCreateEventConsumer = hashMap.get(messageCreateEvent.getMessageAuthor().asUser().get());
                hashMap.remove(messageCreateEvent.getMessageAuthor().asUser().get());
                messageCreateEventConsumer.accept(messageCreateEvent);
            }
        }
    }

    public void addListener(TextChannel textChannel, User user, Consumer<MessageCreateEvent> messageCreateEventConsumer) {
        if (consumers.containsKey(textChannel)) {
            consumers.get(textChannel).put(user, messageCreateEventConsumer);
        } else {
            HashMap<User, Consumer<MessageCreateEvent>> hashMap = new HashMap<>();
            hashMap.put(user, messageCreateEventConsumer);
            consumers.put(textChannel, hashMap);
        }
    }
}
