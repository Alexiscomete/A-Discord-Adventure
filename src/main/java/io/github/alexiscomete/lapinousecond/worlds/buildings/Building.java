package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.entity.Owner;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.save.CacheGetSet;
import io.github.alexiscomete.lapinousecond.save.SaveLocation;
import io.github.alexiscomete.lapinousecond.save.Tables;
import io.github.alexiscomete.lapinousecond.useful.FullTransaction;
import io.github.alexiscomete.lapinousecond.useful.ProgressionBar;
import io.github.alexiscomete.lapinousecond.view.AnswerEnum;
import io.github.alexiscomete.lapinousecond.worlds.Place;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;
import org.json.JSONObject;

import java.awt.*;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Objects;
import java.util.Scanner;

public class Building extends CacheGetSet implements BuildMethods {

    // the json file who contains buildings type information
    public static final JSONObject jsonObject;

    // load the json file
    static {
        InputStream inputStream = Building.class.getClassLoader().getResourceAsStream("buildings_config.json");
        System.out.println(inputStream);
        if (inputStream == null) {
            System.out.println("eeee");
            jsonObject = new JSONObject("{}");
        } else {
            Scanner sc = new Scanner(inputStream);
            StringBuilder stringBuilder = new StringBuilder();
            sc.forEachRemaining(stringBuilder::append);
            jsonObject = new JSONObject(stringBuilder.toString());
        }
    }

    private BuildingInteraction buildingInteraction;
    private final ProgressionBar progressionBar;

    // constructor for the building if it's already exist
    public Building(long id, Buildings buildings) {
        super(id, Tables.BUILDINGS.getTable());
        progressionBar = new ProgressionBar("💰", 3, "🧱", 3, " ", 1, Double.parseDouble(getString("collect_target")), Double.parseDouble(getString("collect_value")), 20);
        this.buildingInteraction = buildings.get(this);
    }

    // constructor for the building if it's not exist yet
    @SafeVarargs
    public Building(Buildings buildings, Owner owner, Place place, AbstractMap.SimpleEntry<String, String>... specialInfos) {
        super(SaveLocation.generateUniqueID(), Tables.BUILDINGS.getTable());
        Main.getSaveManager().buildings.add(id);
        String buildingsString = place.getString("buildings");
        buildingsString += ";" + id;
        place.set("buildings", buildingsString);
        set("collect_target", String.valueOf(buildings.getBasePrice()));
        set("type", buildings.getName());
        set("build_status", "building");
        set("owner_type", owner.getOwnerType());
        set("owner", owner.getOwnerString());
        set("collect_value", "0.0");
        for (AbstractMap.SimpleEntry<String, String> special :
                specialInfos) {
            set(special.getKey(), special.getValue());
        }
        progressionBar = new ProgressionBar("💰", 3, "🧱", 3, " ", 1, buildings.getBasePrice(), 0.0, 20);
    }

    public EmbedBuilder infos(Player p) {
        if (getString("build_status").equals("building")) {
            return inBuildInfos(p);
        } else {
            return getInfos(p);
        }
    }

    public MessageBuilder completeInfos(Player p) {
        if (getString("build_status").equals("building")) {
            return inBuildCompleteInfos(p);
        } else {
            return getCompleteInfos(p);
        }
    }

    private EmbedBuilder inBuildInfos(Player p) {
        return new EmbedBuilder()
                .setColor(Color.CYAN)
                .setTitle(p.getAnswer(AnswerEnum.BUILDING_BA, true))
                .setDescription("Type : " + getString("type") + "\n ID : " + id)
                .addInlineField(p.getAnswer(AnswerEnum.OWNER, true), "Type : " + getString("type") + "\nIdentification : " + getString("owner"))
                .addInlineField(p.getAnswer(AnswerEnum.PROGRESSION, true), progressionBar.getBar() + "\n" + getString("collect_value") + "/" + getString("collect_target"));
    }

    MessageBuilder inBuildCompleteInfos(Player p) {
        long id = SaveLocation.generateUniqueID();
        MessageBuilder messageBuilder = new MessageBuilder()
                .addEmbed(inBuildInfos(p))
                .addComponents(ActionRow.of(
                        Button.success(String.valueOf(id), "Donner de l'argent")
                ));


        Main.getButtonsManager().addButton(id, (messageComponentCreateEvent -> {
            MessageComponentInteraction msg = messageComponentCreateEvent.getMessageComponentInteraction();


            FullTransaction transaction = new FullTransaction(aDouble -> {
                set("collect_value", String.valueOf(Double.parseDouble(getString("collect_value")) + aDouble));
                if (Objects.equals(getString("collect_value"), getString("collect_target"))) {
                    msg.getChannel().get().sendMessage("Build terminé");
                    set("build_status", "finish");
                    configBuilding();
                } else {
                    msg.getChannel().get().sendMessage(inBuildInfos(p));
                }
            }, aDouble -> p.setBal(p.getBal() - aDouble), p::getBal, p, () -> Double.parseDouble(getString("collect_target")) - Double.parseDouble(getString("collect_value")));
            transaction.full(msg);
        }));

        return messageBuilder;
    }

    public void evolute(BuildingInteraction buildingInteraction) {
        this.buildingInteraction = buildingInteraction;
    }

    // --------------------------------------------------
    // --------------- BUILDING INTERACTION -------------
    // --------------------------------------------------

    @Override
    public void configBuilding() {
        buildingInteraction.configBuilding();
    }

    @Override
    public void interpret(String[] args) {
        buildingInteraction.interpret(args);
    }

    @Override
    public String getHelp() {
        return buildingInteraction.getHelp();
    }

    @Override
    public String getUsage() {
        return buildingInteraction.getUsage();
    }

    @Override
    public EmbedBuilder getInfos(Player p) {
        return buildingInteraction.getInfos(p);
    }

    @Override
    public MessageBuilder getCompleteInfos(Player p) {
        return buildingInteraction.getCompleteInfos(p);
    }
}
