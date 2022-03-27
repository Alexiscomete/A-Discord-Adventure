package io.github.alexiscomete.lapinousecond.worlds.buildings.interactions;

import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.worlds.buildings.Building;
import io.github.alexiscomete.lapinousecond.worlds.buildings.BuildingInteraction;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class Marche extends BuildingInteraction {
    public Marche(Building building) {
        super(building);
    }

    @Override
    public void interpret(String[] args) {

    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public EmbedBuilder getInfos(Player p) {
        return null;
    }

    @Override
    public MessageBuilder getCompleteInfos(Player p) {
        return null;
    }

    @Override
    public void configBuilding() {

    }
}
