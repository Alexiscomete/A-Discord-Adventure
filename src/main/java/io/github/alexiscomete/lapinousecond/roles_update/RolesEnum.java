package io.github.alexiscomete.lapinousecond.roles_update;

import java.util.ArrayList;

public enum RolesEnum {

    ADMIN("admin", "Administrateur du serveur Discord", new String[]{"admin", "orgna", "orga"}), // pas besoin de mettre le nom complet du role car le contain ignore les espaces
    MODO("modo", "Modérateur du serveur Discord", new String[]{"modo", "modé"}),
    MEMBER("member", "Membre du serveur Discord", new String[]{"memb"}),
    PARTICIPANT("participant", "Participant du serveur Discord", new String[]{"part", "commu"}),
    VISITOR("visitor", "Visiteur du serveur Discord", new String[]{"visit"}),
    CITOYEN("citoyen", "Citoyen du Dibistan / de la région / du département", new String[]{"citoy"});

    public final String name;
    public final String description;
    // les alias
    public final String[] aliases;

    RolesEnum(String name, String description, String[] aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    public static boolean check(String[] strings, ArrayList<Role> roles, long serverID) {
        for (String string : strings) {
            boolean perm;
            switch (string) {
                case "MANAGE_ADMIN":
                    perm = false;
                    for (Role role : roles) {
                        if (role.getServerID() == serverID && role.getProgName().equals("SERVER_OWNER")) {
                            perm = true;
                            break;
                        }
                    }
                    if (!perm) {
                        return false;
                    }
                    break;
                case "MANAGE_ROLES_SERVER":
                    perm = false;
                    for (Role role : roles) {
                        if (role.getServerID() == serverID && (role.getProgName().equals("SERVER_OWNER") || role.getProgName().equals("PROJECT_ADMIN") || role.getProgName().equals("SERVER_ADMIN"))) {
                            perm = true;
                            break;
                        }
                    }
                    if (!perm) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }
}
