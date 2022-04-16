package io.github.alexiscomete.lapinousecond.worlds.map.pathfindingastar;

import io.github.alexiscomete.lapinousecond.worlds.map.Pixel;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class Graph {
    private final Set<Pixel> nodes;

    private final Map<String, Set<String>> connections;

    public Graph(Set<Pixel> nodes, Map<String, Set<String>> connections) {
        this.nodes = nodes;
        this.connections = connections;
    }

    public Pixel getNode(String id) {
        return nodes.stream()
                .filter(node -> node.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No node found with ID"));
    }

    public Set<Pixel> getConnections(Pixel node) {
        return connections.get(node.getId()).stream()
                .map(this::getNode)
                .collect(Collectors.toSet());
    }
}
