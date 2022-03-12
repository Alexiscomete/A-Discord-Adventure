package io.github.alexiscomete.lapinousecond;

import io.github.alexiscomete.lapinousecond.entity.Player;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.ArrayList;

/**
 * 
 */
public enum ItemsEnum {

    ROB((str) -> new Item("Rob ring", "Permet de voler 1% des RabbitCoins d'une personne qui a moins de 100% de différence", 1000, "ROB", str) {
        @Override
        public void setArgs(String[] args) {

        }

        @Override
        public boolean use(MessageCreateEvent event, String content, String[] args, Player ownerOfItem) {
            if (args.length < 3) {
                event.getMessage().reply("Vous devez indiquer la personne à voler");
                return false;
            }
            try {
                long l = Long.parseLong(args[2]);
                Player victim = Main.getSaveManager().players.get(l);
                if (victim == null) {
                    event.getMessage().reply("Ce joueur n'existe pas ou n'a pas de compte");
                    return false;
                }
                double diff = ownerOfItem.getBal() - victim.getBal();
                if (diff < ownerOfItem.getBal() * -1 || diff > ownerOfItem.getBal()) {
                    event.getMessage().reply("Cette personne a une trop grande différence d'argent avec vous !");
                    return false;
                }
                double rob = victim.getBal() / 100;
                victim.setBal(victim.getBal() - rob);
                ownerOfItem.setBal(ownerOfItem.getBal() + rob);
                return true;
            } catch (NumberFormatException e) {
                event.getMessage().reply("Il faut entrer un nombre à l'argument 2 ...");
                return false;
            }
        }

        @Override
        public String getArgs() {
            return "";
        }
    }, "ROB");

    public GetItem getItem;
    public String jname;

    ItemsEnum(GetItem getItem, String str) {
        this.getItem = getItem;
        this.jname = str;
    }

    @FunctionalInterface
    interface GetItem {
        Item getItem(String[] args);
    }

    public static ArrayList<Item> toItems(String str) {
        ArrayList<Item> items = new ArrayList<>();
        String[] strings = str.split(";");
        for (String s : strings) {
            String[] a = s.split(":");
            String[] args = new String[a.length-1];
            System.arraycopy(a, 1, args, 0, a.length - 1);
            for (ItemsEnum itemsEnum : values()) {
                if (itemsEnum.jname.equals(a[0])) {
                    items.add(itemsEnum.getItem.getItem(args));
                }
            }
        }
        return items;
    }
}
