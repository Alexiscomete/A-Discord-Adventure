package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.save.SaveLocation;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public class BuildProject extends Building {

    public BuildProject(long id) {
        super(id);
    }

    @Override
    public EmbedBuilder getInfos() {
        return null;
    }

    @Override
    public MessageBuilder getCompleteInfos() {
        return null;
    }

    public BuildProject(Buildings buildings) {
        super(SaveLocation.generateUniqueID());
        createNew(buildings);
    }

    public void createNew(Buildings buildings) {
        set("id", String.valueOf(id));
        set("type", buildings.getType());
    }


}
