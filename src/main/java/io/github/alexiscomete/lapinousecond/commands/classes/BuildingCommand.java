package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandInServer;
import io.github.alexiscomete.lapinousecond.entity.Player;
import org.javacord.api.event.message.MessageCreateEvent;

public class BuildingCommand extends CommandInServer {

    public BuildingCommand() {
        super("Toutes les actions relatives aux bâtiments", "build", "Utilisation :\n- enter [id] pour entrer dans un bâtiment\n- exit pour sortir\n- la commande seule permet de voir la liste des bâtiments ou de voir les informations rapides\n- infos pour voir les infos détaillées");
    }

    @Override
    public void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        String building = p.getString("building");
        if (building.equals("") || building.equals("exit"))
        if (args.length > 1) {
            switch (args[1]) {

            }
        } else {

        }
    }
}
