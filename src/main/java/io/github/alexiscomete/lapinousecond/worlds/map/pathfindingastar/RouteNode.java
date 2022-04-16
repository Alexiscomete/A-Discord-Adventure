package io.github.alexiscomete.lapinousecond.worlds.map.pathfindingastar;

import io.github.alexiscomete.lapinousecond.worlds.map.Pixel;

import java.util.StringJoiner;

class RouteNode implements Comparable<RouteNode> {
    private final Pixel current;
    private Pixel previous;
    private double routeScore;
    private double estimatedScore;

    RouteNode(Pixel current) {
        this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    RouteNode(Pixel current, Pixel previous, double routeScore, double estimatedScore) {
        this.current = current;
        this.previous = previous;
        this.routeScore = routeScore;
        this.estimatedScore = estimatedScore;
    }

    Pixel getCurrent() {
        return current;
    }

    Pixel getPrevious() {
        return previous;
    }

    double getRouteScore() {
        return routeScore;
    }

    double getEstimatedScore() {
        return estimatedScore;
    }

    void setPrevious(Pixel previous) {
        this.previous = previous;
    }

    void setRouteScore(double routeScore) {
        this.routeScore = routeScore;
    }

    void setEstimatedScore(double estimatedScore) {
        this.estimatedScore = estimatedScore;
    }

    @Override
    public int compareTo(RouteNode other) {
        if (this.estimatedScore > other.estimatedScore) {
            return 1;
        } else if (this.estimatedScore < other.estimatedScore) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", RouteNode.class.getSimpleName() + "[", "]").add("current=" + current)
                .add("previous=" + previous).add("routeScore=" + routeScore).add("estimatedScore=" + estimatedScore)
                .toString();
    }
}