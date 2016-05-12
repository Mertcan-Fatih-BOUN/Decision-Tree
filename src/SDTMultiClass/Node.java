package SDTMultiClass;


import BuddingTreeMultiClass.Instance;

import java.util.Arrays;

import static misc.Util.*;

class Node {
    Node parent = null;
    Node leftNode = null;
    Node rightNode = null;
    double[] rho;
    double[] w;
    double w0;

    double[] dw;
    double[] drho;
    double dw0;

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
            dw = new double[tree.ATTRIBUTE_COUNT];

            for (int i = 0; i < w.length; i++)
                w[i] = rand(-0.005, 0.005, tree.random);
            w0 = rand(-0.005, 0.005, tree.random);


            this.leftNode = new Node(tree, depth + 1, this);
            this.rightNode = new Node(tree, depth + 1, this);
        } else {
            rho = new double[tree.CLASS_COUNT];
            drho = new double[tree.CLASS_COUNT];
            for (int i = 0; i < rho.length; i++)
                rho[i] = rand(-0.005, 0.005, tree.random);
        }
    }


    @SuppressWarnings("SuspiciousNameCombination")
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
            dw0 = 0;
            Arrays.fill(dw, 0);
            for (int k = 0; k < y.length; k++) {
                for (int count = 0; count < tree.ATTRIBUTE_COUNT; count++)
                    dw[count] += (-t * tree.LEARNING_RATE * instance.d[k] * (leftNode.y[k] - rightNode.y[k]) * g * (1 - g)) * instance.x[count];

                dw0 += (-t * tree.LEARNING_RATE * instance.d[k] * (leftNode.y[k] - rightNode.y[k]) * g * (1 - g));
            }

            for (int count = 0; count < tree.ATTRIBUTE_COUNT; count++)
                w[count] += dw[count];

            w0 += dw0;
            leftNode.learn(instance);
            rightNode.learn(instance);
        } else {
            Arrays.fill(drho, 0);
            for (int k = 0; k < drho.length; k++) {
                drho[k] = -t * tree.LEARNING_RATE * instance.d[k];
                rho[k] += drho[k];
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