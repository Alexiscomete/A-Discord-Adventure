package io.github.alexiscomete.lapinousecond.message_event;

import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.listener.interaction.MessageComponentCreateListener;

import java.util.HashMap;
import java.util.function.Consumer;

public class ButtonsManager implements MessageComponentCreateListener {
    final HashMap<Long, HashMap<String, Consumer<MessageComponentCreateEvent>>> hashMap = new HashMap<>();
    final HashMap<Long, Consumer<MessageComponentCreateEvent>> hashButton = new HashMap<>();

    @Override
    public void onComponentCreate(MessageComponentCreateEvent messageComponentCreateEvent) {
        if (hashMap.containsKey(messageComponentCreateEvent.getMessageComponentInteraction().getMessage().getId())) {
            HashMap<String, Consumer<MessageComponentCreateEvent>> h = hashMap.get(messageComponentCreateEvent.getMessageComponentInteraction().getMessage().getId());
            if (h.containsKey(messageComponentCreateEvent.getMessageComponentInteraction().getCustomId())) {
                try {
                    h.get(messageComponentCreateEvent.getMessageComponentInteraction().getCustomId()).accept(messageComponentCreateEvent);
                } catch (Exception e) {
                    if (messageComponentCreateEvent.getMessageComponentInteraction().getChannel().isPresent()) {
                        messageComponentCreateEvent.getMessageComponentInteraction().getChannel().get().sendMessage("Une erreur est survenue : " + e.getMessage());
                    } else {
                        messageComponentCreateEvent.getMessageComponentInteraction().getUser().sendMessage("Une erreur est survenue : " + e.getMessage() + "\n Impossible de répondre à votre message dans le channel donc ce message est envoyé en DM.");
                    }
                }
            } else {
                messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater()
                        .removeAllComponents()
                        .removeAllEmbeds()
                        .setContent("Hum ... étrange, ce bouton semble ne pas exister")
                        .update();
            }
        } else if (hashButton.containsKey(Long.parseLong(messageComponentCreateEvent.getMessageComponentInteraction().getCustomId()))) {
            hashButton.get(Long.parseLong(messageComponentCreateEvent.getMessageComponentInteraction().getCustomId())).accept(messageComponentCreateEvent);
        } else {
            messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater().removeAllEmbeds().removeAllComponents().setContent("Il est impossible de répondre à cette demande, soit le bouton est invalide soit le bot a été redémarré (pas de mémoire à long terme pour les boutons)").update();
        }
    }

    public void addMessage(long id, HashMap<String, Consumer<MessageComponentCreateEvent>> hash) {
        hashMap.put(id, hash);
    }

    public void addButton(long id, Consumer<MessageComponentCreateEvent> eventConsumer) {
        hashButton.put(id, eventConsumer);
    }
}
