package model;

import common.ElementAttributes;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ModelGraph extends MultiGraph {

    private Map<String, Vertex> vertexes = new HashMap<>();

    private Map<String, InteriorNode> interiors = new HashMap<>();

    private Map<String, GraphEdge> edges = new HashMap<>();

    public ModelGraph(String id, boolean strictChecking, boolean autoCreate, int initialNodeCapacity, int initialEdgeCapacity) {
        super(id, strictChecking, autoCreate, initialNodeCapacity, initialEdgeCapacity);
    }

    public ModelGraph(String id, boolean strictChecking, boolean autoCreate) {
        super(id, strictChecking, autoCreate);
    }

    public ModelGraph(String id) {
        super(id);
    }

    public void insertVertex(Vertex vertex) {
        Node node = this.addNode(vertex.getId());
        node.setAttribute(ElementAttributes.FROZEN_LAYOUT);
        node.setAttribute(ElementAttributes.XYZ, vertex.getXCoordinate(), vertex.getYCoordinate(), vertex.getZCoordinate());
        vertexes.put(vertex.getId(), vertex);
    }

    public Vertex insertVertex(String id, VertexType vertexType, double x, double y, double z) {
        Vertex vertex = new Vertex.VertexBuilder(this, id)
                .setVertexType(vertexType)
                .setXCoordinate(x)
                .setYCoordinate(y)
                .setZCoordinate(z)
                .build();
        insertVertex(vertex);
        return vertex;
    }

    public Optional<Vertex> getVertex(String id) {
        return Optional.ofNullable(vertexes.get(id));
    }

    public Optional<Vertex> removeVertex(String id) {
        Vertex vertex = vertexes.remove(id);
        if (vertex != null) {
            this.removeVertex(id);
            interiors.entrySet().stream()
                    .filter(interior -> interior.getValue().getTriangleVertexes().contains(vertex))
                    .forEach(result -> removeInterior(result.getKey()));
            edges.values().stream()
                    .filter(graphEdge -> graphEdge.getEdgeNodes().contains(vertex))
                    .map(GraphEdge::getId)
                    .forEach(this::removeEdge);
            return Optional.of(vertex);
        }
        return Optional.empty();
    }

    public InteriorNode insertInterior(String id, Vertex v1, Vertex v2, Vertex v3) {
        InteriorNode interiorNode = new InteriorNode(this, id, v1, v2, v3);
        Node node = this.addNode(interiorNode.getId());
        node.setAttribute(ElementAttributes.FROZEN_LAYOUT);
        node.setAttribute(ElementAttributes.XYZ, interiorNode.getXCoordinate(), interiorNode.getYCoordinate(), interiorNode.getZCoordinate());
        interiors.put(id, interiorNode);
        insertEdge(id.concat(v1.getId()), interiorNode, v1);
        insertEdge(id.concat(v2.getId()), interiorNode, v2);
        insertEdge(id.concat(v3.getId()), interiorNode, v3);
        return interiorNode;
    }

    public Optional<InteriorNode> getInterior(String id) {
        return Optional.ofNullable(interiors.get(id));
    }

    public void removeInterior(String id) {
        edges.values().stream()
                .filter(graphEdge -> graphEdge.getEdgeNodes().contains(interiors.get(id)))
                .map(GraphEdge::getId)
                .forEach(this::removeEdge);
        interiors.remove(id);
        this.removeNode(id);
    }

    public GraphEdge insertEdge(String id, GraphNode n1, GraphNode n2) {
        GraphEdge graphEdge = new GraphEdge.GraphEdgeBuilder(id, n1, n2).build();
        this.addEdge(graphEdge.getId(), n1, n2);
        edges.put(graphEdge.getId(), graphEdge);
        return graphEdge;
    }

    public GraphEdge insertEdge(String id, GraphNode n1, GraphNode n2, boolean B, double L) {
        GraphEdge graphEdge = new GraphEdge.GraphEdgeBuilder(id, n1, n2)
                .setB(B)
                .setL(L)
                .build();
        this.addEdge(graphEdge.getId(), n1, n2);
        edges.put(graphEdge.getId(), graphEdge);
        return graphEdge;
    }
}