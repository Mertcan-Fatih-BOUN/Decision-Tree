package BuddingTree;


import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
    String name;
    static boolean hardInit = false;
    public double[] gradient;
    public double gradientSum = 0;

    double y;
    double g;
    double gama = 1;

    Node(int attribute_count) {
        ATTRIBUTE_COUNT = attribute_count;
        w = new double[attribute_count];
        for (int i = 0; i < ATTRIBUTE_COUNT; i++)
            w[i] = rand(-0.01, 0.01);
        w0 = rand(-0.01, 0.01);
        gama = 1;
        gradient = new double[ATTRIBUTE_COUNT + 2];
        Arrays.fill(gradient, 0);

        name = BT.count++ + "abc";
    }


    public void backPropagate(Instance instance) {
        calculateGradient(instance);
//        System.out.println(name + " " + gradient[0] + " " + gradient[1] + " " + gradient[2] + " " + gama + " " + delta(instance));
        if (!isLeaf) {
            leftNode.backPropagate(instance);
            rightNode.backPropagate(instance);
        }
    }

    public void printGradient(){
        String s = "";
        for(int i = 0; i < ATTRIBUTE_COUNT + 2; i++)
            s += gradient[i] + " ";
        System.out.println(s);
    }

    public void update() {
        double previous_Gama = gama;
        learnParameters();

        if (!isLeaf){
            leftNode.update();
            rightNode.update();
        }

        if(previous_Gama == 1 && gama < 1 && leftNode == null) {
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

    public double delta(Instance i) {
        double delta = 0;
        if (parent == null) {
            delta = F(i) - i.classValue;
        } else if (isLeft) {
            delta = parent.delta(i) * (1 - parent.gama) * parent.G(i);
        } else {
            delta = parent.delta(i) * (1 - parent.gama) * (1 - parent.G(i));
        }
        return delta;
    }

    public void calculateGradient(Instance instance) {
        double delta = delta(instance);
        double g = G(instance);
        double leftF = 0;
        double rightF = 0;
        if (leftNode != null)
            leftF = leftNode.F(instance);
        if (rightNode != null)
            rightF = rightNode.F(instance);
        gradient[0] = delta * gama;
        gradient[1] = delta * (-g * leftF - (1 - g) * rightF + w0) - BT.Lambda;
        for (int i = 2; i < ATTRIBUTE_COUNT + 2; i++) {
            gradient[i] = delta * (1 - gama) * g * (1 - g) * (leftF - rightF) * instance.attributes[i - 2];
        }
    }

    public double G(Instance instance) {
        g = sigmoid(dotProduct(w, instance.attributes) + w0);
        return g;
    }

    public double F(Instance instance) {
        g = sigmoid(dotProduct(w, instance.attributes) + w0);
        if (leftNode == null || rightNode == null)
            y = gama * w0;
        else
            y = (1 - gama) * (g * (leftNode.F(instance)) + (1 - g) * (rightNode.F(instance))) + gama * w0;
        return y;

    }

    int size() {
        if (leftNode == null)
            return 1;
        else
            return 1 + leftNode.size() + rightNode.size();
    }

    void learnParameters() {
        gradientSum += dotProduct(gradient, gradient);

        w0 = w0 - BTMain.LEARNING_RATE * gradient[0] / Math.sqrt(gradientSum);

        setGama(gama - BTMain.LEARNING_RATE * gradient[1] / Math.sqrt(gradientSum));

        for (int j = 2; j < ATTRIBUTE_COUNT + 2; j++) {
            w[j - 2] = w[j - 2] - BTMain.LEARNING_RATE * gradient[j] / Math.sqrt(gradientSum);
        }
    }


    void splitNode() {
        leftNode = new Node(ATTRIBUTE_COUNT);
        leftNode.parent = this;
        leftNode.isLeft = true;

        rightNode = new Node(ATTRIBUTE_COUNT);
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