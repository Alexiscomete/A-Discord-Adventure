package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class SetServerSec extends CommandBot {

    public SetServerSec() {
        super("Set Server Security", "sss", "sss [sec]");
    }

    @Override
    void execute(MessageCreateEvent messageCreateEvent, String content, String[] args) {

    }
}
