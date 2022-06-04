package io.github.alexiscomete.lapinousecond.worlds;

public enum WorldEnum {

    NORMAL(new WorldAbstract(30, "Serveur normal", "Monde du chaos", "NORMAL", "Ce monde regroupe tous les serveurs discord sans territoire et qui ne rentrent dans aucune catégorie"), 100),
    DIBIMAP(new WorldWithCoos(20, "Server de territoire", "Monde du drapeau", "DIBIMAP", "Le serveur est un département (avec villes) ? Une région (avec départements ?) ? Une ville ? Ou tout autre chose avec un territoire ? Alors vous le trouverez ici."), 300);

    private final World world;
    private final int price;

    public World getWorld() {
        return world;
    }

    public int getPrice() {
        return price;
    }

    WorldEnum(World world, int price) {
        this.world = world;
        this.price = price;
    }
}
