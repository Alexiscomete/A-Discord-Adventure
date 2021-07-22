package io.github.alexiscomete.lapinousecond;

import org.javacord.api.event.message.MessageCreateEvent;

public enum ItemsEnum {

    ROB(() -> new Item(new UseItem() {
        @Override
        public boolean use(MessageCreateEvent event, String content, String[] args, Player ownerOfItem) {

            return false;
        }
    }, "Rob ring", "Permet de voler 1% de l'argent d'une personne qui a moins de 100% d'argent de différence", 1000));

    public GetItem getItem;

    ItemsEnum(GetItem getItem) {

    }

    @FunctionalInterface
    interface GetItem {
        Item getItem();
    }
}
