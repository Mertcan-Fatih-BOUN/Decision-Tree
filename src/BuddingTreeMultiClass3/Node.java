package BuddingTreeMultiClass3;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static misc.Util.*;

@SuppressWarnings("Duplicates")
class Node {

    Node parent = null;
    Node leftNode = null;
    Node rightNode = null;

    double[][] rho;
    double[][] sum_grad_rho;
    double[][] gradient_rho;
    double[] rho0;
    double[][] w;
    double[] w0;
    double w00;
    double w01;
    double gama = 1;
    double[] y;
    double[] delta;
    double[] g;
    double g1;
    double g2;

    double[] sum_grad_rho0;
    double[][] sum_grad_w;
    double[] sum_grad_w0;
    double sum_grad_w00;
    double sum_grad_w01;
    double sum_grad_gama;

    double[][] gradient_w;
    double[] gradient_rho0;
    double[] gradient_w0;
    double gradient_w00 = 0;
    double gradient_w01 = 0;
    double gradient_gama = 0;

    double[] P1;
    double[] P2;

    double[] gradient_P1;
    double[] gradient_P2;

    double[] sum_grad_P1;
    double[] sum_grad_P2;

    int last_instance_id_g = -1;
    int last_instance_id_rho = -1;
    int last_instance_id_y = -1;
    int last_instance_id_delta = -1;

    BTM tree;

    Node(BTM tree) {
        this.tree = tree;

        w = new double[tree.CLASS_COUNT][tree.ATTRIBUTE_COUNT];
        for(int j = 0; j < tree.CLASS_COUNT; j++)
            for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++)
                w[j][i] = rand(-0.01, 0.01);

        w0 = new double[tree.CLASS_COUNT];
        for(int j = 0; j < tree.CLASS_COUNT; j++)
            w0[j] = rand(-0.01, 0.01);
        w00 = rand(-0.01, 0.01);
        w01 = rand(-0.01, 0.01);
        sum_grad_w0 = new double[tree.CLASS_COUNT];
        gradient_w0 = new double[tree.CLASS_COUNT];
        Arrays.fill(sum_grad_w0, 0);
        Arrays.fill(gradient_w0, 0);


        rho = new double[tree.CLASS_COUNT][tree.ATTRIBUTE_COUNT];
        gradient_rho = new double[tree.CLASS_COUNT][tree.ATTRIBUTE_COUNT];
        sum_grad_rho = new double[tree.CLASS_COUNT][tree.ATTRIBUTE_COUNT];
        for(int i = 0; i < tree.CLASS_COUNT; i++){
            for(int j = 0; j < tree.ATTRIBUTE_COUNT; j++){
                if(Runner.rho_newversion)
                    rho[i][j] = rand(-0.01, 0.01);
                else
                    rho[i][j] = 0;
                gradient_rho[i][j] = 0;
                sum_grad_rho[i][j] = 0;
            }
        }

        rho0 = new double[tree.CLASS_COUNT];
        gradient_rho0 = new double[tree.CLASS_COUNT];
        sum_grad_rho0 = new double[tree.CLASS_COUNT];

        P1 = new double[tree.CLASS_COUNT];
        P2 = new double[tree.CLASS_COUNT];
        gradient_P1 = new double[tree.CLASS_COUNT];
        gradient_P2 = new double[tree.CLASS_COUNT];
        sum_grad_P1 = new double[tree.CLASS_COUNT];
        sum_grad_P2 = new double[tree.CLASS_COUNT];
        y = new double[tree.CLASS_COUNT];
        g = new double[tree.CLASS_COUNT];



        delta = new double[tree.CLASS_COUNT];
        Arrays.fill(y, 0);
        Arrays.fill(g, 0);
        Arrays.fill(gradient_rho0, 0);
        Arrays.fill(sum_grad_rho0, 0);
        Arrays.fill(P1, 0);
        Arrays.fill(P2, 0);
        Arrays.fill(gradient_P1, 0);
        Arrays.fill(gradient_P2, 0);
        Arrays.fill(sum_grad_P1, 0);
        Arrays.fill(sum_grad_P2, 0);

