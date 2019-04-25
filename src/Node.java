public class Node {

    private double x;
    private double y;
    private double newX;
    private double newY;
    private boolean isAnchor;

    public Node(double x, double y){
        this.x = x;
        this.y = y;
        isAnchor = false;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getNewX() {
        return newX;
    }

    public void setNewX(double newX) {
        this.newX = newX;
    }

    public double getNewY() {
        return newY;
    }

    public void setNewY(double newY) {
        this.newY = newY;
    }

    public boolean isAnchor() {
        return isAnchor;
    }

    public void setAnchor(boolean anchor) {
        isAnchor = anchor;
    }

    public double distanceToNode(Node node){
        if(node == null)
            return 100000000;
        else
            return Math.sqrt((node.newX - x)*(node.newX - x) + (node.newY - y)*(node.newY - y));
    }

    @Override
    public String toString(){
        return String.format("Width: %.2f, Height: %.2f, isAnchor: %b", x, y, isAnchor);
    }
}
