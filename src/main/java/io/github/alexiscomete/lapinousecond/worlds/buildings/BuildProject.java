package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.save.SaveLocation;
import io.github.alexiscomete.lapinousecond.save.Tables;
import io.github.alexiscomete.lapinousecond.useful.ProgressionBar;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.util.AbstractMap;
import java.util.HashMap;

public class BuildProject extends Building {

    private ProgressionBar progressionBar;

    public BuildProject(long id) {
        super(id);
        progressionBar = new ProgressionBar("💰", 3, "🧱", 3, " ", 1, Double.parseDouble(getString("collect_target")), Double.parseDouble(getString("collect_value")), 60);
    }

    @Override
    public EmbedBuilder getInfos() {
        return null;
    }

    @Override
    public MessageBuilder getCompleteInfos() {
        return null;
    }

    @SafeVarargs
    public BuildProject(Buildings buildings, double price, String ownerType, String owner, AbstractMap.SimpleEntry<String, String>... specialInfos) {
        super(SaveLocation.generateUniqueID());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", String.valueOf(id));
        Main.getSaveManager().insert(Tables.BUILDINGS.getTable().getName(), hashMap);
        set("collect_target", String.valueOf(price));
        set("type", buildings.getType());
        set("build_status", "building");
        set("owner_type", ownerType);
        set("owner", owner);
        for (AbstractMap.SimpleEntry<String, String> special :
             specialInfos) {
            set(special.getKey(), special.getValue());
        }
        progressionBar = new ProgressionBar("💰", 3, "🧱", 3, " ", 1, price, 0.0, 60);
    }
}
