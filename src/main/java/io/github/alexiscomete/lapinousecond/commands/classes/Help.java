package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.ListenerMain;
import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import io.github.alexiscomete.lapinousecond.message_event.ButtonsManager;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.interaction.MessageComponentCreateEvent;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class Help extends CommandBot {

    public Help() {
        super("Vous affiche l'aide du bot. (help [commande] pour plus d'informations)", "help", "Vous affiche l'aide du bot. (help [commande] pour plus d'informations)");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        System.out.println("help!!");
        EmbedBuilder builder = new EmbedBuilder();
        MessageBuilder messageBuilder = new MessageBuilder();
        builder.setDescription("Pensez au préfix !").setTitle("Aide").setColor(Color.blue);
        if (args.length < 2) {
            addCommands(builder, 0);
            messageBuilder.addComponents(
                    ActionRow.of(
                            Button.success("last_page", "Page précédente"),
                            Button.success("next_page", "Page suivante")
                    ));
            try {
                int[] level = {0};
                messageBuilder.append(builder);
                Message msg = messageBuilder.send(messageCreateEvent.getChannel()).get();
                HashMap<String, Consumer<MessageComponentCreateEvent>> hashMap = new HashMap<>();
                hashMap.put("next_page", messageComponentCreateEvent -> {
                    if (level[0] + 10 < ListenerMain.commands.size()) {
                        level[0] += 10;
                        builder.removeAllFields();
                        addCommands(builder, level[0]);
                        messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater().removeAllEmbeds().addEmbed(builder).update();
                    }
                });
                hashMap.put("last_page", messageComponentCreateEvent -> {
                    if (level[0] > 9) {
                        level[0] -= 10;
                        builder.removeAllFields();
                        addCommands(builder, level[0]);
                        messageComponentCreateEvent.getMessageComponentInteraction().createOriginalMessageUpdater().removeAllEmbeds().addEmbed(builder).update();
                    }
                });
                Main.getButtonsManager().addMessage(msg.getId(), hashMap);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            CommandBot commandBot = ListenerMain.commands.get(args[1]);
            if (commandBot == null) {
                builder.addField("👀", "Commande inconnue");
            } else {
                builder.addField(commandBot.getName(), commandBot.getTotalDescription());
            }
            messageBuilder.replyTo(messageCreateEvent.getMessage());
        }
    }

    public void addCommands(EmbedBuilder embedBuilder, int min) {
        CommandBot[] commandBots = ListenerMain.commands.values().toArray(new CommandBot[0]);
        if (commandBots.length > min) {
            for (int i = min; i < commandBots.length && i < min + 10; i++) {
                CommandBot commandBot = commandBots[i];
                embedBuilder.addField(commandBot.getName(), commandBot.getDescription());
            }
        } else {
            embedBuilder.addField("Erreur", "Impossible de trouver la commande n°" + min);
        }
    }
}
