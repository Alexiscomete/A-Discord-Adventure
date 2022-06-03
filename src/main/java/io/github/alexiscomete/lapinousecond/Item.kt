package io.github.alexiscomete.lapinousecond;

import io.github.alexiscomete.lapinousecond.entity.Player;
import org.javacord.api.event.message.MessageCreateEvent;

public abstract class Item {

    public String name, description, jname;
    public int price;

    Item(String name, String description, int price, String jname, String[] args) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.jname = jname;
        setArgs(args);
    }

    public abstract void setArgs(String[] args);

    public abstract boolean use(MessageCreateEvent event, String content, String[] args, Player ownerOfItem);

    public abstract String getArgs();
}
