package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandInServer;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.view.AnswerEnum;
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building;
import io.github.alexiscomete.lapinousecond.worlds.buildings.Buildings;
import org.javacord.api.event.message.MessageCreateEvent;

public class BuildingCommand extends CommandInServer {

    public BuildingCommand() {
        super("Toutes les actions relatives aux bâtiments", "build", "Utilisation :\n- enter [id] pour entrer dans un bâtiment\n- exit pour sortir\n- la commande seule permet de voir la liste des bâtiments ou de voir les informations rapides\n- infos pour voir les infos détaillées");
    }

    @Override
    public void executeC(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        String building = p.getString("building");
        Building building1;
        if (!(building.equals("") || building.equals("exit"))) {
            building1 = null;
        } else {
            building1 = Buildings.load(building);
        }
        if (args.length > 1) {
            switch (args[1]) {
                case "enter":
                    if (building1 == null) {

                    } else {
                        sendImpossible(messageCreateEvent, p);
                    }
                    break;
                case "exit":
                    if (building1 == null) {
                        sendImpossible(messageCreateEvent, p);
                    } else {

                    }
                    break;
                case "infos":
                    if (building1 == null) {
                        sendImpossible(messageCreateEvent, p);
                    } else {
                        building1.completeInfos(p).send(messageCreateEvent.getChannel());
                    }
                    break;
                case "build":
                    if (building1 == null) {

                    } else {
                        sendImpossible(messageCreateEvent, p);
                    }
                    break;
                default:
                    if (building1 == null) {
                        sendImpossible(messageCreateEvent, p);
                    } else {

                    }
                    break;
            }
        } else {
            if (building1 == null) {

            } else {
                messageCreateEvent.getMessage().reply(building1.infos(p));
            }
        }
    }

    public void sendImpossible(MessageCreateEvent messageCreateEvent, Player p) {
        messageCreateEvent.getMessage().reply(p.getAnswer(AnswerEnum.IMP_SIT, true));
    }
}
