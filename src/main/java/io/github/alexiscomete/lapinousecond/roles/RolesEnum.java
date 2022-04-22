package io.github.alexiscomete.lapinousecond.roles;

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

    public static boolean check(String[] strings, User user, Server server) {
        for (String string : strings) {
            switch (string) {
                case "MANAGE_ADMIN":
                    return server.getOwner().get().equals(user);
                case "MANAGE_ROLES_SERVER":
                    return ADMIN.check(user, server);
                default:
                    System.out.println("WARNING : " + string + " n'est pas une permission");
                    return true;
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

    public static ArrayList<RolesEnum> getRoles(User user, Server server) {
        ArrayList<RolesEnum> roles = new ArrayList<>();
        for (RolesEnum role : RolesEnum.values()) {
            role.check(user, server);
        }
        return roles;
    }
}
