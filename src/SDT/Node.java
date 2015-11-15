package SDT;


import static SDT.Util.dotProduct;
import static SDT.Util.rand;
import static SDT.Util.sigmoid;

class Node {
    Node parent = null;
    Node leftNode = null;
    Node rightNode = null;
    boolean isLeaf = true;
    boolean isLeft;
    double w0;
    double[] w;

    Node(int attribute_count) {
        w = new double[attribute_count];
        this.w0 = rand(-0.005, 0.005);
    }

    public void setChildren(Node l, Node r) {
        this.leftNode = l;
        this.rightNode = r;
        this.isLeaf = false;
        l.parent = this;
        r.parent = this;
        l.isLeft = true;
        r.isLeft = false;

        this.w0 = rand(-0.005, 0.005);

        for (int i = 0; i < this.w.length; i++)
            this.w[i] = rand(-0.005, 0.005);

    }

    public void deleteChildren() {
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
            return sigmoid(r);
        else
            return r;
    }


    double g(Instance instance) {

        return sigmoid(dotProduct(this.w, instance.attributes) + this.w0);
    }

    public String toString(int tab) {
        String s = "";
        for (int i = 0; i < tab; i++) {
            s += "\t";
        }
        if (isLeaf)
            s += "LEAF";
        else {
            s += "NODE" + "\n";
            s += this.leftNode.toString(tab + 1) + "\n";
            s += this.rightNode.toString(tab + 1);
        }
        return s;
    }
}