package io.github.alexiscomete.lapinousecond.worlds.map;

public class RouteNode implements Comparable<RouteNode> {
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

    @Override
    public int compareTo(RouteNode o) {
        if (this.estimatedScore > o.estimatedScore) {
            return 1;
        } else if (this.estimatedScore < o.estimatedScore) {
            return -1;
        } else {
            return 0;
        }
    }
}
