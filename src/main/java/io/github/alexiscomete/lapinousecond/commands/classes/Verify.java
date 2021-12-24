package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.event.message.MessageCreateEvent;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Scanner;

public class Verify extends CommandBot {

    public Verify(String description, String name, String totalDescription, String... perms) {
        super(description, name, totalDescription, perms);
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {

    }

    public static String getUserData(long id) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://dirtybiology.captaincommand.repl.co/api/?authorization=mXpn9frxWJh0RPjZYSPMilfnK5ooxjhL&request=getInfosByDiscordId&datas=%7B%22discordId%22:%22" + id + "%22%7D").openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                String response = "";
                Scanner scanner = new Scanner(connection.getInputStream());
                if (scanner.hasNextLine()) {
                    response += scanner.nextLine();
                }
                scanner.close();
                return response;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
