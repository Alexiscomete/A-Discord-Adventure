package io.github.alexiscomete.lapinousecond.resources;

public enum Resource {

    WOOD("Bois", "C'est juste du bois, utile pendant tout le jeu", "WOOD", 0.25),
    STONE("Pierre", "Très utile", "STONE", 0.5),
    BRANCH("Branche", "Souvent utile pour la fabrication d'objets", "BRANCH", 0.25),
    DIAMOND("Diamant", "Rare", "DIAMOND", 5);

    private final String name, description, progName;
    private double price;

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getProgName() {
        return progName;
    }

    public double getPrice() {
        return price;
    }

    Resource(String name, String description, String progName, double price) {
        this.name = name;
        this.description = description;
        this.progName = progName;
        this.price = price;
    }
}
