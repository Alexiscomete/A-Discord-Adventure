package io.github.alexiscomete.lapinousecond.message_event;

import org.javacord.api.entity.message.Message;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ButtonsManager implements MessageComponentCreateListener {
    HashMap<Long, HashMap<String, Consumer<MessageComponentCreateEvent>>> hashMap = new HashMap<>();

    @Override
    public void onComponentCreate(MessageComponentCreateEvent messageComponentCreateEvent) {
        if (hashMap.containsKey(messageComponentCreateEvent.getMessageComponentInteraction().getMessageId())) {
            HashMap<String, Consumer<MessageComponentCreateEvent>> h = hashMap.get(messageComponentCreateEvent.getMessageComponentInteraction().getMessageId());
            if (h.containsKey(messageComponentCreateEvent.getMessageComponentInteraction().getCustomId())) {
                h.get(messageComponentCreateEvent.getMessageComponentInteraction().getCustomId()).accept(messageComponentCreateEvent);
            } else {
                messageComponentCreateEvent.getMessageComponentInteraction().getMessage().get().reply("Hum ... étrange, ce bouton semble ne pas exister");
            }
        } else {
            Message message = messageComponentCreateEvent.getMessageComponentInteraction().getMessage().get();
            message.edit("Les boutons d'un message ne sont pas stockés dans la mémoire à long terme, il est donc impossible de répondre à cette demande");
        }
    }

    public void addMessage(long id, HashMap<String, Consumer<MessageComponentCreateEvent>> hash) {
        hashMap.put(id, hash);
    }
}
