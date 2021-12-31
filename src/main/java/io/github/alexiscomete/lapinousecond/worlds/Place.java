package io.github.alexiscomete.lapinousecond.worlds;

import java.util.Optional;

public class Place {
    private Long serverID;
    private ServerBot serverBot;


    public Place(ServerBot serverBot) {
        this.serverBot = serverBot;
        if (serverBot == null) {
            this.serverID = null;
        } else {
            this.serverID = serverBot.getId();
        }
    }

    public Optional<ServerBot> getServerBot() {
        return Optional.ofNullable(serverBot);
    }

    public Optional<Long> getServerID() {
        return Optional.ofNullable(serverID);
    }

    public void setServerBot(ServerBot serverBot) {
        this.serverBot = serverBot;
    }

    public void setServerID(Long serverID) {
        this.serverID = serverID;
    }
}
