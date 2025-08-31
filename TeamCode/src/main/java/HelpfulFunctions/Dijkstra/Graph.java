package HelpfulFunctions.Dijkstra;

import java.util.HashSet;
import java.util.Set;

public class Graph {
    private Set<Node> nodes = new HashSet();

    public Graph() {
    }

    public void addNode(Node nodeA) {
        this.nodes.add(nodeA);
    }

    public Set<Node> getNodes() {
        return this.nodes;
    }

    public void setNodes(Set<Node> nodes) {
        this.nodes = nodes;
    }
}
