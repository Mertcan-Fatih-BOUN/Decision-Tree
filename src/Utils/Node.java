package Utils;

public class Node {
    public boolean initiallyCreated = false;
    public String name;
    public int[][] classes;
    public int id = -1;
    public int attributeNumber;
    public double value;
    public double[] massCenter;


    public Node parent = null;
    public Node leftNode = null;
    public Node rightNode = null;
    public boolean isLeaf = true;
    public boolean isLeft;
    public double w0;
    public double[] w = new double[Util.ATTRIBUTE_COUNT];


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

    public Node() {
        this.w0 = Util.rand(-0.005, 0.005);
    }

    public void setChildren(Node l, Node r) {
        this.leftNode = l;
        this.rightNode = r;
        this.isLeaf = false;
        l.parent = this;
        r.parent = this;
        l.isLeft = true;
        r.isLeft = false;

        this.w0 = Util.rand(-0.005, 0.005);

        for (int i = 0; i < this.w.length; i++)
            this.w[i] = Util.rand(-0.005, 0.005);


    }

    public void deleteChilderen() {
        this.isLeaf = true;
        this.leftNode = null;
        this.rightNode = null;
    }

    public double F(Instance instance) {
        double r;
        if (this.isLeaf) {
            r = this.w0;
        } else {
            double g = this.g(instance);
            r = this.leftNode.F(instance) * g + this.rightNode.F(instance) * (1 - g);
        }
        if (this.parent == null)
            return Util.sigmoid(r);
        else
            return r;
    }


    public double g(Instance instance) {

        return Util.sigmoid(Util.dotProduct(this.w, instance.attributes) + this.w0);
    }
}