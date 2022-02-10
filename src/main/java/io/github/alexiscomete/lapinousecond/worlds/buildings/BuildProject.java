package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.entity.Owner;
import io.github.alexiscomete.lapinousecond.save.SaveLocation;
import io.github.alexiscomete.lapinousecond.save.Tables;
import io.github.alexiscomete.lapinousecond.useful.ProgressionBar;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.component.LowLevelComponent;
import org.javacord.api.entity.message.embed.EmbedBuilder;

import java.awt.*;
import java.util.AbstractMap;
import java.util.HashMap;

public class BuildProject extends Building {

    private final ProgressionBar progressionBar;

    public BuildProject(long id) {
        super(id);
        progressionBar = new ProgressionBar("💰", 3, "🧱", 3, " ", 1, Double.parseDouble(getString("collect_target")), Double.parseDouble(getString("collect_value")), 60);
    }

    @Override
    public EmbedBuilder getInfos() {
        return new EmbedBuilder()
                .setColor(Color.CYAN)
                .setTitle("Construction d'un bâtiment")
                .setDescription("Type : " + getString("type"))
                .addInlineField("Owner", "Type : " + getString("type") + "\nIdentification : " + getString("owner"))
                .addInlineField("Progression", progressionBar.getBar());
    }

    @Override
    public MessageBuilder getCompleteInfos() {
        long id = SaveLocation.generateUniqueID();
        MessageBuilder messageBuilder = new MessageBuilder()
                .addEmbed(getInfos())
                .addComponents(ActionRow.of(
                        Button.success(String.valueOf(id), "Investire")
                ));
        Main.getButtonsManager().addButton(id, (messageComponentCreateEvent -> {
            messageComponentCreateEvent.getMessageComponentInteraction().createImmediateResponder().setContent("Entrez le montant à investire (l'investissement ne rapporte pas une 'part' du bâtiment, il faut pour cela utiliser une entreprise)");
            //TODO : suite
        }));
        return messageBuilder;
    }

    @SafeVarargs
    public BuildProject(Buildings buildings, double price, Owner owner, AbstractMap.SimpleEntry<String, String>... specialInfos) {
        super(SaveLocation.generateUniqueID());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", String.valueOf(id));
        Main.getSaveManager().insert(Tables.BUILDINGS.getTable().getName(), hashMap);
        set("collect_target", String.valueOf(price));
        set("type", buildings.getType());
        set("build_status", "building");
        set("owner_type", owner.getOwnerType());
        set("owner", owner.getOwnerString());
        for (AbstractMap.SimpleEntry<String, String> special :
             specialInfos) {
            set(special.getKey(), special.getValue());
        }
        progressionBar = new ProgressionBar("💰", 3, "🧱", 3, " ", 1, price, 0.0, 60);
    }
}
