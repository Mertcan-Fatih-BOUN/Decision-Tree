package BuddingTreeMultiClass;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static misc.Util.*;

@SuppressWarnings("Duplicates")
public class Node {

    Node parent = null;
    Node leftNode = null;
    Node rightNode = null;


    int[] scaled_rho;
    int[] scaled_rho_within;
    int[] scaled_rho_gama;
    int[] scaled_rho_gama_within;

    public double min_rho = 10000;
    public double max_rho = -10000;

    public double min_rho_times_gama = 10000;
    public double max_rho_times_gama = -10000;

    public double a1;
    public double b1;
    public double a2;
    public double b2;

    double[] rho;
    double[] w;
    double w0;
    public double gama = 1;
    double[] y;

    double[] y_means;

    double[] delta;
    double[] g;
    double[] cumulative_g;
    double current_cumulative_g;

    double[] sum_grad_rho;
    double[] sum_grad_w;
    double sum_grad_w0;
    double sum_grad_gama;

    double[] gradient_w;
    double[] gradient_rho;
    double gradient_w0 = 0;
    double gradient_gama = 0;

    int last_instance_id_g = -1;
    int last_instance_id_y = -1;
    int last_instance_id_delta = -1;

    int[] min_diff_indexes = new int[Runner.similar_count];
    int[] max_diff_indexes = new int[Runner.similar_count];

    BTM tree;

    Node(BTM tree) {
        this.tree = tree;

        w = new double[tree.ATTRIBUTE_COUNT];
        for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++)
            w[i] = rand(-0.01, 0.01);

        w0 = rand(-0.01, 0.01);

        scaled_rho = new int[tree.CLASS_COUNT];
        scaled_rho_within = new int[tree.CLASS_COUNT];
        scaled_rho_gama = new int[tree.CLASS_COUNT];
        scaled_rho_gama_within = new int[tree.CLASS_COUNT];

        rho = new double[tree.CLASS_COUNT];
        gradient_rho = new double[tree.CLASS_COUNT];
        sum_grad_rho = new double[tree.CLASS_COUNT];
        y = new double[tree.CLASS_COUNT];
        y_means = new double[tree.CLASS_COUNT];
        g = new double[tree.CLASS_COUNT];
        cumulative_g = new double[tree.CLASS_COUNT];
        delta = new double[tree.CLASS_COUNT];
        Arrays.fill(y, 0);
        Arrays.fill(g, 0);
        Arrays.fill(gradient_rho, 0);
        Arrays.fill(sum_grad_rho, 0);

        for (int i = 0; i < rho.length; i++)
            rho[i] = rand(-0.01, 0.01);

        gama = 1;
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

        cumulative_g = new double[tree.CLASS_COUNT];
        scaled_rho = new int[tree.CLASS_COUNT];
        scaled_rho_within = new int[tree.CLASS_COUNT];
        scaled_rho_gama = new int[tree.CLASS_COUNT];
        scaled_rho_gama_within = new int[tree.CLASS_COUNT];

        rho = new double[tree.CLASS_COUNT];
        for (int i = 0; i < rho.length; i++)
            rho[i] = scanner.nextDouble();

        gradient_gama = scanner.nextDouble();
        gradient_w0 = scanner.nextDouble();


        sum_grad_w = new double[tree.ATTRIBUTE_COUNT];
        for (int i = 0; i < tree.ATTRIBUTE_COUNT; i++)
            sum_grad_w[i] = scanner.nextDouble();

        sum_grad_rho = new double[tree.CLASS_COUNT];
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            sum_grad_rho[i] = scanner.nextDouble();


        y = new double[tree.CLASS_COUNT];
        y_means = new double[tree.CLASS_COUNT];
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

//        if (parent == null) {
//            for (int i = 0; i < y.length; i++)
//                y[i] = sigmoid(y[i]);
//        }

        last_instance_id_y = instance.id;

        for (int i = 0; i < tree.CLASS_COUNT; i++) {
            y_means[i] += y[i] / tree.X.size();
        }

