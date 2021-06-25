package io.github.alexiscomete.lapinousecond;

import org.javacord.api.DiscordApi;
import org.javacord.api.event.message.MessageCreateEvent;

public class Travel extends CommandInServer {

    public Travel() {
        super("Vous permet de voyager vers un serveur", "travel", "travel / travel list / travel [server id]");
    }

    @Override
    public void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        //Main.api.getServerById(id);
    }
}
