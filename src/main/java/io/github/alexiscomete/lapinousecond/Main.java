package io.github.alexiscomete.lapinousecond;


import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;
import java.util.function.Function;

public class Main {

    public static DiscordApi api;
    public static SaveLocation<String> config;

    static {
        try {
            config = new SaveLocation<String>(";", "/config.txt", a -> a);
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

        SaveManager.path = config.getContent().get(1);
        SaveManager.user = config.getContent().get(2);
        SaveManager.mdp = config.getContent().get(3);

        SaveManager.connection();

        addCommand(new Help());
        addCommand(new Hello());
        addCommand(new Work());
        addCommand(new Sec());
        addCommand(new PlayerShop());
        addCommand(new Buy());
        addCommand(new Give());
        addCommand(new SellPlayerShop());
        addCommand(new InventoryC());
        addCommand(new StartAdventure());
        addCommand(new Introduction());

    }

    public static void addCommand(CommandBot commandBot) {
        ListenerMain.commands.put(commandBot.name, commandBot);
    }
}
