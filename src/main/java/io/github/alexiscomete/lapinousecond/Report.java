package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class Report extends CommandBot {

    public Report() {
        super("Permet de report aux modérateurs de A Discord Adventure un abus", "report", "Permet de report aux modérateurs de A Discord Adventure un abus. A utiliser si un un joueur ou un serveur abuse de fait que Lapinou Second donne des invitations pour des serveurs.");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {

    }
}
