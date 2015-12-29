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
    double[] rho;
    int lastIndex_rho = -1;
    double[] w;
    double w0;
    String name;
    static boolean hardInit = false;
    public double[] gradient_rho;
    public double[] gradient_sum_rho;
    public double[] gradient;
    public double[] gradient_sum;
    public double gradientSum = 0;
    public static boolean isClassify = false;
    public static boolean is_k_Classify = false;

    public double[] deltas;
    public double[] ys;

    public double[] softmax_sigmoids;
    public double[] _sigmoids;

    double y;
    double g;
    double gama = 1;

    Node(int attribute_count) {
        ATTRIBUTE_COUNT = attribute_count;
        w = new double[attribute_count];
        for (int i = 0; i < ATTRIBUTE_COUNT; i++)
            w[i] = rand(-0.01, 0.01);
        w0 = rand(-0.01, 0.01);
        if(isClassify){
            if(BT.CLASS_NAMES.size() == 2)
                rho = new double[1];
            else {
                rho = new double[BT.CLASS_NAMES.size()];
                gradient_rho = new double[BT.CLASS_NAMES.size()];
                gradient_sum_rho = new double[BT.CLASS_NAMES.size()];
                Arrays.fill(gradient_rho, 0);
                Arrays.fill(gradient_sum_rho, 0);
                is_k_Classify = true;
            }
        }else{
            rho = new double[1];
        }
        deltas = new double[rho.length];
        ys = new double[rho.length];

        for (int i = 0; i < rho.length; i++)
            rho[i] = rand(-0.01, 0.01);

        gama = 1;
        gradient = new double[ATTRIBUTE_COUNT + 3];
        gradient_sum = new double[ATTRIBUTE_COUNT + 3];
        Arrays.fill(gradient, 0);
        Arrays.fill(gradient_sum, 0);

        name = BT.count++ + "abc";
    }


    public void backPropagate(Instance instance) {
        lastIndex_rho = (int)instance.classValue;
        calculateGradient(instance);
//        System.out.println(name + " " + gradient[0] + " " + gradient[1] + " " + gradient[2] + " " + gama + " " + delta(instance));
        if (!isLeaf) {
            //leftNode.G(instance);
            //rightNode.G(instance);
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
            if (!isClassify)
                delta = y - i.classValue;
            else {
                if(!is_k_Classify)
                    delta = sigmoid(y) - i.classValue;
                else
//                    delta = sigmoid(F(i)) - i.classValue;
//                    delta = sigmoid(F(i)) - 1;
                    delta = BT.ROOT.softmax_sigmoids[(int) i.classValue] - 1;

            }
        } else if (isLeft) {
            delta = parent.delta(i) * (1 - parent.gama) * parent.G(i);
        } else {
            delta = parent.delta(i) * (1 - parent.gama) * (1 - parent.G(i));
        }
        return delta;
    }
    public double delta(Instance i, int index) {
        double delta = 0;
        if (parent == null) {
////          delta = sigmoid(F(i)) - i.classValue;
//            if(index == (int)i.classValue)
//                delta = sigmoid(F(i)) - 1;
//            else
//                delta = sigmoid(F(i, index)) - 0;
            if(index == (int)i.classValue)
                delta = BT.ROOT.softmax_sigmoids[index] - 1;
            else
                delta = BT.ROOT.softmax_sigmoids[index];
        } else if (isLeft) {
            delta = parent.delta(i, index) * (1 - parent.gama) * parent.G(i);
        } else {
            delta = parent.delta(i, index) * (1 - parent.gama) * (1 - parent.G(i));
        }
        return delta;
    }


    public void calculateGradient(Instance instance) {
        double g = sigmoid(dotProduct(w, instance.attributes) + w0);
        double delta = delta(instance);
        double leftF = 0;
        double rightF = 0;
        if (leftNode != null) {
            if(rho.length == 1)
                leftF = leftNode.ys[0];
            else
                leftF = leftNode.ys[(int) instance.classValue];
        }if (rightNode != null) {
            if(rho.length == 1)
                rightF = rightNode.ys[0];
            else
                rightF = rightNode.ys[(int) instance.classValue];
        }
        gradient[0] = delta * gama;
        if(!is_k_Classify)
            gradient[1] = delta * (-g * leftF - (1 - g) * rightF + rho[0]) - BT.Lambda;
        else {
            for(int i = 0; i < gradient_rho.length; i++){
                gradient_rho[i] = delta(instance, i) * gama;
            }
            gradient[1] = delta * (-g * leftF - (1 - g) * rightF + rho[(int) instance.classValue]) - BT.Lambda;
        }
//        System.out.println(delta + " " + leftF + " " + rightF + " " + g);
        gradient[2] = delta * (1 - gama) * g * (1 - g) * (leftF - rightF);
        for (int i = 3; i < ATTRIBUTE_COUNT + 3; i++) {
            gradient[i] = delta * (1 - gama) * g * (1 - g) * (leftF - rightF) * instance.attributes[i - 3];
        }
    }


    public double G(Instance instance) {
        g = sigmoid(dotProduct(w, instance.attributes) + w0);
        return g;
    }

    public double F(Instance instance) {
        double g = sigmoid(dotProduct(w, instance.attributes) + w0);
        double y;
        double rho_current = rho[0];
        if(is_k_Classify)
            rho_current = rho[(int)instance.classValue];
        if (leftNode == null || rightNode == null)
            y = gama * rho_current;
        else
            y = (1 - gama) * (g * (leftNode.F(instance)) + (1 - g) * (rightNode.F(instance))) + gama * rho_current;
        return y;
    }

    public double F(Instance instance, int index) {
        g = sigmoid(dotProduct(w, instance.attributes) + w0);
//        System.out.println(w0);
        double y;
        double rho_current = rho[index];
        if (leftNode == null || rightNode == null)
            y = gama * rho_current;
        else
            y = (1 - gama) * (g * (leftNode.F(instance, index)) + (1 - g) * (rightNode.F(instance, index))) + gama * rho_current;
        ys[index] = y;
        return y;
    }

    public double[] sigmoid_F_rho(Instance instance) {
        double[] f = new double[rho.length];
        String s = "";
        for(int i = 0; i < rho.length; i++) {
            f[i] = (F(instance, i));
            s += f[i] + " ";
        }
        _sigmoids = f;
        softmax_sigmoids = Util.softmax(_sigmoids);
//        System.out.println(s + " " + rho[0] + " " + rho[1] + " " + rho[2] + " " + rho[3] + " " + instance.classValue);
        return f;
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
        gradientSum += dotProduct(gradient, gradient);
        for(int i = 0; i < gradient_sum.length; i++) {
            //System.out.println(gradient[i]);
            gradient_sum[i] += gradient[i] * gradient[i];
            if(gradient_sum[i] == 0)
                gradient_sum[i] = 0.01;
        }
        if(is_k_Classify){
            for(int i = 0; i < gradient_sum_rho.length; i++) {
                //System.out.println(gradient[i]);
                gradient_sum_rho[i] += gradient_rho[i] * gradient_rho[i];
                if(gradient_sum_rho[i] == 0)
                    gradient_sum_rho[i] = 0.01;
            }
        }
        if(!is_k_Classify)
            rho[0] = rho[0] - BTMain.LEARNING_RATE * gradient[0] / Math.sqrt(gradient_sum[0]);
        else {
            for(int i = 0; i < rho.length; i++)
                rho[i] = rho[i] - BTMain.LEARNING_RATE * gradient_rho[i] / Math.sqrt(gradient_sum_rho[i]);
//                rho[lastIndex_rho] = rho[lastIndex_rho] - BTMain.LEARNING_RATE * gradient[0] / Math.sqrt(gradient_sum[0]);
        }
        setGama(gama - BTMain.LEARNING_RATE * gradient[1] / Math.sqrt(gradient_sum[1]));
//        System.out.println(gradient[2]);
        w0 = w0 - BTMain.LEARNING_RATE * gradient[2] / Math.sqrt(gradient_sum[2]);
//        System.out.println(w0);

        for (int j = 3; j < ATTRIBUTE_COUNT + 3; j++) {
            w[j - 3] = w[j - 3] - BTMain.LEARNING_RATE * gradient[j] / Math.sqrt(gradient_sum[j]);
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