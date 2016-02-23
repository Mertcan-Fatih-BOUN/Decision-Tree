package BuddingTree;

import misc.Instance;

import java.util.Arrays;
import java.util.HashMap;

import static misc.Util.*;

class Node {
    Node parent = null;
    Node leftNode = null;
    Node rightNode = null;
    boolean isLeaf = true;
    boolean isLeft;

    double[] rho;
    double[] w;
    double w0;
    double gama = 1;
    double[] y;
    double g;

    double[] sum_grad_rho;
    double[] sum_grad_w;
    double sum_grad_w0;
    double sum_grad_gama;

    double[] gradient_w;
    double[] gradient_rho;
    double gradient_w0 = 0;
    double gradient_gama = 0;

    BT tree;
    Instance last_y_instance = null;
    Instance last_g_instance = null;

    Node(BT tree) {
        this.tree = tree;

        w = new double[tree.ATTRIBUTE_COUNT];
        for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++)
            w[i] = rand(-0.01, 0.01);

        w0 = rand(-0.01, 0.01);

        if (tree.isClassify) {
            if (tree.CLASS_NAMES.size() == 2) {
                rho = new double[1];
                gradient_rho = new double[1];
                sum_grad_rho = new double[1];
                y = new double[1];
                Arrays.fill(y, 0);
                Arrays.fill(gradient_rho, 0);
                Arrays.fill(sum_grad_rho, 0);
            }else {
                rho = new double[tree.CLASS_NAMES.size()];
                gradient_rho = new double[tree.CLASS_NAMES.size()];
                sum_grad_rho = new double[tree.CLASS_NAMES.size()];
                y = new double[tree.CLASS_COUNT];
                Arrays.fill(y, 0);
                Arrays.fill(gradient_rho, 0);
                Arrays.fill(sum_grad_rho, 0);
            }
        } else {
            rho = new double[1];
            y = new double[1];
            Arrays.fill(y, 0);
            gradient_rho = new double[1];
            sum_grad_rho = new double[1];
            Arrays.fill(gradient_rho, 0);
            Arrays.fill(sum_grad_rho, 0);
        }

        for (int i = 0; i < rho.length; i++)
            rho[i] = rand(-0.01, 0.01);



