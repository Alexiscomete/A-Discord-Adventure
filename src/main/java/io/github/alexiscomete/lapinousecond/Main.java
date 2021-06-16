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

        ListenerMain.commands.put("help", new Help());
        ListenerMain.commands.put("hello", new Hello());
    }
}
