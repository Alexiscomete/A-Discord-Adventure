package io.github.alexiscomete.lapinousecond.worlds.buildings;

import io.github.alexiscomete.lapinousecond.save.SaveLocation;

public class BuildProject extends Building {

    public BuildProject(long id) {
        super(id);
    }

    public BuildProject() {
        super(SaveLocation.generateUniqueID());
        createNew();
    }

    public void createNew() {
        set("id", String.valueOf(id));
    }


}
