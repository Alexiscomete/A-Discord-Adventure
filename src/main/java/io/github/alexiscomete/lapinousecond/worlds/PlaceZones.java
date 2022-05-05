package io.github.alexiscomete.lapinousecond.worlds;

import java.util.ArrayList;

public class PlaceZones extends Place {
    ArrayList<Zone> zones = new ArrayList<>();

    public PlaceZones() {
        super();
    }

    public PlaceZones(long id) {
        super(id);
        String zonesBDD = getString("zones");
        if (zonesBDD != null && !zonesBDD.equals("")) {
            String[] zonesTab = zonesBDD.split(";");
            for (String zone : zonesTab) {
                if (!zone.equals("")) {
                    zones.add(Zone.fromString(zone));
                }
            }
        }
    }
}
