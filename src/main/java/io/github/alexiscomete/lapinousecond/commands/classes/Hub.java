package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import io.github.alexiscomete.lapinousecond.worlds.ServerBot;
import org.javacord.api.event.message.MessageCreateEvent;

public class Hub extends CommandBot {

    public Hub() {
        super("Vous permet de retourner au serveur principal **gratuitement**", "hub", "Permet de retourner gratuitement au hub si vous êtes bloqué dans un serveur, par exemple si il n'y a pas de salon textuel");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        Player p = Main.getSaveManager().getPlayer(messageCreateEvent.getMessageAuthor().getId());
        if (p == null) {
            messageCreateEvent.getMessage().reply("Voici le serveur principal : <https://discord.gg/q4hVQ6gwyx>");
            return;
        }
        if (content.endsWith("yes do it")) {
            messageCreateEvent.getMessage().reply("✔ Flavinou vient de vous téléporter au hub <https://discord.gg/q4hVQ6gwyx>");
            p.setServer(854288660147994634L);
            p.set("world", "NORMAL");
            p.set("place_NORMAL", new ServerBot(854288660147994634L).getString("places"));
        } else {
            messageCreateEvent.getMessage().reply("Etes-vous sûr de vouloir retourner au serveur principal ? Cette action ne peut pas être annulée. Tapez **hub yes do it** pour y retourner.");
        }
    }
}
