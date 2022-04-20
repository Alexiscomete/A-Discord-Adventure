package io.github.alexiscomete.lapinousecond.roles_update;

import io.github.alexiscomete.lapinousecond.entity.Player;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.util.ArrayList;
import java.util.List;

public enum RolesEnum {

    ADMIN("admin", "Administrateur du serveur Discord", new String[]{"admin", "orgna", "orga"}, 80, /* 5 heures */ 43_200), // pas besoin de mettre le nom complet du role car le contain ignore les espaces
    MODO("modo", "Modérateur du serveur Discord", new String[]{"modo", "modé"}, 60, 43_200),
    MEMBER("member", "Membre du serveur Discord", new String[]{"memb"}, 10, /* 1h */3_600),
    PARTICIPANT("participant", "Participant du serveur Discord", new String[]{"part", "commu"}, 50, /* 3 heures */ 10_800),
    VISITOR("visitor", "Visiteur du serveur Discord", new String[]{"visit"}, 5, /* 1h */3_600),
    CITOYEN("citoyen", "Citoyen du Dibistan / de la région / du département", new String[]{"citoy"}, 10, /* 3 heures */ 10_800),
    AMBASSADOR("ambassadeur", "Ambassadeur", new String[]{"ambassad"}, 10, /* 3 heures */ 10_800),
    DELEGATE("delegate", "Représentant", new String[]{"delag", "repr"}, 10, /* 3 heures */ 10_800);

    public final String name;
    public final String description;
    // les alias
    public final String[] aliases;
    public final int salary;
    public final int coolDownSize; // en secondes

    RolesEnum(String name, String description, String[] aliases, int salary, int coolDownSize) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
        this.salary = salary;
        this.coolDownSize = coolDownSize;
    }

    public static boolean check(String[] strings, ArrayList<RolesEnum> roles, long serverID) {
        for (String string : strings) {
            boolean perm;
            switch (string) {
                case "MANAGE_ADMIN":
                    perm = false;
                    for (RolesEnum role : roles) {
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
                    for (RolesEnum role : roles) {
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

    public boolean check(User user, Server server) {
        List<Role> roles = user.getRoles(server);
        for (String alias : aliases) {
            for (Role role : roles) {
                if (role.getName().toLowerCase().contains(alias)) {
                    return true;
                }
            }
        }
        return false;
    }
}
