package io.github.alexiscomete.lapinousecond.worlds.map.pathfindingastar;

import io.github.alexiscomete.lapinousecond.worlds.map.Pixel;

import java.util.*;

public class RouteFinder {
    private final Graph graph;
    private final Scorer nextNodeScorer;
    private final Scorer targetScorer;

    public RouteFinder(Graph graph, Scorer nextNodeScorer, Scorer targetScorer) {
        this.graph = graph;
        this.nextNodeScorer = nextNodeScorer;
        this.targetScorer = targetScorer;
    }

    public List<Pixel> findRoute(Pixel from, Pixel to) {
        Map<Pixel, RouteNode> allNodes = new HashMap<>();
        Queue<RouteNode> openSet = new PriorityQueue<>();

        RouteNode start = new RouteNode(from, null, 0d, targetScorer.computeCost(from, to));
        allNodes.put(from, start);
        openSet.add(start);

        while (!openSet.isEmpty()) {
            RouteNode next = openSet.poll();
            if (next.getCurrent().equals(to)) {

                List<Pixel> route = new ArrayList<>();
                RouteNode current = next;
                do {
                    route.add(0, current.getCurrent());
                    current = allNodes.get(current.getPrevious());
                } while (current != null);

                return route;
            }

            graph.getConnections(next.getCurrent()).forEach(connection -> {
                double newScore = next.getRouteScore() + nextNodeScorer.computeCost(next.getCurrent(), connection);
                RouteNode nextNode = allNodes.getOrDefault(connection, new RouteNode<>(connection));
                allNodes.put(connection, nextNode);

                if (nextNode.getRouteScore() > newScore) {
                    nextNode.setPrevious(next.getCurrent());
                    nextNode.setRouteScore(newScore);
                    nextNode.setEstimatedScore(newScore + targetScorer.computeCost(connection, to));
                    openSet.add(nextNode);
                }
            });
        }

        return null;
    }

}
