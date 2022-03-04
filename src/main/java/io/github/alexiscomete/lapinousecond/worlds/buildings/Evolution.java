package io.github.alexiscomete.lapinousecond.worlds.buildings;

public class Evolution {
    private final Buildings evolutionTarget;
    private final double cost;

    public Evolution(Buildings evolutionTarget, double cost) {
        this.evolutionTarget = evolutionTarget;
        this.cost = cost;
    }

    public Buildings getEvolutionTarget() {
        return evolutionTarget;
    }

    public double getCost() {
        return cost;
    }
}
