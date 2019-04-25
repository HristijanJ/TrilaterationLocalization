import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrilaterationLocalisation {

    public static List<Node> generateStartingNodes(int numberOfNodes, int surfaceLength){
        Random rand = new Random();
        double x, y;
        List<Node> nodes = new ArrayList<>();
        for(int i=0; i<numberOfNodes; i++){
            x = rand.nextDouble() * surfaceLength;
            y = rand.nextDouble() * surfaceLength;
            Node n = new Node(x, y);
            nodes.add(n);
        }
        return nodes;
    }

    public static List<Node> generateRandomAnchorNodes(List<Node> nodes, int numberOfNodes, int percentOfAnchorNodes){
        int numberOfAnchorNodes =(int) (numberOfNodes * (percentOfAnchorNodes/100.0));
        int nodesMadeAnchors = 0;
        List<Node> anchorNodes = new ArrayList<>();
        Random rand = new Random();
        int index;
        while(nodesMadeAnchors < numberOfAnchorNodes){
            index = rand.nextInt(numberOfNodes);
            if(!nodes.get(index).isAnchor()){
                nodes.get(index).setAnchor(true);
                nodes.get(index).setNewX(nodes.get(index).getX());
                nodes.get(index).setNewY(nodes.get(index).getY());
                anchorNodes.add(nodes.get(index));
                nodesMadeAnchors++;
            }
        }
        return anchorNodes;
    }

    public static boolean checkIfNodeCanBecomeAnchor(List<Node> anchorNodes, Node node, int radioRange, double signalNoise){
        Node firstClosest = null, secondClosest = null, thirdClosest = null;
        for (Node n: anchorNodes) {
            double distance = node.distanceToNode(n) * (1 + signalNoise);
            if(distance < node.distanceToNode(firstClosest)* (1 + signalNoise) && radioRange >= distance){
                thirdClosest = secondClosest;
                secondClosest = firstClosest;
                firstClosest = n;
            }
            else{
                if(distance < node.distanceToNode(secondClosest)* (1 + signalNoise) && radioRange >= distance) {
                    thirdClosest = secondClosest;
                    secondClosest = n;
                }
                else{
                    if(distance < node.distanceToNode(thirdClosest)* (1 + signalNoise) && radioRange >= distance){
                        thirdClosest = n;
                    }
                }
            }
        }
        if(firstClosest != null && secondClosest != null && thirdClosest != null){
            calculatePositionWithTrilateration(node, firstClosest, secondClosest, thirdClosest, signalNoise);
            anchorNodes.add(node);
            return true;
        }
        return false;
    }

    public static void calculatePositionWithTrilateration(Node node, Node firstAnchor, Node secondAnchor, Node thirdAnchor, double signalNoise){
        double r1 = node.distanceToNode(firstAnchor) * (1 + signalNoise);
        double r2 = node.distanceToNode(secondAnchor) * (1 + signalNoise);
        double r3 = node.distanceToNode(thirdAnchor) * (1 + signalNoise);
        double x1 = firstAnchor.getNewX();
        double x2 = secondAnchor.getNewX();
        double x3 = thirdAnchor.getNewX();
        double y1 = firstAnchor.getNewY();
        double y2 = secondAnchor.getNewY();
        double y3 = thirdAnchor.getNewY();
        double c = r1*r1 - r2*r2 - x1*x1 + x2*x2 - y1*y1 + y2*y2;
        double f = r2*r2 - r3*r3 - x2*x2 + x3*x3 - y2*y2 + y3*y3;
        double a = (-2*x1 + 2*x2);
        double b = (-2*y1 + 2*y2);
        double d = (-2*x2 + 2*x3);
        double e = (-2*y2 + 2*y3);
        double newX = (c*e - f*b) / (e*a - b*d);
        double newY = (c*d - a*f) / (b*d - a*e);
        node.setAnchor(true);
        node.setNewX(newX);
        node.setNewY(newY);
    }



    public static double localizeNetworkWithTrilateration(int numberOfNodes, int surfaceLength, int radioRange,
                                                          double signalNoise, int percentOfAnchorNodes){
        List<Node> nodes = generateStartingNodes(numberOfNodes, surfaceLength);
        List<Node> anchorNodes = generateRandomAnchorNodes(nodes, numberOfNodes, percentOfAnchorNodes);
        boolean shouldExecuteOneMoreTime = true;
        while(shouldExecuteOneMoreTime){
            int nodesMadeAnchors = 0;
            for (Node n: nodes) {
                if(!n.isAnchor()){
                    if(checkIfNodeCanBecomeAnchor(anchorNodes, n, radioRange, signalNoise))
                        nodesMadeAnchors++;
                }
            }
            if(nodesMadeAnchors == 0)
                shouldExecuteOneMoreTime = false;
        }
        return anchorNodes.size() / (numberOfNodes * 1.0);
    }

    public static void main(String[] args) {

        System.out.println(localizeNetworkWithTrilateration(100, 500, 50, 0.1, 50 ));
    }
}
