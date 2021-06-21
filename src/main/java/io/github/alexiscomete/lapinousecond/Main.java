package io.github.alexiscomete.lapinousecond;


import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;

import java.io.IOException;

public class Main {

    public static DiscordApi api;
    public static SaveLocation<String> config;

    static {
        try {
            config = new SaveLocation<>(";", "/config.txt", (String a) -> a);
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

        ListenerMain.commands.put("help", new Help());
        ListenerMain.commands.put("hello", new Hello());
        ListenerMain.commands.put("work", new Work());
        ListenerMain.commands.put("sec", new Sec());
        ListenerMain.commands.put("pc", new PlayerShop());
        ListenerMain.commands.put("buy", new Buy());
        ListenerMain.commands.put("give", new Give());
        ListenerMain.commands.put("sell", new SellPlayerShop());
        ListenerMain.commands.put("inv", new InventoryC());
        ListenerMain.commands.put("start", new StartAdventure());
        ListenerMain.commands.put("intro", new Introduction());

    }
}