        for (int i = 0; i < rho0.length; i++)
            rho0[i] = rand(-0.01, 0.01);

        for (int i = 0; i < rho0.length; i++) {
            P1[i] = tree.percentages1[i];
            P2[i] = tree.percentages2[i];
            P1[i] = 0.05;
            P2[i] = 0.05;
        }

        gama = 1;
        gradient_w = new double[tree.CLASS_COUNT][tree.ATTRIBUTE_COUNT];
        sum_grad_w = new double[tree.CLASS_COUNT][tree.ATTRIBUTE_COUNT];
        for(int i = 0; i < tree.CLASS_COUNT; i++){
            for(int j = 0; j < tree.ATTRIBUTE_COUNT; j++){
                gradient_w[i][j] = 0;
                sum_grad_w[i][j] = 0;
            }
        }
    }


//    Node(BTM tree, Scanner scanner, Node parent) {
//        this.tree = tree;
//
//        int t = scanner.nextInt();
//        if (t == -1)
//            this.parent = null;
//        else
//            this.parent = parent;
//
//        gama = scanner.nextDouble();
//        w0 = scanner.nextDouble();
//
//        w = new double[tree.ATTRIBUTE_COUNT];
//        for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++)
//            w[i] = scanner.nextDouble();
//
//        rho0 = new double[tree.CLASS_COUNT];
//        for (int i = 0; i < rho0.length; i++)
//            rho0[i] = scanner.nextDouble();
//
//        gradient_gama = scanner.nextDouble();
//        gradient_w0 = scanner.nextDouble();
//
//
//        sum_grad_w = new double[tree.ATTRIBUTE_COUNT];
//        for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++)
//            sum_grad_w[i] = scanner.nextDouble();
//
//        sum_grad_rho0 = new double[tree.CLASS_COUNT];
//        for (int i = 0; i < tree.CLASS_COUNT; i++)
//            sum_grad_rho0[i] = scanner.nextDouble();
//
//
//        y = new double[tree.CLASS_COUNT];
//        g = new double[tree.CLASS_COUNT];
//        delta = new double[tree.CLASS_COUNT];
//        Arrays.fill(y, 0);
//        Arrays.fill(g, 0);
//
//        gradient_rho0 = new double[tree.CLASS_COUNT];
//        gradient_w = new double[tree.ATTRIBUTE_COUNT];
//        Arrays.fill(gradient_w, 0);
//        Arrays.fill(gradient_rho0, 0);
//
//        if (t == 1 || t == -1) {
//            this.leftNode = new Node(tree, scanner, this);
//            this.rightNode = new Node(tree, scanner, this);
//        }
//    }


