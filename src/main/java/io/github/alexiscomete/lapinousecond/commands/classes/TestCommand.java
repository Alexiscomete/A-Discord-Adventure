package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandWithAccount;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import io.github.alexiscomete.lapinousecond.worlds.PlaceForTest;
import io.github.alexiscomete.lapinousecond.worlds.map.Map;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class TestCommand extends CommandWithAccount {

    public TestCommand() {
        super("test", "test", "test");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args, Player p) {
        messageCreateEvent.getMessage().reply("Aucun test en cours, revenez plus tard.");
    }
}
