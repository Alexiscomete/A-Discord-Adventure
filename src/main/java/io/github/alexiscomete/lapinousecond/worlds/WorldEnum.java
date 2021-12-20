package io.github.alexiscomete.lapinousecond.worlds;

public enum WorldEnum {

    DEFAULT(new WorldAbstract(30, "Serveur sans type", "Monde du chaos", "DEFAULT", "Ces serveurs sont ici dans l'attente d'être classé ou si ils sont inclassables.")),
    PROJECT(new WorldAbstract(30, "Serveur de projet", "Monde des objectifs", "PROJECT", "Cette catégorie est la plus vaste car n'importe qui peut très facilement créer un projet. Journaux, archives, wiki, Dibi, ....")),
    GOUV(new WorldAbstract(50, "Serveur d'organisation gouvernemental", "Monde des décisions", "GOUV", "Ici vous trouverez des serveurs officiels comme l'ORU, la RPDB ou l'AASR (si ils ajoutent la bot)")),
    REG(new WorldWithCoos(20, "Serveur de région ou de département", "Monde du drapeau", "REG", "Dans ce monde vous trouverez les serveurs des régions et des départements, avec leurs différentes villes.")),
    POL(new WorldAbstract(200, "Serveur politique", "Monde des idées", "POL", "Les défférents partis politiques **de la RPBD** ont une catégorie à part, ce ne sont pas des projets. Il est important de rappeler que le bot ne prend aucun parti."));

    private final World world;

    public World getWorld() {
        return world;
    }

    WorldEnum(World world) {
        this.world = world;
    }
}
