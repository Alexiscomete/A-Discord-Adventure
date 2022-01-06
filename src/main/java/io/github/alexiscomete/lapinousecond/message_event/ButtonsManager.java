package io.github.alexiscomete.lapinousecond.message_event;

import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.util.HashMap;
import java.util.function.Consumer;

public class ButtonsManager implements MessageComponentCreateListener {
    HashMap<Long, HashMap<String, Consumer<MessageComponentCreateEvent>>> hashMap = new HashMap<>();

    @Override
    public void onComponentCreate(MessageComponentCreateEvent messageComponentCreateEvent) {

    }
}
