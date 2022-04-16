package io.github.alexiscomete.lapinousecond.worlds.map.pathfindingastar;

public interface Scorer<T extends Pixel> {
    double computeCost(T from, T to);
}
