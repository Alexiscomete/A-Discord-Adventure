package io.github.alexiscomete.lapinousecond.resources;

import java.util.Collection;
import java.util.HashMap;

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

    public static HashMap<Resource, ResourceManager> stringToArray(String str) {
        HashMap<Resource, ResourceManager> resourceManagers = new HashMap<>();
        if (str == null || str.equals("")) {
            return resourceManagers;
        }
        String[] strings = str.split(" ");
        for (String s :
                strings) {
            String[] elements = s.split(":");
            if (elements.length > 1) {
                resourceManagers.put(Resource.valueOf(elements[0]) , new ResourceManager(Resource.valueOf(elements[0]), Integer.parseInt(elements[1])));
            }
        }
        return resourceManagers;
    }

    public static String toString(Collection<ResourceManager> resourceManagers) {
        StringBuilder stringBuilder = new StringBuilder();
        for (ResourceManager re :
                resourceManagers) {
            stringBuilder.append(re.toString()).append(" ");
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return resource.getProgName() + ":" + quantity;
    }
}
