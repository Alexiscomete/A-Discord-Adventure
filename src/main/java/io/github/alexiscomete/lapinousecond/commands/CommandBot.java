package io.github.alexiscomete.lapinousecond.commands;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.save.SaveManager;
import io.github.alexiscomete.lapinousecond.UserPerms;
import io.github.alexiscomete.lapinousecond.view.AnswerEnum;
import org.javacord.api.entity.channel.ServerTextChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.Optional;

public abstract class CommandBot {

    protected SaveManager saveManager = Main.getSaveManager();

    public String getDescription() {
        return description;
    }


    public String getName() {
        return name;
    }

    public String getTotalDescription() {
        return totalDescription;
    }

    private final String description, name, totalDescription;
    String[] perms;

    public CommandBot(String description, String name, String totalDescription, String... perms) {
        this.description = description;
        this.name = name;
        this.totalDescription = totalDescription;
        this.perms = perms;
    }

    public void checkAndExecute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        if (messageCreateEvent.isServerMessage()) {
            Optional<Server> serverOptional = messageCreateEvent.getServer();
            if (serverOptional.isPresent()) {
                Server s = serverOptional.get();
                if (s.getId() == 904736069080186981L && messageCreateEvent.getChannel().getId() != 914268153796771950L) {
                    return;
                } else {
                    Optional<ServerTextChannel> serverTextChannelOp = messageCreateEvent.getServerTextChannel();
                    if (serverTextChannelOp.isPresent()) {
                        ServerTextChannel sC = serverTextChannelOp.get();
                        String name = sC.getName();
                        // je pense que limiter les salons est important, venture permet d'inclure adventure et aventure
                        if (! (name.contains("bot") || name.contains("command") || name.contains("spam") || name.contains("🤖") || name.contains("venture"))) {
                            return;
                        }
                    }
                }
            }
        }
        try {
            if (perms == null || perms.length == 0) {
                execute(messageCreateEvent, content, args);
                return;
            }
            if (UserPerms.check(messageCreateEvent.getMessageAuthor().getId(), perms)) {
                execute(messageCreateEvent, content, args);
            } else {
                messageCreateEvent.getMessage().reply("Vous n'avez pas le droit d'exécuter cette commande");
            }
        } catch (Exception e) {
            messageCreateEvent.getMessage().reply("Erreur : \n```\n" + e.getLocalizedMessage() + "\n```");
            e.printStackTrace();
        }
    }

    public abstract void execute(MessageCreateEvent messageCreateEvent, String content, String[] args);

    public void sendImpossible(MessageCreateEvent messageCreateEvent, Player p) {
        messageCreateEvent.getMessage().reply(p.getAnswer(AnswerEnum.IMP_SIT, true));
    }

    public void sendArgs(MessageCreateEvent messageCreateEvent, Player p) {
        messageCreateEvent.getMessage().reply(p.getAnswer(AnswerEnum.NO_ENOUGH_ARGS, true));
    }

    public void sendNumberEx(MessageCreateEvent messageCreateEvent, Player p, int i) {
        messageCreateEvent.getMessage().reply(p.getAnswer(AnswerEnum.ILLEGAL_ARGUMENT_NUMBER, true, i));
    }
}
