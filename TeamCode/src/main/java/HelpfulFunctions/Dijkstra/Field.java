package HelpfulFunctions.Dijkstra;

//import com.qualcomm.robotcore.hardware.DcMotor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Field {
    public Graph fieldGraph;
    //    public String currentLocation;
    public Field(String startingLocation) {
        fieldGraph = new Graph();
        Node OneA = new Node("1-1");
        Node TwoA = new Node("2-1");
        Node ThreeA = new Node("3-1");
        Node FourA = new Node("4-1");
        Node FiveA = new Node("5-1");
        Node SixA = new Node("6-1");
        Node OneB = new Node("1-2");
        Node TwoB = new Node("2-2");
        Node ThreeB = new Node("3-2");
        Node FourB = new Node("4-2");
        Node FiveB = new Node("5-2");
        Node SixB = new Node("6-2");
        Node OneC = new Node("1-3");
        Node TwoC = new Node("2-3");
        Node FiveC = new Node("5-3");
        Node SixC = new Node("6-3");
        Node OneD = new Node("1-4");
        Node TwoD = new Node("2-4");
        Node FiveD = new Node("5-4");
        Node SixD = new Node("6-4");
        Node OneE = new Node("1-5");
        Node TwoE = new Node("2-5");
        Node FiveE = new Node("5-5");
        Node SixE = new Node("6-5");
        Node OneF = new Node("1-6");
        Node TwoF = new Node("2-6");
        Node ThreeF = new Node("3-6");
        Node FourF = new Node("4-6");
        Node FiveF = new Node("5-6");
        Node SixF = new Node("6-6");
        OneA.addDestination(OneB, 24);
        OneA.addDestination(TwoA, 24);
        OneB.addDestination(OneA, 24);
        OneB.addDestination(OneC, 24);
        OneB.addDestination(TwoB, 24);
        OneC.addDestination(OneB, 24);
        OneC.addDestination(OneD, 24);
        OneC.addDestination(TwoC, 24);
        OneD.addDestination(OneC, 24);
        OneD.addDestination(TwoD, 24);
        OneD.addDestination(OneE, 24);
        OneE.addDestination(OneD, 24);
        OneE.addDestination(TwoE, 24);
        OneE.addDestination(OneF, 24);
        OneF.addDestination(OneE, 24);
        OneF.addDestination(TwoF, 24);
        TwoA.addDestination(OneA, 24);
        TwoA.addDestination(TwoB, 24);
        TwoA.addDestination(ThreeA, 24);
        TwoB.addDestination(OneB, 24);
        TwoB.addDestination(TwoC, 24);
        TwoB.addDestination(ThreeB, 24);
        TwoB.addDestination(TwoA, 24);
        TwoC.addDestination(TwoB, 24);
        TwoC.addDestination(TwoD, 24);
        TwoC.addDestination(OneC, 24);
        TwoD.addDestination(TwoE, 24);
        TwoD.addDestination(TwoC, 24);
        TwoD.addDestination(OneD, 24);
        TwoE.addDestination(TwoF, 24);
        TwoF.addDestination(ThreeF, 24);
        TwoF.addDestination(OneF, 24);
        ThreeA.addDestination(ThreeB, 24);
        ThreeA.addDestination(FourA, 24);
        ThreeA.addDestination(TwoA, 24);
        ThreeB.addDestination(FourB, 24);
        ThreeB.addDestination(TwoB, 24);
        ThreeB.addDestination(ThreeA, 24);
        ThreeF.addDestination(FourF, 24);
        ThreeF.addDestination(TwoF, 24);
        FourA.addDestination(FourB, 24);
        FourA.addDestination(FiveA, 24);
        FourA.addDestination(ThreeA, 24);
        FourB.addDestination(FiveB, 24);
        FourB.addDestination(ThreeB, 24);
        FourB.addDestination(FourA, 24);
        FourF.addDestination(FiveF, 24);
        FourF.addDestination(ThreeF, 24);
        FiveA.addDestination(FiveB, 24);
        FiveA.addDestination(SixA, 24);
        FiveA.addDestination(FourA, 24);
        FiveB.addDestination(FiveC, 24);
        FiveB.addDestination(SixB, 24);
        FiveB.addDestination(FourB, 24);
        FiveB.addDestination(FiveA, 24);
        FiveC.addDestination(FiveD, 24);
        FiveC.addDestination(SixC, 24);
        FiveC.addDestination(FiveB, 24);
        FiveD.addDestination(FiveE, 24);
        FiveD.addDestination(SixD, 24);
        FiveD.addDestination(FiveC, 24);
        FiveE.addDestination(FiveD, 24);
        FiveE.addDestination(FiveF, 24);
        FiveE.addDestination(SixE, 24);
        FiveF.addDestination(SixF, 24);
        FiveF.addDestination(FourF, 24);
        FiveF.addDestination(FiveE, 24);
        SixA.addDestination(FiveA, 24);
        SixA.addDestination(SixB, 24);
        SixB.addDestination(FiveB, 24);
        SixB.addDestination(SixA, 24);
        SixB.addDestination(SixC, 24);
        SixC.addDestination(FiveC, 24);
        SixC.addDestination(SixB, 24);
        SixC.addDestination(SixD, 24);
        SixD.addDestination(FiveD, 24);
        SixD.addDestination(SixC, 24);
        SixD.addDestination(SixE, 24);
        SixE.addDestination(FiveE, 24);
        SixE.addDestination(SixD, 24);
        SixE.addDestination(SixF, 24);
        SixF.addDestination(FiveF, 24);
        SixF.addDestination(SixE, 24);
        fieldGraph = new Graph();
        fieldGraph.addNode(OneA);
        fieldGraph.addNode(TwoA);
        fieldGraph.addNode(ThreeA);
        fieldGraph.addNode(FourA);
        fieldGraph.addNode(FiveA);
        fieldGraph.addNode(SixA);
        fieldGraph.addNode(OneB);
        fieldGraph.addNode(TwoB);
        fieldGraph.addNode(ThreeB);
        fieldGraph.addNode(FourB);
        fieldGraph.addNode(FiveB);
        fieldGraph.addNode(SixB);
        fieldGraph.addNode(OneC);
        fieldGraph.addNode(TwoC);
        fieldGraph.addNode(FiveC);
        fieldGraph.addNode(SixC);
        fieldGraph.addNode(OneD);
        fieldGraph.addNode(TwoD);
        fieldGraph.addNode(FiveD);
        fieldGraph.addNode(SixD);
        fieldGraph.addNode(OneE);
        fieldGraph.addNode(TwoE);
        fieldGraph.addNode(FiveE);
        fieldGraph.addNode(SixE);
        fieldGraph.addNode(OneF);
        fieldGraph.addNode(TwoF);
        fieldGraph.addNode(ThreeF);
        fieldGraph.addNode(FourF);
        fieldGraph.addNode(FiveF);
        fieldGraph.addNode(SixF);
    }

    public Graph createNewGraph () {
        Graph newGraph = new Graph();
        Node OneA = new Node("1-1");
        Node TwoA = new Node("2-1");
        Node ThreeA = new Node("3-1");
        Node FourA = new Node("4-1");
        Node FiveA = new Node("5-1");
        Node SixA = new Node("6-1");
        Node OneB = new Node("1-2");
        Node TwoB = new Node("2-2");
        Node ThreeB = new Node("3-2");
        Node FourB = new Node("4-2");
        Node FiveB = new Node("5-2");
        Node SixB = new Node("6-2");
        Node OneC = new Node("1-3");
        Node TwoC = new Node("2-3");
        Node FiveC = new Node("5-3");
        Node SixC = new Node("6-3");
        Node OneD = new Node("1-4");
        Node TwoD = new Node("2-4");
        Node FiveD = new Node("5-4");
        Node SixD = new Node("6-4");
        Node OneE = new Node("1-5");
        Node TwoE = new Node("2-5");
        Node FiveE = new Node("5-5");
        Node SixE = new Node("6-5");
        Node OneF = new Node("1-6");
        Node TwoF = new Node("2-6");
        Node ThreeF = new Node("3-6");
        Node FourF = new Node("4-6");
        Node FiveF = new Node("5-6");
        Node SixF = new Node("6-6");
        OneA.addDestination(OneB, 24);
        OneA.addDestination(TwoA, 24);
        OneB.addDestination(OneA, 24);
        OneB.addDestination(OneC, 24);
        OneB.addDestination(TwoB, 24);
        OneC.addDestination(OneB, 24);
        OneC.addDestination(OneD, 24);
        OneC.addDestination(TwoC, 24);
        OneD.addDestination(OneC, 24);
        OneD.addDestination(TwoD, 24);
        OneD.addDestination(OneE, 24);
        OneE.addDestination(OneD, 24);
        OneE.addDestination(TwoE, 24);
        OneE.addDestination(OneF, 24);
        OneF.addDestination(OneE, 24);
        OneF.addDestination(TwoF, 24);
        TwoA.addDestination(OneA, 24);
        TwoA.addDestination(TwoB, 24);
        TwoA.addDestination(ThreeA, 24);
        TwoB.addDestination(OneB, 24);
        TwoB.addDestination(TwoC, 24);
        TwoB.addDestination(ThreeB, 24);
        TwoB.addDestination(TwoA, 24);
        TwoC.addDestination(TwoB, 24);
        TwoC.addDestination(TwoD, 24);
        TwoC.addDestination(OneC, 24);
        TwoD.addDestination(TwoE, 24);
        TwoD.addDestination(TwoC, 24);
        TwoD.addDestination(OneD, 24);
        TwoE.addDestination(TwoF, 24);
        TwoF.addDestination(ThreeF, 24);
        TwoF.addDestination(OneF, 24);
        ThreeA.addDestination(ThreeB, 24);
        ThreeA.addDestination(FourA, 24);
        ThreeA.addDestination(TwoA, 24);
        ThreeB.addDestination(FourB, 24);
        ThreeB.addDestination(TwoB, 24);
        ThreeB.addDestination(ThreeA, 24);
        ThreeF.addDestination(FourF, 24);
        ThreeF.addDestination(TwoF, 24);
        FourA.addDestination(FourB, 24);
        FourA.addDestination(FiveA, 24);
        FourA.addDestination(ThreeA, 24);
        FourB.addDestination(FiveB, 24);
        FourB.addDestination(ThreeB, 24);
        FourB.addDestination(FourA, 24);
        FourF.addDestination(FiveF, 24);
        FourF.addDestination(ThreeF, 24);
        FiveA.addDestination(FiveB, 24);
        FiveA.addDestination(SixA, 24);
        FiveA.addDestination(FourA, 24);
        FiveB.addDestination(FiveC, 24);
        FiveB.addDestination(SixB, 24);
        FiveB.addDestination(FourB, 24);
        FiveB.addDestination(FiveA, 24);
        FiveC.addDestination(FiveD, 24);
        FiveC.addDestination(SixC, 24);
        FiveC.addDestination(FiveB, 24);
        FiveD.addDestination(FiveE, 24);
        FiveD.addDestination(SixD, 24);
        FiveD.addDestination(FiveC, 24);
        FiveE.addDestination(FiveD, 24);
        FiveE.addDestination(FiveF, 24);
        FiveE.addDestination(SixE, 24);
        FiveF.addDestination(SixF, 24);
        FiveF.addDestination(FourF, 24);
        FiveF.addDestination(FiveE, 24);
        SixA.addDestination(FiveA, 24);
        SixA.addDestination(SixB, 24);
        SixB.addDestination(FiveB, 24);
        SixB.addDestination(SixA, 24);
        SixB.addDestination(SixC, 24);
        SixC.addDestination(FiveC, 24);
        SixC.addDestination(SixB, 24);
        SixC.addDestination(SixD, 24);
        SixD.addDestination(FiveD, 24);
        SixD.addDestination(SixC, 24);
        SixD.addDestination(SixE, 24);
        SixE.addDestination(FiveE, 24);
        SixE.addDestination(SixD, 24);
        SixE.addDestination(SixF, 24);
        SixF.addDestination(FiveF, 24);
        SixF.addDestination(SixE, 24);
        newGraph = new Graph();
        newGraph.addNode(OneA);
        newGraph.addNode(TwoA);
        newGraph.addNode(ThreeA);
        newGraph.addNode(FourA);
        newGraph.addNode(FiveA);
        newGraph.addNode(SixA);
        newGraph.addNode(OneB);
        newGraph.addNode(TwoB);
        newGraph.addNode(ThreeB);
        newGraph.addNode(FourB);
        newGraph.addNode(FiveB);
        newGraph.addNode(SixB);
        newGraph.addNode(OneC);
        newGraph.addNode(TwoC);
        newGraph.addNode(FiveC);
        newGraph.addNode(SixC);
        newGraph.addNode(OneD);
        newGraph.addNode(TwoD);
        newGraph.addNode(FiveD);
        newGraph.addNode(SixD);
        newGraph.addNode(OneE);
        newGraph.addNode(TwoE);
        newGraph.addNode(FiveE);
        newGraph.addNode(SixE);
        newGraph.addNode(OneF);
        newGraph.addNode(TwoF);
        newGraph.addNode(ThreeF);
        newGraph.addNode(FourF);
        newGraph.addNode(FiveF);
        newGraph.addNode(SixF);

        return newGraph;
    }


    public Graph spfGraph(Node origin) {
        return Dijkstra.calculateShortestPathFromSource(fieldGraph, origin);
    }

    public List<List<String>> getInstructionsList(String origin, String target) {
        Node originNode = getNodeFromName(origin);
        Node targetNode = getNodeFromName(target);
        List<Node> path;

        path = getPathNodes(origin, target);

        List<List<String>> directions = new ArrayList<>();

        for(int i = 0; i < path.size() + 1; i++) {
            Node lastNode;
            List<String> currentDirections = new ArrayList();
            try {
                lastNode = path.get(i-1);
            }
            catch(Exception e) {
                lastNode = originNode;
            }


            Node currentNode;
            int distance;
            if(i == path.size()) {
                currentNode = targetNode;
                distance = 24;
            } else{
                currentNode = path.get(i);
                distance = currentNode.getDistance();
            }

            if(distance != 0) {
                if((int) currentNode.getName().charAt(2) < (int) lastNode.getName().charAt(2)) {
                    currentDirections.add("left");
                    currentDirections.add(Integer.toString(distance));
                    directions.add(currentDirections);
                }
                else if ((int) currentNode.getName().charAt(2) > (int) lastNode.getName().charAt(2)) {
                    currentDirections.add("right");
                    currentDirections.add(Integer.toString(distance));
                    directions.add(currentDirections);
                }
                else if ((int) currentNode.getName().charAt(0) < (int) lastNode.getName().charAt(0)) {
                    currentDirections.add("back");
                    currentDirections.add(Integer.toString(distance));
                    directions.add(currentDirections);
                }
                else if ((int) currentNode.getName().charAt(0) > (int) lastNode.getName().charAt(0)) {
                    currentDirections.add("forward");
                    currentDirections.add(Integer.toString(distance));
                    directions.add(currentDirections);
                }
            }
        }
        return directions;
    }

    public String getInstructionsString(String origin, String target) {
        Node originNode = getNodeFromName(origin);
        Node targetNode = getNodeFromName(target);
        List<Node> path = getPathNodes(origin, target);
        List<String> directions = new ArrayList<>();

        for(int i = 0; i < path.size() + 1; i++) {
            Node lastNode;
            try {
                lastNode = path.get(i-1);
            }
            catch(Exception e) {
                lastNode = originNode;
            }

            Node currentNode;
            int distance;
            if(i == path.size()) {
                currentNode = targetNode;
                distance = 24;
            } else{
                currentNode = path.get(i);
                distance = currentNode.getDistance();
            }

            if((int) currentNode.getName().charAt(2) < (int) lastNode.getName().charAt(2)) {
                directions.add("Left: " + distance);
            }
            else if ((int) currentNode.getName().charAt(2) > (int) lastNode.getName().charAt(2)) {
                directions.add("Right: " + distance);
            }
            else if ((int) currentNode.getName().charAt(0) < (int) lastNode.getName().charAt(0)) {
                directions.add("Back: " + distance);
            }
            else if ((int) currentNode.getName().charAt(0) > (int) lastNode.getName().charAt(0)) {
                directions.add("Forward: " + distance);
            }
        }


        return directions.toString();
    }

    public Integer getPathDistance(String origin, String target) {
        Node originNode = getNodeFromName(origin);
        Node targetNode = getNodeFromName(target);

        Graph graph = spfGraph(originNode);
        for(Node node : graph.getNodes()) {
            if (node == targetNode) {
                return node.getDistance();
            }
        }

        return -1;
    }

    public List<Node> getPathNodes(String origin, String target) {
        Node originNode = getNodeFromName(origin);
        Node targetNode = getNodeFromName(target);

        Graph graph = spfGraph(originNode);
        for(Node node : graph.getNodes()) {
            if(node == targetNode) {

                return node.getShortestPath();
            }
        }
        List<Node> emptyList = new ArrayList<>();
        return emptyList;
    }

    public List<Node> getPathNodesInverse(String origin, String target) {
        Node originNode = getNodeFromName(origin);
        Node targetNode = getNodeFromName(target);

        Graph graph = spfGraph(targetNode);
        for(Node node : graph.getNodes()) {
            if(node == originNode) {
                for(Node othernode: node.getShortestPath()) {
                }
                return node.getShortestPath();
            }
        }
        List<Node> emptyList = new ArrayList<>();
        return emptyList;
    }

    public String getPathNodeNames(String origin, String target) {
        Node originNode = getNodeFromName(origin);
        Node targetNode = getNodeFromName(target);

        Graph graph = spfGraph(originNode);
        String returnString = "";
        for(Node node : graph.getNodes()) {
            if(node == targetNode) {
                for(Node innerNode: node.getShortestPath()) {
                    returnString += innerNode.getName() + " ";
                }
            }
        }
        return returnString;
    }

    private Node getNodeFromName(String name) {
        for (Node node : fieldGraph.getNodes()) {
            if (node.getName() == name) {
                return node;
            }
        }

        throw new IllegalArgumentException("Invalid argument: " + name + " is not a node.");

    }
}
