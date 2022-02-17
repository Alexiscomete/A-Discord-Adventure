package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.Main;
import io.github.alexiscomete.lapinousecond.entity.Owner;
import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.save.CacheGetSet;
import io.github.alexiscomete.lapinousecond.save.SaveLocation;
import io.github.alexiscomete.lapinousecond.save.Tables;
import io.github.alexiscomete.lapinousecond.useful.ProgressionBar;
import io.github.alexiscomete.lapinousecond.view.AnswerEnum;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.component.ActionRow;
import org.javacord.api.entity.message.component.Button;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.MessageComponentInteraction;

import java.awt.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Objects;

public abstract class Building extends CacheGetSet {

    private final ProgressionBar progressionBar;

    public Building(long id) {
        super(id, Tables.BUILDINGS.getTable());
        progressionBar = new ProgressionBar("💰", 3, "🧱", 3, " ", 1, Double.parseDouble(getString("collect_target")), Double.parseDouble(getString("collect_value")), 60);
    }

    @SafeVarargs
    public Building(Buildings buildings, double price, Owner owner, AbstractMap.SimpleEntry<String, String>... specialInfos) {
        super(SaveLocation.generateUniqueID(), Tables.BUILDINGS.getTable());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", String.valueOf(id));
        Main.getSaveManager().insert(Tables.BUILDINGS.getTable().getName(), hashMap);
        set("collect_target", String.valueOf(price));
        set("type", buildings.getType());
        set("build_status", "building");
        set("owner_type", owner.getOwnerType());
        set("owner", owner.getOwnerString());
        set("collect_value", "0.0");
        for (AbstractMap.SimpleEntry<String, String> special :
                specialInfos) {
            set(special.getKey(), special.getValue());
        }
        progressionBar = new ProgressionBar("💰", 3, "🧱", 3, " ", 1, price, 0.0, 60);
    }

    public abstract EmbedBuilder getInfos(Player p);
    public abstract MessageBuilder getCompleteInfos(Player p);
    public abstract void configBuilding();

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

    EmbedBuilder inBuildInfos(Player p) {
        return new EmbedBuilder()
                .setColor(Color.CYAN)
                .setTitle(p.getAnswer(AnswerEnum.BUILDING_BA, true))
                .setDescription("Type : " + getString("type"))
                .addInlineField(p.getAnswer(AnswerEnum.OWNER, true), "Type : " + getString("type") + "\nIdentification : " + getString("owner"))
                .addInlineField(p.getAnswer(AnswerEnum.PROGRESSION, true), progressionBar.getBar() + "\n" + getString("collect_value") + "/" + getString("collect_target"));
    }

    MessageBuilder inBuildCompleteInfos(Player p) {
        long id = SaveLocation.generateUniqueID();
        MessageBuilder messageBuilder = new MessageBuilder()
                .addEmbed(inBuildInfos(p))
                .addComponents(ActionRow.of(
                        Button.success(String.valueOf(id), "Investir")
                ));

        Main.getButtonsManager().addButton(id, (messageComponentCreateEvent -> {
            MessageComponentInteraction msg = messageComponentCreateEvent.getMessageComponentInteraction();
            msg.createImmediateResponder().setContent("Entrez le montant à investir (l' investissement ne rapporte pas une 'part' du bâtiment, il faut pour cela utiliser une entreprise)");

            Main.getMessagesManager().addListener(msg.getChannel().get(), msg.getUser().getId(), messageCreateEvent -> {
                String content = messageCreateEvent.getMessageContent();
                try {
                    double price = Long.parseLong(content);
                    Player player = Main.getSaveManager().players.get(messageCreateEvent.getMessageAuthor().getId());
                    if (player == null) {
                        messageCreateEvent.getMessage().reply("Vous n'avez pas de compte, utilisez -start");
                        return;
                    }
                    double bal = player.getBal();
                    if (bal < price) {
                        messageCreateEvent.getMessage().reply("Hum ... vous n'avez pas cette argent, annulation");
                        return;
                    }
                    player.setBal(bal - price);
                    double coValue = Double.parseDouble(getString("collect_value"));
                    if (coValue + price > Double.parseDouble(getString("collect_target"))) {
                        messageCreateEvent.getMessage().reply("Montant trop élevé");
                        messageCreateEvent.getMessage().reply("Montant attendu : " + (Double.parseDouble(getString("collect_target")) - coValue));
                        return;
                    }
                    set("collect_value", String.valueOf(coValue + price));
                    messageCreateEvent.getMessage().reply("Transaction effectuée");
                    if (Objects.equals(getString("collect_value"), getString("collect_target"))) {
                        messageCreateEvent.getMessage().reply("Build terminé");
                        set("build_status", "finish");
                        configBuilding();
                    } else {
                        messageCreateEvent.getMessage().reply(inBuildInfos(p));
                    }
                } catch (NumberFormatException numberFormatException) {
                    messageCreateEvent.getMessage().reply("Ceci n'est pas un montant, annulation");
                }
            });
        }));

        return messageBuilder;
    }

}
