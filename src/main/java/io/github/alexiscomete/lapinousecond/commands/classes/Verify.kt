package io.github.alexiscomete.lapinousecond.commands.classes;

import io.github.alexiscomete.lapinousecond.entity.Player;
import io.github.alexiscomete.lapinousecond.commands.CommandBot;
import org.javacord.api.event.message.MessageCreateEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class Verify extends CommandBot {

    public Verify() {
        super("Permet de vérifier votre compte", "verify", "Permet de vérifier votre compte grâce au bot de l'ORU");
    }

    @Override
    public void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {
        if (saveManager.players.get(messageCreateEvent.getMessageAuthor().getId()) != null) {
            messageCreateEvent.getMessage().reply("Votre vérification est en cours");
            UserData userData = getUserData(messageCreateEvent.getMessageAuthor().getId());
            if (userData.hasAccount()) {
                Player player = saveManager.players.get(messageCreateEvent.getMessageAuthor().getId());
                player.setX(userData.getX());
                player.setY(userData.getY());
                player.setHasAccount(userData.hasAccount());
                player.setVerify(userData.isVerify());

                if (userData.isVerify()) {
                    messageCreateEvent.getMessage().reply("Votre compte a été associé à votre pixel. Vous avez la vérification");
                } else {
                    messageCreateEvent.getMessage().reply("Votre compte a été associé à votre pixel. Vous n'avez malheuresement pas la vérification 😕");
                }
            } else {
                messageCreateEvent.getMessage().reply("Vous n'avez pas encore de compte avec l'ORU");
            }
        } else {
            messageCreateEvent.getMessage().reply("Utilisez -start");
        }
    }

    public static String getUser(long id) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://dirtybiology.captaincommand.repl.co/api/?authorization=mXpn9frxWJh0RPjZYSPMilfnK5ooxjhL&request=getInfosByDiscordId&datas=%7B%22discordId%22:%22" + id + "%22%7D").openConnection();
            connection.setRequestMethod("GET");
            String response = "";
            Scanner scanner = new Scanner(connection.getInputStream());
            if (scanner.hasNextLine()) {
                response += scanner.nextLine();
            }
            scanner.close();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UserData getUserData(long id) {
        String userData = Verify.getUser(id);
        System.out.println("Data :");
        System.out.println(userData);
        if (userData != null) {
            JSONObject jsonObject = new JSONObject(userData);
            JSONObject back = jsonObject.getJSONObject("back");
            if (!back.isEmpty()) {
                JSONObject member = back.getJSONObject("member");
                boolean verified = member.getBoolean("verified");
                if (verified) {
                    JSONArray jsonArray = member.getJSONArray("coordinatesVerified");
                    return new UserData(jsonArray.getInt(0), jsonArray.getInt(1), true, true);
                } else {
                    JSONArray jsonArray = member.getJSONArray("coordinatesUnverified");
                    return new UserData(jsonArray.getInt(0), jsonArray.getInt(1), false, true);
                }
            }
        }
        return new UserData(-1, -1, false, false);
    }

    public static class UserData {
        private final int x, y;
        private final boolean isVerify, hasAccount;


        public UserData(int x, int y, boolean isVerify, boolean hasAccount) {
            this.x = x;
            this.y = y;
            this.isVerify = isVerify;
            this.hasAccount = hasAccount;
        }

        public boolean isVerify() {
            return isVerify;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean hasAccount() {
            return hasAccount;
        }
    }
}
