package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public class Item {

    public UseItem use;
    public String name, description, jname;
    public int price;

    Item(UseItem use, String name, String description, int price, String jname) {
        this.use = use;
        this.name = name;
        this.description = description;
        this.price = price;
        this.jname = jname;
    }

    public boolean use(MessageCreateEvent event, String content, String[] args, Player ownerOfItem) {
        return use.use(event, content, args, ownerOfItem);
    }

}
