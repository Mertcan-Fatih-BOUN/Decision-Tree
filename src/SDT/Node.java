package SDT;


import java.util.ArrayList;
import java.util.Arrays;

import static SDT.Util.dotProduct;
import static SDT.Util.rand;
import static SDT.Util.sigmoid;

class Node {
    int ATTRIBUTE_COUNT;
    Node parent = null;
    Node leftNode = null;
    Node rightNode = null;
    boolean isLeaf = true;
    boolean isLeft;
    double w0;
    double[] w;

    double y;
    double g;

    Node(int attribute_count) {
        ATTRIBUTE_COUNT = attribute_count;
    }


    public double F(Instance instance) {
        if (isLeaf)
            y = w0;
        else {
            g = sigmoid(dotProduct(w, instance.attributes) + w0);
            y = g * (leftNode.F(instance)) + (1 - g) * (rightNode.F(instance));
        }
        return y;

    }


    void learnParameters(ArrayList<Instance> X, ArrayList<Instance> V, double alpha, SDT tree, int MAX_EPOCH) {
        double u = 0.1;

        double[] dw = new double[ATTRIBUTE_COUNT];
        double[] dwp = new double[ATTRIBUTE_COUNT];
        Arrays.fill(dw, 0);
        Arrays.fill(dwp, 0);
        double dw10p = 0, dw20p = 0, dw0p = 0;
        double dw10, dw20, dw0;

        for (int e = 0; e < MAX_EPOCH; e++) {
            for (int i = 0; i < X.size(); i++) {
                //TODO Shuffle
                int j = i;
                double[] x = X.get(j).attributes;
                double r = X.get(j).classValue;
                double y = tree.eval(X.get(j));
                double d = y - r;

                double t = alpha * d;
                Node m = this;
                Node p;

                while (m.parent != null) {
                    p = m.parent;
                    if (m.isLeft)
                        t *= p.g;
                    else
                        t *= (1 - p.g);
                    m = m.parent;
                }

                for (int count = 0; count < ATTRIBUTE_COUNT; count++)
                    dw[count] = (-t * (leftNode.y - rightNode.y) * g * (1 - g)) * x[count];

                dw0 = (-t * (leftNode.y - rightNode.y) * g * (1 - g));
                dw10 = -t * (g);
                dw20 = -t * (1 - g);


                for (int count = 0; count < ATTRIBUTE_COUNT; count++)
                    w[count] += dw[count] + u * dwp[count];

                w0 += dw0 + u * dw0p;
                leftNode.w0 += dw10 + u * dw10p;
                rightNode.w0 += dw20 + u * dw20p;

                dwp = dw;
                dw0p = dw0;
                dw10p = dw10;
                dw20p = dw20;

                alpha *= 0.9999;


            }
        }
    }

    void splitNode(ArrayList<Instance> X, ArrayList<Instance> V, SDT tree) {
        double err = tree.ErrorOfTree(V);
        double y, r;

        double oldw0 = w0;

        isLeaf = false;
        w = new double[ATTRIBUTE_COUNT];
        Arrays.fill(w, 0);

        leftNode = new Node(ATTRIBUTE_COUNT);
        leftNode.isLeft = true;
        leftNode.parent = this;


        rightNode = new Node(ATTRIBUTE_COUNT);
        rightNode.isLeft = false;
        rightNode.parent = this;

        double[] bestw = new double[ATTRIBUTE_COUNT];
        double bestw0 = 0, bestw0l = 0, bestw0r = 0;
        double bestErr = 1e10;
        double newErr;



        double alpha;
        for (int t = 0; t < tree.MAX_STEP; t++) {
            for (int i = 0; i < ATTRIBUTE_COUNT; i++)
                w[i] = rand(-0.005, 0.005);
            w0 = rand(-0.005, 0.005);
            leftNode.w0 = rand(-0.005, 0.005);
            rightNode.w0 = rand(-0.005, 0.005);

            alpha = (tree.LEARNING_RATE + 0.0) / Math.pow(2, t + 1);
            learnParameters(X, V, alpha, tree, tree.EPOCH);

            newErr = tree.ErrorOfTree(V);

            if (newErr < bestErr) {
                bestw = w;
                bestw0 = w0;
                bestw0l = leftNode.w0;
                bestw0r = rightNode.w0;
                bestErr = newErr;
            }
        }

        w = bestw;
        w0 = bestw0;
        leftNode.w0 = bestw0l;
        rightNode.w0 = bestw0r;

        if (bestErr + 1e-3 < err) {
            leftNode.splitNode(X, V, tree);
            rightNode.splitNode(X, V, tree);
        } else {
            isLeaf = true;
            leftNode = null;
            rightNode = null;
            w0 = oldw0;
            y = w0;
        }
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