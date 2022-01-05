package io.github.alexiscomete.lapinousecond;


import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import io.github.alexiscomete.lapinousecond.commands.classes.*;
import io.github.alexiscomete.lapinousecond.reactions.ReactionManager;
import io.github.alexiscomete.lapinousecond.save.SaveLocation;
import io.github.alexiscomete.lapinousecond.save.SaveManager;
import io.github.alexiscomete.lapinousecond.save.Tables;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;

public class Main {

    public static DiscordApi api;
    public static SaveLocation<String> config;

    public static SaveManager getSaveManager() {
        return saveManager;
    }
    public static ReactionManager getReactionManager() {
        return reactionManager;
    }

    private static SaveManager saveManager;
    private static ReactionManager reactionManager;

    static {
        try {
            config = new SaveLocation<>(";", "/config.txt", a -> a);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("RIP Lapinou premier");
        config.loadAll();
        api = new DiscordApiBuilder().setToken(config.getContent().get(0)).login().join();
        api.updateActivity("Prefix : -");
        api.addListener(new ListenerMain());
        reactionManager = new ReactionManager();
        api.addListener(reactionManager);

        saveManager = new SaveManager(config.getContent().get(1));
        Tables.testTables();

        addCommand(new Help());
        addCommand(new Hello());
        addCommand(new Work());
        addCommand(new Shop());
        addCommand(new Give());
        addCommand(new PlayerShop());
        addCommand(new InventoryC());
        addCommand(new ConfigServ());
        addCommand(new Travel());
        addCommand(new Hub());
        addCommand(new StartAdventure());
        addCommand(new UseCommand());
        addCommand(new PermsManager());
        addCommand(new Invite());
        addCommand(new Verify());
        addCommand(new RoleCommand());
    }

    public static void addCommand(CommandBot commandBot) {
        ListenerMain.commands.put(commandBot.getName(), commandBot);
    }
}
