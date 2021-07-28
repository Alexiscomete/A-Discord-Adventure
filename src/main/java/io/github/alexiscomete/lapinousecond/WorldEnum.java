package io.github.alexiscomete.lapinousecond;

public enum WorldEnum {

    DEFAULT("default", "Plateau éternel"),
    COMPUTERSCIENCE("computer science", "Cavernes de l'informatique"),
    VIDEOGAMES("video games", "Montagne des jeux vidéos");

    public String name, nameRP;

    WorldEnum(String name, String nameRP) {
        this.name = name;
        this.nameRP = nameRP;
    }
}
