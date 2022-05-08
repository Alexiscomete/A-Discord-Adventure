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

    public void addZone(Zone zone) {
        zones.add(zone);
        updateBDD();
    }

    public String toString() {
        StringBuilder zonesString = new StringBuilder();
        for (Zone zone : zones) {
            zonesString.append(zone.toString()).append(";");
        }
        return zonesString.toString();
    }

    public ArrayList<Zone> getZones() {
        return zones;
    }

    public void updateBDD() {
        set("zones", toString());
    }

    public boolean isInZones(int x, int y) {
        for (Zone zone : zones) {
            if (zone.contains(x, y)) {
                return true;
            }
        }
        return false;
    }
}
