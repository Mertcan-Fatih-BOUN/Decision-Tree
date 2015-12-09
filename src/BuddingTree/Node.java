package BuddingTree;


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
    String name;
    static boolean hardInit = false;
    public double[] gradient_w;
    public double[] gradient_w_sum;

    public double gradientSum = 0;


    double y;
    double g;
    double gama = 1;

    boolean is_classify = false;


    double gradient_game = 0;
    double gradient_game_sum = 0;
    double gradient_w0 = 0;
    double gradient_w0_sum = 0;

    Node(int attribute_count, boolean is_classify) {
        is_classify = is_classify;
        ATTRIBUTE_COUNT = attribute_count;
        w = new double[attribute_count];
        for (int i = 0; i < ATTRIBUTE_COUNT; i++)
            w[i] = rand(-0.01, 0.01);
        w0 = rand(-0.01, 0.01);
        gama = 1;
        gradient_w = new double[ATTRIBUTE_COUNT];
        Arrays.fill(gradient_w, 0);

        gradient_w_sum = new double[ATTRIBUTE_COUNT];
        Arrays.fill(gradient_w_sum, 0);


        name = BT.count++ + "abc";
    }


    public void backPropagate(Instance instance) {
        calculateGradient(instance);
//        System.out.println(name + " " + gradient_w[0] + " " + gradient_w[1] + " " + gradient_w[2] + " " + gama + " " + delta(instance));
        if (!isLeaf && gama < 1) {
            leftNode.backPropagate(instance);
            rightNode.backPropagate(instance);
        }
    }

    public void printGradient() {
        String s = "";
        for (int i = 0; i < ATTRIBUTE_COUNT + 2; i++)
            s += gradient_w[i] + " ";
        System.out.println(s);
    }

    public void update() {
        double previous_Gama = gama;
        learnParameters();

        if (!isLeaf && gama < 1) {
            leftNode.update();
            rightNode.update();
        }

        if (previous_Gama == 1 && gama < 1 && leftNode == null) {
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
            if (!is_classify)
                delta = F(i) - i.classValue;
            else {
                double y = sigmoid(F(i));

                if (y > 0.5) {
                    if (i.classValue != 1)
                        delta = 1;
                } else if (i.classValue != 0)
                    delta = 1;


            }
        } else if (isLeft) {
            delta = parent.delta(i) * (1 - parent.gama) * parent.G(i);
        } else {
            delta = parent.delta(i) * (1 - parent.gama) * (1 - parent.G(i));
        }

        return delta;
    }

    public void calculateGradient(Instance instance) {
        double delta = delta(instance);
        double g = this.g;
        double leftF = 0;
        double rightF = 0;
        if (leftNode != null)
            leftF = leftNode.F(instance);
        if (rightNode != null)
            rightF = rightNode.F(instance);

        gradient_w0 = delta * gama;
        gradient_game = delta * (-g * leftF - (1 - g) * rightF + w0) - BT.Lambda;
        if (!isLeaf && gama < 1) {
            for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
                gradient_w[i] = delta * (1 - gama) * g * (1 - g) * (leftF - rightF) * instance.attributes[i];
            }
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

    double effSize() {
        if (isLeaf || gama == 1)
            return 1;
        else
            return 1 + (1-gama)*(leftNode.effSize() + rightNode.effSize());
    }

    int myEffSize() {
        if (isLeaf || gama == 1)
            return 1;
        else
            return 1 + leftNode.myEffSize() + rightNode.myEffSize();
    }

    void learnParameters() {
        gradientSum += dotProduct(gradient_w, gradient_w);

        gradient_w0_sum += gradient_w0 * gradient_w0;

        w0 = w0 - BTMain.LEARNING_RATE * gradient_w0 / Math.sqrt(gradient_w0_sum);

        gradient_game_sum += gradient_game * gradient_game;

        setGama(gama - BTMain.LEARNING_RATE * gradient_game / Math.sqrt(gradient_game_sum));


        if (!isLeaf && gama < 1) {
            double sum = 0;
            for (int j = 0; j < ATTRIBUTE_COUNT; j++) {
                gradient_w_sum[j] += gradient_w[j] * gradient_w[j];
                w[j] = w[j] - BTMain.LEARNING_RATE * gradient_w[j] / Math.sqrt(gradient_w_sum[j]);
            }
        }
    }


    void splitNode() {
        leftNode = new Node(ATTRIBUTE_COUNT, is_classify);
        leftNode.parent = this;
        leftNode.isLeft = true;

        rightNode = new Node(ATTRIBUTE_COUNT, is_classify);
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