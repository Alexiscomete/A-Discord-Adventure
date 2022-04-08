package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandWithAccount;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.worlds.World;
import io.github.alexiscomete.lapinousecond.worlds.WorldEnum;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

/**
 * @author Alexiscomete
 *
 * Command to change the world of the player
 */
public class WorldCommand extends CommandWithAccount {

    public WorldCommand() {
        super("Commande pour changer de monde", "world", "Commande pour changer de monde, utiliser world change [nom du monde] pour changer de monde et world list pour voir les mondes disponibles");
    }


    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        if (args.length > 1) {
            if (args[1].equals("change")) {
                if (args.length > 2) {
                    try {
                        WorldEnum worldEnum = WorldEnum.valueOf(args[2].toUpperCase());
                        World world = worldEnum.getWorld();

                    } catch (IllegalArgumentException e) {
                        EmbedBuilder eb = new EmbedBuilder();
                        eb.setTitle("Monde inconnu");
                        eb.setDescription("Le monde " + args[2] + " n'existe pas");
                        messageCreateEvent.getChannel().sendMessage(eb);
                    }
                } else {
                    messageCreateEvent.getChannel().sendMessage("Veuillez entrer le nom du monde");
                }
            } else if (args[1].equals("list")) {
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setTitle("Liste des mondes")
                        .setDescription("Voici la liste des mondes disponibles");
                for (WorldEnum worldEnum : WorldEnum.values()) {
                    World world = worldEnum.getWorld();
                    embedBuilder.addField(world.getName(), "(" + world.getProgName() + ") -> NomRP : " + world.getNameRP() + ". Description : " + world.getDesc(), false);
                }
                messageCreateEvent.getMessage().reply(embedBuilder);
            }
        } else {
            sendArgs(messageCreateEvent, p);
        }
    }
}
