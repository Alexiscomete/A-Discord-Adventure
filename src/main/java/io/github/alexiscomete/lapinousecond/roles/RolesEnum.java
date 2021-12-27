package io.github.alexiscomete.lapinousecond.roles;

import java.util.ArrayList;
import java.util.function.Function;

public enum RolesEnum {
    SERVER_OWNER((id) -> new DefaultRole(100, 10000000, "Propriétaire de serveur", "Le rôle pour toute personne créant un serveur (il doit y avoir le bot dessus)", id, "SERVER_OWNER")),
    PROJECT_ADMIN((id) -> new DefaultRole(100, 10000000, "Administrateur de projet", "Uniquement pour ceux qui sont admin sur un serveur de projet", id, "PROJECT_ADMIN")),
    GOUV((id) -> new DefaultRole(200, 1000000, "Membre de l'assemblée", "Les membres de l'assemblée de la RPDB peuvent demander à avoir ce rôle", id, "GOUV")),
    REPRESENTANT((id) -> new DefaultRole(200, 10000000, "Représentant de région / département", "Chaque région peut définir ses conditions", id, "REPRESENTANT")),
    VERIFIE((id) -> new DefaultRole(10, 10000000, "Membre vérifié", "Vérifié par le bot de l'ORU", id, "VERIFIE")),
    MODO((id) -> new DefaultRole(100, 10000000, "Modérateur", "Pour toute personne ayant un rôle intermédiaire sur un serveur", id, "MODO")),
    PARTICIPANT((id) -> new DefaultRole(100, 10000000, "Participant projet", "Pour toute personne participant à un projet (un peu plus qu'avec sa simple précense sur le serveur). A chaque projet de décider", id, "PARTICIPANT"));

    private final Function<Long, Role> supplier;

    public Role getInstance(Long serverID) {
        return supplier.apply(serverID);
    }

    RolesEnum(Function<Long, Role> supplier) {
        this.supplier = supplier;
    }

    public static ArrayList<Role> getRoles(String rolesString) {
        String[] strings = rolesString.split(";");
        ArrayList<Role> roles = new ArrayList<>();
        for (String str : strings) {
            String[] serverAndRole = str.split(":");
            roles.add(valueOf(serverAndRole[1]).getInstance(Long.parseLong(serverAndRole[0])));
        }
        return roles;
    }

    public static String rolesToString(ArrayList<Role> arrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arrayList.size(); i++) {
            stringBuilder.append(arrayList.get(i).getServerID()).append(":").append(arrayList.get(i).getProgName());
            if (i != arrayList.size()-1) {
                stringBuilder.append(";");
            }
        }
        return stringBuilder.toString();
    }
}