        return y;
    }

    public void update_ymeans(int size) {
        for (int i = 0; i < tree.CLASS_COUNT; i++) {
            y_means[i] += gama * rho[i] / size;
//            if(tree.LAST.r[18] == 1)
//                y_means[i] += y[i] / size;
        }
        if (leftNode != null) {
            leftNode.update_ymeans(size);
            rightNode.update_ymeans(size);
        }
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

        if (gama < 1 && leftNode == null
//                && tree.LAST == instance
                ) {
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

        gradient_gama = 0;
        for (int i = 0; i < tree.CLASS_COUNT; i++)
            gradient_gama += delta[i] * ((-g[i] * left_y[i]) - (1 - g[i]) * right_y[i] + rho[i]) - tree.LAMBDA;
        // gradient_gama /= tree.CLASS_COUNT;
        // gradient_gama = delta[(int) instance.classValue] * ((-g * left_y[(int) instance.classValue]) - (1 - g) * right_y[(int) instance.classValue] + rho[(int) instance.classValue]) - tree.LAMBDA;

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
            if (sum_grad_w[i] != 0)
                w[i] = w[i] - tree.LEARNING_RATE * gradient_w[i] / Math.sqrt(sum_grad_w[i]);
        }
        if (sum_grad_w0 != 0)
            w0 = w0 - tree.LEARNING_RATE * gradient_w0 / Math.sqrt(sum_grad_w0);

        if (sum_grad_gama != 0)
            setGama(gama - tree.LEARNING_RATE * gradient_gama / Math.sqrt(sum_grad_gama));

        for (int i = 0; i < sum_grad_rho.length; i++) {
            if (sum_grad_rho[i] != 0)
                rho[i] = rho[i] - tree.LEARNING_RATE * gradient_rho[i] / Math.sqrt(sum_grad_rho[i]);
        }
    }

    void splitNode() {
        leftNode = new Node(tree);
        leftNode.parent = this;

        rightNode = new Node(tree);
        rightNode.parent = this;
//        System.out.println(tree.size());
    }


    public double cumulative_g(Instance instance) {
        if (this.parent == null) {
            current_cumulative_g = 1;
        } else {
            if (this == this.parent.leftNode) {
                current_cumulative_g = parent.current_cumulative_g * (parent.g(instance)[0]);
            } else {
                current_cumulative_g = parent.current_cumulative_g * (1 - parent.g(instance)[0]);
            }
        }
        for (int i = 0; i < cumulative_g.length; i++) {
            if (instance.r[i] == 1)
                cumulative_g[i] += current_cumulative_g;
        }
        if (leftNode != null) {
            leftNode.cumulative_g(instance);
            rightNode.cumulative_g(instance);
        }
        return current_cumulative_g;
    }

    public void findAllMinMaxDifferences(ArrayList<Instance> X, TreeNode treeNode) {
        treeNode.node = this;
        if (this.gama< 0.99) {
            min_diff_indexes = minDifferences(X);
            max_diff_indexes = maxDifferences(X);

            treeNode.leftTreeNode = new TreeNode();
            for (int i = 0; i < max_diff_indexes.length; i++)
                treeNode.leftTreeNode.instances[i] = X.get(max_diff_indexes[i]);

            treeNode.rightTreeNode = new TreeNode();
            for (int i = 0; i < min_diff_indexes.length; i++)
                treeNode.rightTreeNode.instances[i] = X.get(min_diff_indexes[i]);

            if (this.leftNode != null) {
                leftNode.findAllMinMaxDifferences(X, treeNode.rightTreeNode);
                rightNode.findAllMinMaxDifferences(X, treeNode.leftTreeNode);
            }
        }
    }

    public int[] minDifferences(ArrayList<Instance> X) {
        int count = Runner.similar_count;
        int[] minDiffIndex = new int[count];
        double[] minDifferences = new double[count];
        for (int i = 0; i < count; i++) {
//            double diff = difference((X.get(0).x));
            double diff = dotProduct(w, (X.get(0).x));
            minDifferences[i] = diff;
            minDiffIndex[i] = 0;
        }

        for (int i = 1; i < X.size(); i++) {
//            double diff = difference(X.get(i).x);
            double diff = dotProduct(w, (X.get(i).x));
            for (int j = 0; j < count; j++) {
                if (diff < minDifferences[j]) {
                    for (int t = count - 1; t > j; t--) {
                        minDifferences[t] = minDifferences[t - 1];
                        minDiffIndex[t] = minDiffIndex[t - 1];
                    }
                    minDifferences[j] = diff;
                    minDiffIndex[j] = i;
                    break;
                }
            }
        }

//        String s = "";
//        for(int i = 0; i < count; i++){
//            s += minDifferences[i] + " " + minDiffIndex[i] + "\n";
//        }
//        System.out.println(s);

        return minDiffIndex;
    }

    public int[] maxDifferences(ArrayList<Instance> X) {
        int count = Runner.similar_count;
        int[] maxDiffIndex = new int[count];
        double[] maxDifferences = new double[count];
        for (int i = 0; i < count; i++) {
//            double diff = difference((X.get(0).x));
            double diff = dotProduct(w, (X.get(0).x));
            maxDifferences[i] = diff;
            maxDiffIndex[i] = 0;
        }

        for (int i = 1; i < X.size(); i++) {
//            double diff = difference(X.get(i).x);
            double diff = dotProduct(w, (X.get(i).x));
            for (int j = 0; j < count; j++) {
                if (diff > maxDifferences[j]) {
                    for (int t = count - 1; t > j; t--) {
                        maxDifferences[t] = maxDifferences[t - 1];
                        maxDiffIndex[t] = maxDiffIndex[t - 1];
                    }
                    maxDifferences[j] = diff;
                    maxDiffIndex[j] = i;
                    break;
                }
            }
        }

//        String s = "";
//        for(int i = 0; i < count; i++){
//            s += minDifferences[i] + " " + minDiffIndex[i] + "\n";
//        }
//        System.out.println(s);

        return maxDiffIndex;
    }

    public double difference(double[] x) {
        double diff = 0;
        for (int i = 0; i < w.length; i++) {
            diff += Math.pow((Math.abs(w[i] - x[i])), 2);
        }
        diff = Math.sqrt(diff);
        return diff;
    }

    public void findScaledRhos() {
        for (int i = 0; i < rho.length; i++) {
            scaled_rho[i] = (int) (tree.a1 * rho[i] + tree.b1);
            scaled_rho_gama[i] = (int) (tree.a2 * (rho[i] * gama) + tree.b2);

            scaled_rho_within[i] = (int) (this.a1 * rho[i] + this.b1);
            scaled_rho_gama_within[i] = (int) (this.a2 * (rho[i] * gama) + this.b2);
        }
        if (leftNode != null) {
            leftNode.findScaledRhos();
            rightNode.findScaledRhos();
        }
    }

    public void findMinMaxRho() {
        for (int i = 0; i < rho.length; i++) {
            if (rho[i] < tree.min_rho) {
                tree.min_rho = rho[i];
            }
            if (rho[i] > tree.max_rho) {
                tree.max_rho = rho[i];
            }
            if (rho[i] * gama < tree.min_rho_times_gama) {
                tree.min_rho_times_gama = rho[i] * gama;
            }
            if (rho[i] * gama > tree.max_rho_times_gama) {
                tree.max_rho_times_gama = rho[i] * gama;
            }

            if (rho[i] < this.min_rho) {
                this.min_rho = rho[i];
            }
            if (rho[i] > this.max_rho) {
                this.max_rho = rho[i];
            }
            if (rho[i] * gama < this.min_rho_times_gama) {
                this.min_rho_times_gama = rho[i] * gama;
            }
            if (rho[i] * gama > this.max_rho_times_gama) {
                this.max_rho_times_gama = rho[i] * gama;
            }
        }
        this.a1 = 255 / (this.max_rho - this.min_rho);
        this.b1 = -this.min_rho * this.a1;
        this.a2 = 255 / (this.max_rho_times_gama - this.min_rho_times_gama);
        this.b2 = -this.min_rho_times_gama * this.a2;

        if (leftNode != null) {
            leftNode.findMinMaxRho();
            rightNode.findMinMaxRho();
        }
    }

    public int[] toGrayScale(double[] array) {
        int[] grayScale = new int[array.length];
        double max = array[0];
        double min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (max < array[i]) {
                max = array[i];
            }
            if (min > array[i]) {
                min = array[i];
            }
        }
        double a1 = 255 / (max - min);
        double b1 = -min * a1;
        for (int i = 0; i < array.length; i++) {
            grayScale[i] = (int) (a1 * array[i] + b1);
        }
        return grayScale;
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

        info += "\n" + s + "y means: ";

        for (int i = 0; i < g.length; i++) {
            info += String.format("%.2f ", y_means[i]);
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

    public String toStringIndexesAndRhos(int tab, ArrayList<Instance> X) {
        String s = "";
        for (int i = 0; i < tab; i++) {
            s += "\t";
        }
        String info = "\n" + s;
        for (int i = 0; i < min_diff_indexes.length; i++) {
            info += String.format("%d ", min_diff_indexes[i]);
        }

        info += "\n" + s + "index_classes min:\n" + s;

        for (int i = 0; i < Runner.similar_count; i++) {
            for (int j = 0; j < rho.length; j++) {
                info += String.format("%d ", X.get(min_diff_indexes[i]).r[j]);
            }
            info += "\n" + s;
        }

        info += "\n" + s;
        for (int i = 0; i < max_diff_indexes.length; i++) {
            info += String.format("%d ", max_diff_indexes[i]);
        }

        info += "\n" + s + "index_classes max:\n" + s;

        for (int i = 0; i < Runner.similar_count; i++) {
            for (int j = 0; j < rho.length; j++) {
                info += String.format("%d ", X.get(max_diff_indexes[i]).r[j]);
            }
            info += "\n" + s;
        }

        info += "\n" + s + "cumulative_g: ";

        int[] gray_scale = toGrayScale(cumulative_g);

        for (int i = 0; i < rho.length; i++) {
            info += String.format("%d ", gray_scale[i]);
        }

        info += "\n" + s + "rho_scaled_within: ";

        for (int i = 0; i < rho.length; i++) {
            info += String.format("%d ", scaled_rho_within[i]);
        }

        info += "\n" + s + "rho_scaled: ";

        for (int i = 0; i < rho.length; i++) {
            info += String.format("%d ", scaled_rho[i]);
        }

        info += "\n" + s + "rho_scaled_gama: ";

        for (int i = 0; i < rho.length; i++) {
            info += String.format("%d ", scaled_rho_gama[i]);
        }

        if (leftNode == null)
            s += "LEAF" + info;
        else {
            s += "NODE" + info + "\n";
            s += this.leftNode.toStringIndexesAndRhos(tab + 1, X) + "\n";
            s += this.rightNode.toStringIndexesAndRhos(tab + 1, X) + "\n";
        }
        return s;
    }

    public String toStringy_means(int tab) {
        String s = "";
        for (int i = 0; i < tab; i++) {
            s += "\t";
        }
        double max1 = y_means[0];
        double max2 = y_means[0];
        double max3 = y_means[0];
        int max1index = 0;
        int max2index = 0;
        int max3index = 0;
        for (int i = 1; i < y_means.length; i++) {
            if (y_means[i] > max1) {
                max3 = max2;
                max2 = max1;
                max1 = y_means[i];

                max3index = max2index;
                max2index = max1index;
                max1index = i;
            } else if (y_means[i] > max2) {
                max3 = max2;
                max2 = y_means[i];

                max3index = max2index;
                max2index = i;
            } else if (y_means[i] > max3) {
                max3 = y_means[i];
                max3index = i;
            }
        }


        String info = "\n" + s + "gama: " + gama + " max3 classes: " + max1index + " " + max2index + " " + max3index + " y_means: ";
        for (int i = 0; i < g.length; i++) {
            info += String.format("%.2f ", y_means[i]);
        }

        if (leftNode == null)
            s += "LEAF" + info;
        else {
            s += "NODE" + info + "\n";
            s += this.leftNode.toStringy_means(tab + 1) + "\n";
            s += this.rightNode.toStringy_means(tab + 1) + "\n";
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

        writer.write(sum_grad_gama + "\n");
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

    public String toStringWeights() {
        String s = w[0] + "";
        for (int i = 1; i < w.length; i++) {
            s += " " + w[i];
        }
        return s;
    }
}