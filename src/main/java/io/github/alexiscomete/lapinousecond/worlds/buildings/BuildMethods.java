package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.entity.Player;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public interface BuildMethods {

    EmbedBuilder getInfos(Player p);
    MessageBuilder getCompleteInfos(Player p);
    void configBuilding();
}
