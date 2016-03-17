package BuddingTreeMultiClass;


import java.io.IOException;
import java.util.ArrayList;
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
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < X.size(); i++) indices.add(i);

        for (int e = 0; e < EPOCH; e++) {
            Collections.shuffle(indices);
            for (int i = 0; i < X.size(); i++) {
                int j = indices.get(i);
                ROOT.backPropagate(X.get(j));
                ROOT.update();
            }
            LEARNING_RATE *= 0.99;
            System.out.println("Epoch :" + e + "\nSize: " + size() + "\n" + getErrors() + "\n-----------------------\n");
        }
    }


    public String getErrors() {
        return "Training \n" + MAP_error(X) + "\n\nValidation: \n" +  MAP_error(V);
    }


    public int[] eval(Instance instance) {
        double[] y = ROOT.F(instance);
        int[] ret = new int[y.length];
        for (int i = 0; i < ret.length; i++) {
            if (y[i] > 0.5)
                ret[i] = 1;
            else
                ret[i] = 0;
        }
        return ret;
    }

    public Error2 MAP_error(ArrayList<Instance> V) {
        Error2 error2 = new Error2(CLASS_COUNT, V.size());

        for (Instance instance : V) {
            instance.y = ROOT.F(instance);
        }

        for (int i = 0; i < CLASS_COUNT; i++) {
            double error = 0;
            double positive_count = 0;
            final int finalI = i;
            Collections.sort(V, (o1, o2) -> (int) (o1.y[finalI] - o2.y[finalI]));
            for (int j = 0; j < V.size(); j++) {
                if (V.get(j).r[i] == 1) {
                    positive_count++;
                    error += (positive_count * 1.0) / (j + 1);
                }
            }

            error /= positive_count;

            error2.MAP[i] = error;
        }

        return error2;
    }

    public Error ErrorOfTree11(ArrayList<Instance> V) {
        Error error = new Error(CLASS_COUNT, V.size());
        for (Instance instance : V) {
            double[] y = ROOT.F(instance);
            int[] y_class = eval(instance);
            int[] r = instance.r;
            boolean catched = false;
            for (int i = 0; i < y.length; i++) {
                error.addClassification(y_class[i], r[i], i);
                if (y_class[i] != r[i] && !catched) {
                    catched = true;
                    error.addClassicMissClass();
                }
            }
        }
        return error;
    }

}