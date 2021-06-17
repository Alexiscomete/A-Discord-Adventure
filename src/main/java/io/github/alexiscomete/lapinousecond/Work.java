package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class Work extends CommandBot {

    public Work() {
        super("Gagnez de l'argent du jeu", "work", "Utilisable régulièrement pour gagner un peut d'argent du jeu, c'est le moyen le plus simple d'en gagner. Plus vous évolurez dans le jeu plus cette commande vous donnera de l'argent. Votre métier peut aussi influencer le gain. Vous pourrez peut-être choisir un type de work. Bon je parle mais en fait tout ça est loin d'être codé.");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        messageCreateEvent.getMessage().reply("Impossible pour le moment");
    }
}