        gama = 1;
        gradient_w = new double[tree.ATTRIBUTE_COUNT];
        sum_grad_w = new double[tree.ATTRIBUTE_COUNT];
        Arrays.fill(gradient_w, 0);
        Arrays.fill(sum_grad_w, 0);
    }


    public double g(Instance instance) {
        if (last_g_instance == instance)
            return g;

        g = sigmoid(dotProduct(w, instance.attributes) + w0);
        last_g_instance = instance;
        return g;
    }

    public double[] F(Instance instance) {
        if (last_y_instance == instance) {
            return y;
        }


        if (this.leftNode == null) {
            for (int i = 0; i < y.length; i++) {
                y[i] = this.gama * rho[i];
            }
        } else {
            double[] _yL = this.leftNode.F(instance);
            double[] _yR = this.rightNode.F(instance);
            for (int i = 0; i < y.length; i++) {
                double mg = this.g(instance);
                y[i] = (1 - gama) * ((mg * _yL[i]) + ((1 - mg) * _yR[i])) + gama * rho[i];
            }
        }
        last_y_instance = instance;
//        y = softmax(y);
        return (y);
    }

    public double[] F_last(Instance instance) {
        double[] r = this.F(instance);
        if (tree.isClassify) {
            if (tree.is_k_Classify) {
                r = softmax(r);
            } else
                r[0] = sigmoid(r[0]);
        }
        return r;
    }

    public double[] delta(Instance instance) {
        double[] delta = new double[tree.CLASS_COUNT];
        if (this.parent == null) {
            double[] _y = F_last(instance);
            double[] actual_y = new double[y.length];
            Arrays.fill(actual_y, 0);
            if (tree.isClassify) {
                if (tree.is_k_Classify) {
                    actual_y[(int) instance.classValue] = 1;
                } else
                    actual_y[0] = instance.classValue;
            } else
                actual_y[0] = instance.classValue;

            for (int i = 0; i < delta.length; i++) {
                delta[i] = _y[i] - actual_y[i];
            }

        } else {
            double[] p_delta = this.parent.delta(instance);
            if (this == this.parent.leftNode) {
                for (int i = 0; i < delta.length; i++) {
                    delta[i] = p_delta[i] * (1 - this.parent.gama) * this.parent.g(instance);
                }
            } else {
                for (int i = 0; i < delta.length; i++) {
                    delta[i] = p_delta[i] * (1 - this.parent.gama) * (1 - this.parent.g(instance));
                }
            }
        }
        return delta;
    }


    public void backPropagate(Instance instance) {
        calculateGradient(instance);
        if (leftNode != null) {
            leftNode.backPropagate(instance);
            rightNode.backPropagate(instance);
        }
    }

    public void update() {
        learnParameters();

        if (leftNode != null) {
            leftNode.update();
            rightNode.update();
        }

        if (gama < 1 && leftNode == null) {
            splitNode();
        }
    }

    public void setGama(double f) {
        if (f < 0) {
            gama = 0;
        } else if (f > 1) {
            gama = 1;
        } else
            gama = f;
    }

    public void calculateGradient(Instance instance) {
        double[] delta = delta(instance);
        double[] left_y;
        double[] right_y;
        double g = this.g(instance);
//        System.out.println(g);

        if (leftNode != null) {
            left_y = leftNode.y;
        } else {
            left_y = new double[tree.CLASS_COUNT];
            Arrays.fill(left_y, 0);
        }

        if (rightNode != null) {
            right_y = rightNode.y;
        } else {
            right_y = new double[tree.CLASS_COUNT];
            Arrays.fill(right_y, 0);
        }

        gradient_w = new double[tree.ATTRIBUTE_COUNT];
        Arrays.fill(gradient_w, 0);
        for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++) {
            for (int j = 0; j < tree.CLASS_COUNT; j++)
                gradient_w[i] += delta[j] * (1 - gama) * g * (1 - g) * (left_y[j] - right_y[j]) * instance.attributes[i];
            //gradient_w[i] /= tree.CLASS_COUNT;
            // gradient_w[i] = delta[(int) instance.classValue] * (1 - gama) * g * (1 - g) * (left_y[(int) instance.classValue] - right_y[(int) instance.classValue]) * instance.attributes[i];
        }

        gradient_w0 = 0;
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_w0 += delta[i] * (gama);
        // gradient_w0 = delta[(int) instance.classValue] * (gama);
        // gradient_w0 /= tree.CLASS_COUNT;

        gradient_rho = new double[tree.CLASS_COUNT];
        for (int i = 0; i < tree.CLASS_COUNT; i++) {
            gradient_rho[i] = delta[i] * gama;
        }

        gradient_gama = 0;
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_gama += delta[i] * ((-g * left_y[i]) - (1 - g) * right_y[i] + rho[i]) - tree.Lambda;
        // gradient_gama /= tree.CLASS_COUNT;
        // gradient_gama = delta[(int) instance.classValue] * ((-g * left_y[(int) instance.classValue]) - (1 - g) * right_y[(int) instance.classValue] + rho[(int) instance.classValue]) - tree.Lambda;

    }

    public int size() {
        if (leftNode == null)
            return 1;
        else
            return 1 + leftNode.size() + rightNode.size();
    }

    int myEffSize() {
        if (leftNode == null || gama == 1)
            return 1;
        else
            return 1 + leftNode.myEffSize() + rightNode.myEffSize();
    }

    void learnParameters() {

        for (int i = 0; i < sum_grad_w.length; i++) {
            sum_grad_w[i] += gradient_w[i] * gradient_w[i];
        }

        sum_grad_w0 += gradient_w0 * gradient_w0;

        for (int i = 0; i < sum_grad_rho.length; i++) {
            sum_grad_rho[i] += gradient_rho[i] * gradient_rho[i];
        }

        sum_grad_gama += gradient_gama * gradient_gama;

        for (int i = 0; i < sum_grad_w.length; i++) {
            if (sum_grad_w[i] == 0)
                sum_grad_w[i] = 0.01;
            w[i] = w[i] - tree.LEARNING_RATE * gradient_w[i] / Math.sqrt(sum_grad_w[i]);
        }
        if (sum_grad_w0 == 0)
            sum_grad_w0 = 0.01;
        w0 = w0 - tree.LEARNING_RATE * gradient_w0 / Math.sqrt(sum_grad_w0);

        if (sum_grad_gama == 0)
            sum_grad_gama = 0.01;
        setGama(gama - tree.LEARNING_RATE * gradient_gama / Math.sqrt(sum_grad_gama));

        for (int i = 0; i < sum_grad_rho.length; i++) {
            if (sum_grad_rho[i] == 0)
                sum_grad_rho[i] = 0.01;
            rho[i] = rho[i] - tree.LEARNING_RATE * gradient_rho[i] / Math.sqrt(sum_grad_rho[i]);
        }
        last_y_instance = null;
        last_g_instance = null;
    }

    void splitNode() {
        leftNode = new Node(tree);
        leftNode.parent = this;
        leftNode.isLeft = true;

        rightNode = new Node(tree);
        rightNode.parent = this;
        rightNode.isLeft = false;

        isLeaf = false;
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