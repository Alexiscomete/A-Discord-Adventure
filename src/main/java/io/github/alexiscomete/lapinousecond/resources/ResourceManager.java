package io.github.alexiscomete.lapinousecond.resources;

public class ResourceManager {

    private final Resource resource;
    private int quantity;

    public ResourceManager(Resource resource, int quantity) {
        this.resource = resource;
        this.quantity = quantity;
    }

    public Resource getResource() {
        return resource;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