//    public double g(Instance instance) {
//        g = sigmoid(dotProduct(w, instance.x) + w0);
//        return g;
//    }


    public double[] g(Instance instance) {
        if (last_instance_id_g == instance.id) {
//            System.out.println("ggg");
            return g;
        }

        if (Runner.g_newversion) {
//            double sum1 = 0;
//            double sum2 = 0;
//            int lenght = instance.x.length;
//            for (int j = 0; j < lenght; j++) {
//                if (j < SetReader.tag_size) {
//                    sum1 += w[j] * instance.x[j];
//                } else {
//                    sum2 += w[j] * instance.x[j];
//                }
//            }
//            sum1 += w00;
//            sum2 += w01;
//            g1 = sigmoid(sum1);
//            g2 = sigmoid(sum2);
//            for (int i = 0; i < g.length; i++) {
//                if (P1[i] == 0 || P2[i] == 0)
//                    g[i] = (sigmoid(sum1) + sigmoid(sum2)) / 2;
//                else
////                    g[i] = (tree.percentages1[i] * sigmoid(sum1) + tree.percentages2[i] * sigmoid(sum2)) / (tree.percentages1[i] + tree.percentages2[i]);
//                    g[i] = (P1[i] * sigmoid(sum1) + P2[i] * sigmoid(sum2)) / (P1[i] + P2[i]);
//            }
        } else {
            for (int i = 0; i < g.length; i++)
                g[i] = sigmoid(dotProduct(w[i], instance.x) + w0[i]);;
        }
        last_instance_id_g = instance.id;

        return g;
    }


    public double[] F(Instance instance) {
        if (last_instance_id_y == instance.id) {
            System.out.println("yyy");
            return y;
        }

        if (leftNode == null) {
            for (int i = 0; i < y.length; i++) {
                y[i] = this.gama * (dotProduct(instance.x, rho[i]) + rho0[i]);
            }
        } else {
            leftNode.F(instance);
            rightNode.F(instance);
            for (int i = 0; i < y.length; i++) {
                g(instance);
                y[i] = (1 - gama) * ((g[i] * leftNode.y[i]) + ((1 - g[i]) * rightNode.y[i])) + gama * (dotProduct(instance.x, rho[i]) + rho0[i]);
            }
        }

        if (parent == null) {
            for (int i = 0; i < y.length; i++)
                y[i] = sigmoid(y[i]);
        }

        last_instance_id_y = instance.id;
        return y;
    }

    public double[] delta(Instance instance) {
        if (last_instance_id_delta == instance.id) {
            System.out.println("ddd");
            return delta;
        }

        if (this.parent == null) {
            for (int i = 0; i < delta.length; i++) {
                delta[i] = y[i] - instance.r[i];
            }
        } else {
            if (this == this.parent.leftNode) {
                for (int i = 0; i < delta.length; i++) {
                    delta[i] = parent.delta[i] * (1 - this.parent.gama) * this.parent.g[i];
                }
            } else {
                for (int i = 0; i < delta.length; i++) {
                    delta[i] = parent.delta[i] * (1 - this.parent.gama) * (1 - this.parent.g[i]);
                }
            }
        }

        last_instance_id_delta = instance.id;
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
        last_instance_id_delta = -1;
        last_instance_id_y = -1;
        last_instance_id_g = -1;

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
        delta(instance);
        double[] left_y;
        double[] right_y;

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

        gradient_w = new double[tree.CLASS_COUNT][tree.ATTRIBUTE_COUNT];
        for(int i = 0; i < tree.CLASS_COUNT; i++){
            for(int j = 0; j < tree.ATTRIBUTE_COUNT; j++){
                gradient_w[i][j] = 0;
            }
        }
//        for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++) {
//            for (int j = 0; j < tree.CLASS_COUNT; j++)
//                gradient_w[i] += delta[j] * (1 - gama) * g[j] * (1 - g[j]) * (left_y[j] - right_y[j]) * instance.x[i];
//            //gradient_w[i] /= tree.CLASS_COUNT;
//            // gradient_w[i] = delta[(int) instance.classValue] * (1 - gama) * g * (1 - g) * (left_y[(int) instance.classValue] - right_y[(int) instance.classValue]) * instance.attributes[i];
//        }

        for (int j = 0; j < tree.CLASS_COUNT; j++) {
            for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++) {
                if(Runner.g_newversion) {
//                    if (i < SetReader.tag_size)
//                        gradient_w[i] += delta[j] * (1 - gama) * g1 * (P1[j] / (P1[j] + P2[j])) * (1 - g1) * (left_y[j] - right_y[j]) * instance.x[i];
//                    else
//                        gradient_w[i] += delta[j] * (1 - gama) * g2 * (P2[j] / (P1[j] + P2[j])) * (1 - g2) * (left_y[j] - right_y[j]) * instance.x[i];
                }else{
                    gradient_w[j][i] = delta[j] * (1 - gama) * g[j] * (1 - g[j]) * (left_y[j] - right_y[j]) * instance.x[i];
                }
                //gradient_w[i] /= tree.CLASS_COUNT;
                // gradient_w[i] = delta[(int) instance.classValue] * (1 - gama) * g * (1 - g) * (left_y[(int) instance.classValue] - right_y[(int) instance.classValue]) * instance.attributes[i];
            }
        }




        gradient_P1 = new double[tree.CLASS_COUNT];
        Arrays.fill(gradient_P1, 0);
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_P1[i] += delta[i] * (1 - gama) * (left_y[i] - right_y[i]) * ((g1 * (P1[i] + P2[i]) - (P1[i] * g1 + P2[i] * g2)) / ((P1[i] + P2[i]) * (P1[i] + P2[i])));

        gradient_P2 = new double[tree.CLASS_COUNT];
        Arrays.fill(gradient_P2, 0);
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_P2[i] += delta[i] * (1 - gama) * (left_y[i] - right_y[i]) * ((g2 * (P1[i] + P2[i]) - (P1[i] * g1 + P2[i] * g2)) / ((P1[i] + P2[i]) * (P1[i] + P2[i])));


        gradient_w0 = new double[tree.CLASS_COUNT];
        Arrays.fill(gradient_w0, 0);
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_w0[i] = delta[i] * (1 - gama) * g[i] * (1 - g[i]) * (left_y[i] - right_y[i]);
        // gradient_w0 = delta[(int) instance.classValue] * (gama);
        // gradient_w0 /= tree.CLASS_COUNT;

        gradient_w00 = 0;
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_w00 += delta[i] * (1 - gama) * g1 * (P1[i]/(P1[i] + P2[i])) * (1 - g1) * (left_y[i] - right_y[i]);;

        gradient_w01 = 0;
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_w01 += delta[i] * (1 - gama) * g2 * (P2[i]/(P1[i] + P2[i])) * (1 - g2) * (left_y[i] - right_y[i]);


        gradient_rho = new double[tree.CLASS_COUNT][tree.ATTRIBUTE_COUNT];
        for (int i = 0; i < tree.CLASS_COUNT; i++) {
            for(int j = 0; j < tree.ATTRIBUTE_COUNT; j++){
                gradient_rho[i][j] = delta[i] * gama * instance.x[j];
            }
        }

        gradient_rho0 = new double[tree.CLASS_COUNT];
        for (int i = 0; i < tree.CLASS_COUNT; i++) {
            gradient_rho0[i] = delta[i] * gama;
        }

        gradient_gama = 0;
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_gama += delta[i] * ((-g[i] * left_y[i]) - (1 - g[i]) * right_y[i] + rho0[i]) - tree.LAMBDA;
        // gradient_gama /= tree.CLASS_COUNT;
        // gradient_gama = delta[(int) instance.classValue] * ((-g * left_y[(int) instance.classValue]) - (1 - g) * right_y[(int) instance.classValue] + rho0[(int) instance.classValue]) - tree.LAMBDA;

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

        for(int i = 0; i < tree.CLASS_COUNT; i++){
            for(int j = 0; j < tree.ATTRIBUTE_COUNT; j++){
                sum_grad_rho[i][j] += gradient_rho[i][j] * gradient_rho[i][j];
            }
        }
        for(int i = 0; i < tree.CLASS_COUNT; i++){
            for(int j = 0; j < tree.ATTRIBUTE_COUNT; j++){
                sum_grad_w[i][j] += gradient_w[i][j] * gradient_w[i][j];
            }
        }

        for (int i = 0; i < sum_grad_P1.length; i++) {
            sum_grad_P1[i] += gradient_P1[i] * gradient_P1[i];
        }

        for (int i = 0; i < sum_grad_P2.length; i++) {
            sum_grad_P2[i] += gradient_P2[i] * gradient_P2[i];
        }

        for (int i = 0; i < sum_grad_w0.length; i++) {
            sum_grad_w0[i] += gradient_w0[i] * gradient_w0[i];
        }


        sum_grad_w00 += gradient_w00 * gradient_w00;

        sum_grad_w01 += gradient_w01 * gradient_w01;

        for (int i = 0; i < sum_grad_rho0.length; i++) {
            sum_grad_rho0[i] += gradient_rho0[i] * gradient_rho0[i];
        }

        sum_grad_gama += gradient_gama * gradient_gama;

        for(int i = 0; i < tree.CLASS_COUNT; i++){
            for(int j = 0; j < tree.ATTRIBUTE_COUNT; j++){
                if(sum_grad_rho[i][j] != 0){
                    if(Runner.rho_newversion)
                        rho[i][j] = rho[i][j] - tree.LEARNING_RATE * gradient_rho[i][j] / Math.sqrt(sum_grad_rho[i][j]);
                }
            }
        }

        for (int i = 0; i < tree.CLASS_COUNT; i++) {
            for(int j = 0; j < tree.ATTRIBUTE_COUNT; j++){
                if (sum_grad_w[i][j] != 0)
                    w[i][j] = w[i][j] - tree.LEARNING_RATE * gradient_w[i][j] / Math.sqrt(sum_grad_w[i][j]);
            }
        }
        for (int i = 0; i < tree.CLASS_COUNT; i++) {
                if (sum_grad_w0[i] != 0)
                    w0[i] = w0[i] - tree.LEARNING_RATE * gradient_w0[i] / Math.sqrt(sum_grad_w0[i]);
        }

        if (sum_grad_w00 != 0)
            w00 = w00 - tree.LEARNING_RATE * gradient_w00 / Math.sqrt(sum_grad_w00);

        if (sum_grad_w01 != 0)
             w01 = w01 - tree.LEARNING_RATE * gradient_w01 / Math.sqrt(sum_grad_w01);

        for (int i = 0; i < sum_grad_P1.length; i++) {
            if (sum_grad_P1[i] != 0)
                 P1[i] = P1[i] - tree.LEARNING_RATE * gradient_P1[i] / Math.sqrt(sum_grad_P1[i]);
        }

        for (int i = 0; i < sum_grad_P2.length; i++) {
            if (sum_grad_P2[i] != 0)
                P2[i] = P2[i] - tree.LEARNING_RATE * gradient_P2[i] / Math.sqrt(sum_grad_P2[i]) / 100;
        }

        if (sum_grad_gama != 0)
            setGama(gama - tree.LEARNING_RATE * gradient_gama / Math.sqrt(sum_grad_gama));

        for (int i = 0; i < sum_grad_rho0.length; i++) {
            if (sum_grad_rho0[i] != 0)
                rho0[i] = rho0[i] - tree.LEARNING_RATE * gradient_rho0[i] / Math.sqrt(sum_grad_rho0[i]);
        }
    }

    void splitNode() {
        leftNode = new Node(tree);
        leftNode.parent = this;

        rightNode = new Node(tree);
        rightNode.parent = this;
    }

    public String toString(int tab) {
        String s = "";
        for (int i = 0; i < tab; i++) {
            s += "\t";
        }
        String info = "\n" + s;
        info += "gama: " + String.format("%.2f ",gama);
        info += "\n" + s;
        info += "g: " + String.format("%.2f ",g[0]) + "\n" + s + "y: ";
        for(int i = 0; i < g.length; i++){
            info += String.format("%.2f ",y[i]);
        }
        if (leftNode == null)
            s += "LEAF" + info;
        else {
            s += "NODE" + info + "\n";
            s += this.leftNode.toString(tab + 1) + "\n";
            s += this.rightNode.toString(tab + 1) + "\n";
        }
        return s;
    }

//    public void printToFile(BufferedWriter writer) throws IOException {
//        if (leftNode == null)
//            writer.write("0\n");
//        else if (parent == null)
//            writer.write("-1\n");
//        else
//            writer.write("1\n");
//
//
//        writer.write(gama + "\n");
//        writer.write(w0 + "\n");
//        for (double d : w)
//            writer.write(d + " ");
//        writer.write("\n");
//
//        for (double d : rho0)
//            writer.write(d + " ");
//
//        writer.write("\n");
//
//        writer.write(sum_grad_gama + "\n");
//        writer.write(sum_grad_w0 + "\n");
//        for (double d : sum_grad_w)
//            writer.write(d + " ");
//        writer.write("\n");
//
//        for (double d : sum_grad_rho0)
//            writer.write(d + " ");
//
//        writer.write("\n");
//
//        if (leftNode != null) {
//            leftNode.printToFile(writer);
//            rightNode.printToFile(writer);
//        }
//    }
}