package BuddingTreeMultiClass;


import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class BTM {
    public double LEARNING_RATE;
    final public int EPOCH;
    final public int ATTRIBUTE_COUNT;
    final public int CLASS_COUNT;
    final public double LAMBDA;

    final ArrayList<Instance> X;
    final ArrayList<Instance> V;

    public static Node ROOT;

    /**
     * @param X             Training Set
     * @param V             Validation Set
     * @param learning_rate Learning Rate
     * @param epoch         Epoch
     * @param lambda        in general 0.01 is optimum. make it lower if you wish to increase the tree size and vice versa.
     * @throws IOException
     */
    public BTM(ArrayList<Instance> X, ArrayList<Instance> V, double learning_rate, int epoch, double lambda) {
        this.LEARNING_RATE = learning_rate;
        this.EPOCH = epoch;
        this.X = X;
        this.V = V;
        this.LAMBDA = lambda;
        this.ATTRIBUTE_COUNT = X.get(0).x.length;
        this.CLASS_COUNT = X.get(0).r.length;

        ROOT = new Node(this);
    }

    public int size() {
        return ROOT.size();
    }

    public void learnTree() throws IOException {

        for (int e = 0; e < EPOCH; e++) {
            Collections.shuffle(X);
            for (Instance instance : X) {
                ROOT.F(instance);
                ROOT.backPropagate(instance);
                ROOT.update();
            }
            LEARNING_RATE *= 0.99;
            System.out.println("Epoch :" + e + "\nSize: " + size() + "\n" + getErrors() + "\n-----------------------\n");
        }
    }


    public String getErrors() {
        return "Training \n" + MAP_error(X) + "\n\nValidation: \n" + MAP_error(V);
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

}