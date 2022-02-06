package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.save.CacheGetSet;
import io.github.alexiscomete.lapinousecond.save.Tables;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

public abstract class Building extends CacheGetSet {

    public Building(long id) {
        super(id, Tables.BUILDINGS.getTable());
    }

    public abstract EmbedBuilder getInfos();
    public abstract MessageBuilder getCompleteInfos();

}
