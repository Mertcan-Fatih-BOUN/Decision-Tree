package BuddingTreeMultiClass;

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


    double[] rho;
    double[] w;
    double w0;
    double gama = 1;
    double[] y;
    double[] delta;
    double[] g;

    double[] sum_grad_rho;
    double[] sum_grad_w;
    double sum_grad_w0;


    double[] gradient_w;
    double[] gradient_rho;
    double gradient_w0 = 0;


    int last_instance_id_g = -1;
    int last_instance_id_y = -1;
    int last_instance_id_delta = -1;

    BTM tree;

    Node(BTM tree, Node parent) {
        this.tree = tree;
        this.parent = parent;
        w = new double[tree.ATTRIBUTE_COUNT];
        for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++)
            w[i] = rand(-0.01, 0.01, tree.random);

        w0 = rand(-0.01, 0.01,tree.random);


        rho = new double[tree.CLASS_COUNT];
        gradient_rho = new double[tree.CLASS_COUNT];
        sum_grad_rho = new double[tree.CLASS_COUNT];
        y = new double[tree.CLASS_COUNT];
        g = new double[tree.CLASS_COUNT];
        delta = new double[tree.CLASS_COUNT];
        Arrays.fill(y, 0);
        Arrays.fill(g, 0);
        Arrays.fill(gradient_rho, 0);
        Arrays.fill(sum_grad_rho, 0);

        for (int i = 0; i < rho.length; i++)
            rho[i] = rand(-0.01, 0.01,tree.random);

        gama = 1;

        if (parent != null)
            parent.gama = 0;

        gradient_w = new double[tree.ATTRIBUTE_COUNT];
        sum_grad_w = new double[tree.ATTRIBUTE_COUNT];
        Arrays.fill(gradient_w, 0);
        Arrays.fill(sum_grad_w, 0);
    }


    Node(BTM tree, Scanner scanner, Node parent) {
        this.tree = tree;

        int t = scanner.nextInt();
        if (t == -1)
            this.parent = null;
        else
            this.parent = parent;

        gama = scanner.nextDouble();
        w0 = scanner.nextDouble();

        w = new double[tree.ATTRIBUTE_COUNT];
        for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++)
            w[i] = scanner.nextDouble();

        rho = new double[tree.CLASS_COUNT];
        for (int i = 0; i < rho.length; i++)
            rho[i] = scanner.nextDouble();

        gradient_w0 = scanner.nextDouble();


        sum_grad_w = new double[tree.ATTRIBUTE_COUNT];
        for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++)
            sum_grad_w[i] = scanner.nextDouble();

        sum_grad_rho = new double[tree.CLASS_COUNT];
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            sum_grad_rho[i] = scanner.nextDouble();


        y = new double[tree.CLASS_COUNT];
        g = new double[tree.CLASS_COUNT];
        delta = new double[tree.CLASS_COUNT];
        Arrays.fill(y, 0);
        Arrays.fill(g, 0);

        gradient_rho = new double[tree.CLASS_COUNT];
        gradient_w = new double[tree.ATTRIBUTE_COUNT];
        Arrays.fill(gradient_w, 0);
        Arrays.fill(gradient_rho, 0);

        if (t == 1 || t == -1) {
            this.leftNode = new Node(tree, scanner, this);
            this.rightNode = new Node(tree, scanner, this);
        }
    }


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
            double sum1 = 0;
            double sum2 = 0;
            int lenght = instance.x.length;
            for (int j = 0; j < lenght; j++) {
                if (j < SetReader.tag_size) {
                    sum1 += w[j] * instance.x[j];
                } else {
                    sum2 += w[j] * instance.x[j];
                }
            }
            for (int i = 0; i < g.length; i++) {
                if (tree.percentages1[i] == 0 || tree.percentages2[i] == 0)
                    g[i] = (sigmoid(sum1) + sigmoid(sum2)) / 2;
                else
                    g[i] = (tree.percentages1[i] * sigmoid(sum1) + tree.percentages2[i] * sigmoid(sum2)) / (tree.percentages1[i] + tree.percentages2[i]);
            }
        } else {
            double gg = sigmoid(dotProduct(w, instance.x) + w0);
            for (int i = 0; i < g.length; i++)
                g[i] = gg;
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
                y[i] = this.gama * rho[i];
            }
        } else {
            leftNode.F(instance);
            rightNode.F(instance);
            for (int i = 0; i < y.length; i++) {
                g(instance);
                y[i] = (1 - gama) * ((g[i] * leftNode.y[i]) + ((1 - g[i]) * rightNode.y[i])) + gama * rho[i];
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

    public void update(Instance instance) {
        learnParameters();
        last_instance_id_delta = -1;
        last_instance_id_y = -1;
        last_instance_id_g = -1;

        if (leftNode != null) {
            leftNode.update(instance);
            rightNode.update(instance);
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

        gradient_w = new double[tree.ATTRIBUTE_COUNT];
        Arrays.fill(gradient_w, 0);
        for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++) {
            for (int j = 0; j < tree.CLASS_COUNT; j++)
                gradient_w[i] += delta[j] * (1 - gama) * g[j] * (1 - g[j]) * (left_y[j] - right_y[j]) * instance.x[i];
            //gradient_w[i] /= tree.CLASS_COUNT;
            // gradient_w[i] = delta[(int) instance.classValue] * (1 - gama) * g * (1 - g) * (left_y[(int) instance.classValue] - right_y[(int) instance.classValue]) * instance.attributes[i];
        }

        gradient_w0 = 0;
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_w0 += delta[i] * (1 - gama) * g[i] * (1 - g[i]) * (left_y[i] - right_y[i]);
//         gradient_w0 = delta[i] * (gama);
        // gradient_w0 /= tree.CLASS_COUNT;

        gradient_rho = new double[tree.CLASS_COUNT];
        for (int i = 0; i < tree.CLASS_COUNT; i++) {
            gradient_rho[i] = delta[i] * gama;
        }


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



        for (int i = 0; i < sum_grad_w.length; i++) {
            if (sum_grad_w[i] == 0)
                sum_grad_w[i] = Double.MIN_VALUE;
            w[i] = w[i] - tree.LEARNING_RATE * gradient_w[i] / Math.sqrt(sum_grad_w[i]);
        }
        if (sum_grad_w0 == 0)
            sum_grad_w0 = Double.MIN_VALUE;
        w0 = w0 - tree.LEARNING_RATE * gradient_w0 / Math.sqrt(sum_grad_w0);



        for (int i = 0; i < sum_grad_rho.length; i++) {
            if (sum_grad_rho[i] == 0)
                sum_grad_rho[i] = Double.MIN_VALUE;
            rho[i] = rho[i] - tree.LEARNING_RATE * gradient_rho[i] / Math.sqrt(sum_grad_rho[i]);
        }
    }

    public String toString(int tab) {
        String s = "";
        for (int i = 0; i < tab; i++) {
            s += "\t";
        }
        String info = "\n" + s;
        info += "gama: " + String.format("%.2f ", gama);
        info += "\n" + s;
        info += "g: " + String.format("%.2f ", g[0]) + "\n" + s + "y: ";
        for (int i = 0; i < g.length; i++) {
            info += String.format("%.2f ", y[i]);
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

    public void printToFile(BufferedWriter writer) throws IOException {
        if (leftNode == null)
            writer.write("0\n");
        else if (parent == null)
            writer.write("-1\n");
        else
            writer.write("1\n");


        writer.write(gama + "\n");
        writer.write(w0 + "\n");
        for (double d : w)
            writer.write(d + " ");
        writer.write("\n");

        for (double d : rho)
            writer.write(d + " ");

        writer.write("\n");

        writer.write(sum_grad_w0 + "\n");
        for (double d : sum_grad_w)
            writer.write(d + " ");
        writer.write("\n");

        for (double d : sum_grad_rho)
            writer.write(d + " ");

        writer.write("\n");

        if (leftNode != null) {
            leftNode.printToFile(writer);
            rightNode.printToFile(writer);
        }
    }

    public void increase_depth() {
        if (leftNode == null)
            leftNode = new Node(tree,this);
        else
            leftNode.increase_depth();

        if (rightNode == null)
            rightNode = new Node(tree,this);
        else
            rightNode.increase_depth();
    }
}