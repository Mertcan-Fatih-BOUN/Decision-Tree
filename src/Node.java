public class Node {
    public boolean isLeaf;
    public String className;
    public int attributeNumber;
    public double value;
    public Node leftNode;
    public Node rightNode;


    public Node(String cn) {
        className = cn;
        isLeaf = true;
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
            s += className;
        else
            s += "The attribute "  + attributeNumber + " with value " + value + "\n " + leftNode.toString(k+1) + "\n" + rightNode.toString(k+1);

        return s;
    }
}