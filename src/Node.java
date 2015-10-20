public class Node {
    public boolean isLeaf;
    public boolean initiallyCreated = false;
    public String name;
    public int[][] classes;
    public int id = -1;
    public int attributeNumber;
    public double value;
    public Node leftNode;
    public Node rightNode;
    public Node parentNode;
    public double[] massCenter;


    public Node(String cn) {
        name = cn;
        isLeaf = true;
    }

    public Node(String cn, double[] mCenter) {
        name = cn;
        isLeaf = true;
        massCenter = mCenter;
        this.id = id;
    }

    public Node(String cn, double[] mCenter, int id) {
        name = cn;
        isLeaf = true;
        massCenter = mCenter;
        this.id = id;
    }

    public Node(int an, double v, Node ln, Node rn) {
        isLeaf = false;
        attributeNumber = an;
        value = v;
        leftNode = ln;
        rightNode = rn;
    }

    public String toString(int k) {
        String s = "";
        for (int i = 0 ; i < k; i++) {
            s += "\t";
        }
        if (isLeaf)
            s += name;
        else
            s += "The attribute "  + attributeNumber + " with value " + value + "\n " + leftNode.toString(k+1) + "\n" + rightNode.toString(k+1);

        return s;
    }
}