package io.github.alexiscomete.lapinousecond.worlds;

public enum WorldEnum {

    DEFAULT("Serveur sans type", "Monde du chaos"),
    PROJECT("Serveur de projet", "Monde des objectifs"),
    GOUV("Serveur d'organisation gouvernemental", "Monde des décisions"),
    REG("Serveur de région ou de département", "Monde du drapeau"),
    POL("Serveur politique", "Monde des idées");

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameRP() {
        return nameRP;
    }

    public void setNameRP(String nameRP) {
        this.nameRP = nameRP;
    }

    private String name, nameRP;

    WorldEnum(String name, String nameRP) {
        this.name = name;
        this.nameRP = nameRP;
    }
}
