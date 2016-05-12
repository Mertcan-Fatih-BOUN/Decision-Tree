package BuddingTreeMultiClass;


import SDTMultiClass.*;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;


public class BTM {
    public double LEARNING_RATE;
    final public int EPOCH;
    final public int ATTRIBUTE_COUNT;
    final public int CLASS_COUNT;
    final public double LAMBDA;
    public int DEPTH;
    final ArrayList<Instance> X;
    final ArrayList<Instance> V;
    Random random = new Random(465643);

    Instance LAST;
    //1 only tags, 2 only image
    public double[] percentages1 = new double[]{0.565, 0.329, 0.567, 0.533, 0.680, 0.663, 0.510, 0.603, 0.418, 0.562, 0.549, 0.520, 0.507, 0.907, 0.654, 0.487, 0.596, 0.620, 0.691, 0.684, 0.607, 0.531, 0.603, 0.629
            , 0.694, 0.366, 0.534, 0.679, 0.724, 0.467, 0.737, 0.476, 0.622, 0.574, 0.488, 0.489, 0.565, 0.545, 0.000};
    public double[] percentages2 = new double[]{0.533, 0.447, 0.708, 0.744, 0.926, 0.701, 0.911, 0.684, 0.64, 0.966, 0.678, 0.932, 0.811, 0.951, 0.926, 0.949, 0.683, 0.85, 0.946, 0.947, 0.815, 0.685, 0.928, 0.743, 0.667, 0.62, 0.828, 0.905, 0.786, 0.944, 0.843, 0.958, 0.88, 0.952, 0.957, 0.659, 0.756, 0.755, 0.328};


    public static Node ROOT;

    /**
     * @param X             Training Set
     * @param V             Validation Set
     * @param learning_rate Learning Rate
     * @param epoch         Epoch
     * @param lambda        in general 0.01 is optimum. make it lower if you wish to increase the tree size and vice versa.
     * @throws IOException
     */
    public BTM(ArrayList<Instance> X, ArrayList<Instance> V, double learning_rate, int epoch, double lambda, int depth) {
        this.LEARNING_RATE = learning_rate;
        this.EPOCH = epoch;
        this.X = X;
        this.V = V;
        this.LAMBDA = lambda;
        this.ATTRIBUTE_COUNT = X.get(0).x.length;
        this.CLASS_COUNT = X.get(0).r.length;
        this.DEPTH = depth;
        ROOT = new Node(this, null);
    }

    public int size() {
        return ROOT.size();
    }

    public int eff_size() {
        return ROOT.myEffSize();
    }

    public void learnTree() throws IOException {
        double preMAP = 0;
        for (int e = 0; e < EPOCH; e++) {
            Collections.shuffle(X, random);
            LAST = X.get(X.size() - 1);
            for (Instance instance : X) {
                ROOT.F(instance);
                ROOT.backPropagate(instance);
                ROOT.update(instance);
            }

            Error2 errorV = MAP_error(V);
            Error2 errorX = MAP_error(X);
            double avXmap = errorX.getAverageMAP();
            double avXprec = errorX.getAveragePrec();
            double avVmap = errorV.getAverageMAP();
            double avVprec = errorV.getAveragePrec();

            System.out.printf("%2d %3d %.3f %.3f %.3f %.3f", e, size(), avXmap, avXprec, avVmap, avVprec);
            for (double d : errorX.MAP)
                System.out.printf(" %.3f", d);
            for (double d : errorX.precision)
                System.out.printf(" %.3f", d);
            for (double d : errorV.MAP)
                System.out.printf(" %.3f", d);
            for (double d : errorV.precision)
                System.out.printf(" %.3f", d);
            System.out.printf("\n");

            if (avVmap <= preMAP) {
                ROOT.increase_depth();
                preMAP = 0;
            }
            else
                preMAP = avVmap;
        }
    }


    public String getErrors() {
        return "Training \n" + MAP_error(X) + "\n\nValidation: \n" + MAP_error(V);
    }

    public void followInstance(Instance i) {
        double[] y = ROOT.F(i).clone();
        for (int t = 0; t < i.r.length; t++) {
            System.out.print(i.r[t] + " ");
        }
        System.out.println();
        System.out.println(ROOT.toString(0) + "\n\n");
    }


    public Error2 MAP_error(ArrayList<Instance> A) {
        Error2 error2 = new Error2(CLASS_COUNT, A.size());

        for (Instance instance : A) {
            instance.y = ROOT.F(instance).clone();
        }

        for (int i = 0; i < CLASS_COUNT; i++) {
            double error = 0;
            double positive_count = 0;
            double pre_count = 0;
            final int finalI = i;
            Collections.sort(A, (o1, o2) -> Double.compare(o2.y[finalI], o1.y[finalI]));

            for (int j = 0; j < A.size(); j++) {
                if (A.get(j).r[i] == 1) {
                    if (j < 50)
                        pre_count++;
                    positive_count++;
                    error += (positive_count * 1.0) / (j + 1);
                }
            }

            error /= positive_count;

            error2.MAP[i] = error;
            error2.precision[i] = pre_count / 50.0f;
        }

        return error2;
    }

    public void printToFile(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write(LEARNING_RATE + "\n");
        writer.write(LAMBDA + "\n");
        writer.write(CLASS_COUNT + "\n");
        writer.write(ATTRIBUTE_COUNT + "\n");
        ROOT.printToFile(writer);
        writer.flush();
        writer.close();
    }

    public BTM(ArrayList<Instance> X, ArrayList<Instance> V, String filename, int epoch) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileReader(filename));
        this.LEARNING_RATE = scanner.nextDouble();
        this.LAMBDA = scanner.nextDouble();
        this.CLASS_COUNT = scanner.nextInt();
        this.ATTRIBUTE_COUNT = scanner.nextInt();
        this.EPOCH = epoch;
        this.X = X;
        this.V = V;

        ROOT = new Node(this, scanner, null);
    }
}