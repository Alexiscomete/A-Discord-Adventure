package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class Hub extends CommandBot {

    public Hub() {
        super("Vous permet de retourner au serveur principal **gratuitement**", "hub", "Permet de retourner gratuitement au hub si vous êtes bloqué dans un serveur, par exemple si il n'y a pas de salon textuel");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        Player p = SaveManager.getPlayer(messageCreateEvent.getMessageAuthor().getId());
        if (p == null) {
            messageCreateEvent.getMessage().reply("Voici le serveur principal : <https://discord.gg/q4hVQ6gwyx>");
            return;
        }
        if (content.endsWith("yes do it")) {
            messageCreateEvent.getMessage().reply("✔ Flavinou vient de vous téléporter au hub <https://discord.gg/q4hVQ6gwyx>");
            p.setServer(854288660147994634L);
        } else {
            messageCreateEvent.getMessage().reply("Etes-vous sûr de vouloir retourner au serveur principal ? Cette action ne peut pas être annulée. Tapez **hub yes do it** pour y retourner.");
        }
    }
}
