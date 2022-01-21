package io.github.alexiscomete.lapinousecond.worlds;

public enum WorldEnum {

    NORMAL(new WorldAbstract(30, "Serveur normal", "Monde du chaos", "NORMAL", "Ce monde regroupe tous les serveurs discord sans territoire et qui ne rentrent dans aucune catégorie")),
    DIBIMAP(new WorldWithCoos(20, "Server de territoire", "Monde du drapeau", "DIBIMAP", "Le serveur est un département (avec villes) ? Une région (avec départements ?) ? Une ville ? Ou tout autre chose avec un territoire ? Alors vous le trouverez ici."));

    private final World world;

    public World getWorld() {
        return world;
    }

    WorldEnum(World world) {
        this.world = world;
    }
}
