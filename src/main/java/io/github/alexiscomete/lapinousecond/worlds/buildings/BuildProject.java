package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.save.SaveLocation;

public class BuildProject extends Building {

    public BuildProject(long id) {
        super(id);
    }

    public BuildProject(String type) {
        super(SaveLocation.generateUniqueID());
        createNew(type);
    }

    public void createNew(String type) {
        set("id", String.valueOf(id));
        set("type", type);
    }


}
