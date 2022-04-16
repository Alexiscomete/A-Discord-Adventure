package io.github.alexiscomete.lapinousecond.worlds.map.pathfindingastar;

import io.github.alexiscomete.lapinousecond.worlds.map.Pixel;

public interface Scorer {
    double computeCost(Pixel from, Pixel to);
}
