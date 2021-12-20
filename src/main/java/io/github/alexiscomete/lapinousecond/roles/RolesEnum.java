package io.github.alexiscomete.lapinousecond.roles;

import java.util.function.Supplier;

public enum RolesEnum {
    SERVER_OWNER(() -> new DefaultRole(100, 10000000, "Propriétaire de serveur", "Le rôle pour toute personne créant un serveur (il doit y avoir le bot dessus)")),
    PROJECT_ADMIN(() -> new DefaultRole(100, 10000000, "Administrateur de projet", "Uniquement pour ceux qui sont admin sur un serveur de projet")),
    GOUV(() -> new DefaultRole(200, 1000000, "Membre de l'assemblée", "Les membres de l'assemblée de la RPDB peuvent demander à avoir ce rôle")),
    REPRESENTANT(() -> new DefaultRole(200, 10000000, "Représentant de région / département", "Chaque région peut définir ses conditions")),
    VERIFIE(() -> new DefaultRole(10, 10000000, "Membre vérifié", "Vérifié par le bot de l'ORU")),
    MODO(() -> new DefaultRole(100, 10000000, "Modérateur", "Pour toute personne ayant un rôle intermédiaire sur un serveur")),
    PARTICIPANT(() -> new DefaultRole(100, 10000000, "Participant projet", "Pour toute personne participant à un projet (un peu plus qu'avec sa simple précense sur le serveur). A chaque projet de décider"));

    private final Supplier<Role> supplier;

    public Role getInstance() {
        return supplier.get();
    }

    RolesEnum(Supplier<Role> supplier) {
        this.supplier = supplier;
    }
}
