package SDTMultiClass;


import BuddingTreeMultiClass.Instance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static misc.Util.*;

@SuppressWarnings("Duplicates")
class Node {
    Node parent = null;
    Node leftNode = null;
    Node rightNode = null;
    double[] rho;
    double[] w;
    double w0;

    double[] y;
    double g;

    double t;

    SDTM tree;

    Node(SDTM tree, int depth, Node parent) {
        this.tree = tree;
        this.parent = parent;

        y = new double[tree.CLASS_COUNT];
        if (depth < tree.DEPTH) {
            w = new double[tree.ATTRIBUTE_COUNT];

            for (int i = 0; i < w.length; i++)
                w[i] = rand(-0.005, 0.005, tree.random);
            w0 = rand(-0.005, 0.005, tree.random);

            this.leftNode = new Node(tree, depth + 1, this);
            this.rightNode = new Node(tree, depth + 1, this);
        } else {
            rho = new double[tree.CLASS_COUNT];
            for (int i = 0; i < rho.length; i++)
                rho[i] = rand(-0.005, 0.005, tree.random);
        }
    }


    public double[] F(Instance instance) {
        if (leftNode == null) {
            y = Arrays.copyOf(rho, rho.length);
        } else {
            g = sigmoid(dotProduct(w, instance.x) + w0);
            double[] y_left = leftNode.F(instance);
            double[] y_right = rightNode.F(instance);
            for (int i = 0; i < y.length; i++)
                y[i] = g * y_left[i] + (1 - g) * y_right[i];
        }
        if (parent == null) {
            for (int i = 0; i < tree.CLASS_COUNT; i++) {
                y[i] = sigmoid(y[i]);
            }
        }
        return y;
    }


    int size() {
        if (leftNode == null)
            return 0;
        else
            return 1 + leftNode.size() + rightNode.size();
    }


    void learn(Instance instance) {
        if (parent == null)
            t = 1;
        else {
            if (this == parent.leftNode)
                t = parent.g * parent.t;
            else
                t = (1 - parent.g) * parent.t;
        }

        if (leftNode != null) {
            double[] dw = new double[tree.ATTRIBUTE_COUNT];
            double dw0 = 0;
            Arrays.fill(dw, 0);
            for (int k = 0; k < y.length; k++) {
                for (int count = 0; count < tree.ATTRIBUTE_COUNT; count++)
                    dw[count] += (-t * tree.LEARNING_RATE * instance.d[k] * (leftNode.y[k] - rightNode.y[k]) * g * (1 - g)) * instance.x[count];

                dw0 += (-t * instance.d[k] * (leftNode.y[k] - rightNode.y[k]) * g * (1 - g));
            }

            for (int count = 0; count < tree.ATTRIBUTE_COUNT; count++)
                w[count] += dw[count];

            w0 += dw0;
            leftNode.learn(instance);
            rightNode.learn(instance);
        } else {
            double[] drho = new double[tree.CLASS_COUNT];
            for (int k = 0; k < drho.length; k++) {
                drho[k] = -t * tree.LEARNING_RATE * instance.d[k];
                rho[k] += drho[k];
            }


        }
    }


    void learnParameters(ArrayList<Instance> X, double alpha, SDTM tree, int MAX_EPOCH) {
        double u = 0.1;

        double[] dw = new double[tree.ATTRIBUTE_COUNT];
        double[] dwp = new double[tree.ATTRIBUTE_COUNT];
        Arrays.fill(dw, 0);
        Arrays.fill(dwp, 0);
        double[] dwleftp = new double[rho.length];
        double[] dwrightp = new double[rho.length];
        double[] dwleft = new double[rho.length];
        double[] dwright = new double[rho.length];

        double dw0p = 0;
        double dw0;


        for (int e = 0; e < MAX_EPOCH; e++) {
            ArrayList<Integer> indices = new ArrayList<>();
            for (int i = 0; i < X.size(); i++) indices.add(i);
            Collections.shuffle(indices);
            for (int i = 0; i < X.size(); i++) {
                int j = indices.get(i);
                Instance instance = X.get(j);
                double[] x = instance.x;
                int[] r = instance.r;
                double[] y = tree.ROOT.F(instance);
                double[] d = new double[y.length];
                for (int c = 0; c < y.length; c++)
                    d[c] = y[c] - r[c];

                double t = alpha;
                Node m = this;
                Node p;

                while (m.parent != null) {
                    p = m.parent;
                    if (m == m.parent.leftNode)
                        t *= p.g;
                    else
                        t *= (1 - p.g);
                    m = m.parent;
                }

                Arrays.fill(dw, 0);
                dw0 = 0;
                for (int k = 0; k < y.length; k++) {

                    for (int count = 0; count < tree.ATTRIBUTE_COUNT; count++)
                        dw[count] += (-t * d[k] * (leftNode.y[k] - rightNode.y[k]) * g * (1 - g)) * x[count];

                    dw0 += (-t * d[k] * (leftNode.y[k] - rightNode.y[k]) * g * (1 - g));

                    dwleft[k] = -t * d[k] * g;
                    dwright[k] = -t * d[k] * (1 - g);
                }

                for (int count = 0; count < tree.ATTRIBUTE_COUNT; count++)
                    w[count] += dw[count] + u * dwp[count];


                w0 += dw0 + u * dw0p;

                for (int k = 0; k < rho.length; k++) {
                    leftNode.rho[k] += dwleft[k] + u * dwleftp[k];
                    rightNode.rho[k] += dwright[k] + u * dwrightp[k];
                }

                dwp = Arrays.copyOf(dw, dw.length);
                dw0p = dw0;
                dwleftp = Arrays.copyOf(dwleft, rho.length);
                dwrightp = Arrays.copyOf(dwright, dwright.length);
                alpha *= 0.9999;
            }
        }
    }

    public String toString(int tab) {
        String s = "";
        for (int i = 0; i < tab; i++) {
            s += "\t";
        }
        if (leftNode == null)
            s += "LEAF";
        else {
            s += "NODE" + "\n";
            s += this.leftNode.toString(tab + 1) + "\n";
            s += this.rightNode.toString(tab + 1);
        }
        return s;
    }
}