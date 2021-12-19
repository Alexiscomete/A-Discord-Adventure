package io.github.alexiscomete.lapinousecond;


import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import io.github.alexiscomete.lapinousecond.commands.classes.*;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;

public class Main {

    public static DiscordApi api;
    public static SaveLocation<String> config;

    public static SaveManager getSaveManager() {
        return saveManager;
    }

    private static SaveManager saveManager;

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

        saveManager = new SaveManager(config.getContent().get(1), config.getContent().get(2), config.getContent().get(3));

        addCommand(new Help());
        addCommand(new Hello());
        addCommand(new Work());
        addCommand(new Sec());
        addCommand(new PlayerShop());
        addCommand(new Buy());
        addCommand(new Give());
        addCommand(new SellPlayerShop());
        addCommand(new InventoryC());
        addCommand(new ConfigServ());
        addCommand(new Report());
        addCommand(new SetServerSec());
        addCommand(new Travel());
        addCommand(new Hub());
        addCommand(new StartAdventure());
        addCommand(new Introduction());
        addCommand(new UseCommand());
        addCommand(new PermsManager());
    }

    public static void addCommand(CommandBot commandBot) {
        ListenerMain.commands.put(commandBot.getName(), commandBot);
    }
}
