package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.save.SaveLocation;

public class BuildProject extends Building {

    public BuildProject(long id) {
        super(id);
    }

    public BuildProject(Buildings buildings) {
        super(SaveLocation.generateUniqueID());
        createNew(buildings);
    }

    public void createNew(Buildings buildings) {
        set("id", String.valueOf(id));
        set("type", buildings.getType());
    }


}
