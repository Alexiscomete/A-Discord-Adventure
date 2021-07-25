package io.github.alexiscomete.lapinousecond;

public enum ItemsEnum {

    ROB(() -> new Item((event, content, args, ownerOfItem) -> {
        if (args.length < 3) {
            event.getMessage().reply("Vous devez indiquer la personne à voler");
            return false;
        }
        try {
            long l = Long.parseLong(args[2]);
            Player victim = SaveManager.getPlayer(l);
            if (victim == null) {
                event.getMessage().reply("Ce joueur n'existe pas ou n'a pas de compte");
                return false;
            }
            long diff = ownerOfItem.getBal() - victim.getBal();
            if (diff < ownerOfItem.getBal() * -1 || diff > ownerOfItem.getBal()) {
                event.getMessage().reply("Cette personne a une trop grande différence d'argent avec vous !");
                return false;
            }
            long rob = victim.getBal() / 100;
            victim.setBal(victim.getBal() - rob);
            ownerOfItem.setBal(ownerOfItem.getBal() + rob);
            return true;
        } catch (NumberFormatException e) {
            event.getMessage().reply("Il faut entrer un nombre à l'argument 2 ...");
            return false;
        }
    }, "Rob ring", "Permet de voler 1% des RabbitCoins d'une personne qui a moins de 100% de différence", 1000, "ROB"));

    public GetItem getItem;

    ItemsEnum(GetItem getItem) {
        this.getItem = getItem;
    }

    @FunctionalInterface
    interface GetItem {
        Item getItem();
    }
}
