package io.github.alexiscomete.lapinousecond.message_event;

import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.util.HashMap;
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
                messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater()
                        .removeAllComponents()
                        .removeAllEmbeds()
                        .setContent("Hum ... étrange, ce bouton semble ne pas exister")
                        .update();
            }
        } else {
            messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater().removeAllEmbeds().removeAllComponents().setContent("Il est impossible de répondre à cette demande, soit le bouton est invalide soit le bot a été redémarré (pas de mémoire à long terme pour les boutons)").update();
        }
    }

    public void addMessage(long id, HashMap<String, Consumer<MessageComponentCreateEvent>> hash) {
        hashMap.put(id, hash);
    }
}
