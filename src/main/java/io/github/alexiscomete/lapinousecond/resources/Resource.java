package io.github.alexiscomete.lapinousecond.resources;

public enum Resource {

    ;

    private final String name, description, progName;
    private int price;

    public void setPrice(int price) {
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

    public int getPrice() {
        return price;
    }

    Resource(String name, String description, String progName, int price) {
        this.name = name;
        this.description = description;
        this.progName = progName;
        this.price = price;
    }
}
