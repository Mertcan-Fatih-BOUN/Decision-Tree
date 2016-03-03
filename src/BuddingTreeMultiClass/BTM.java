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
            System.out.println("Epoch :" + e + "\t C-E Training :" + ErrorOfTree(X).getClassficationError() + "\t C-E Validation :" + ErrorOfTree(V).getClassficationError());
        }
    }


    public String getErrors() {
        return "Test \n" + ErrorOfTree(X).toString() + "\n\n Validation: \n" + ErrorOfTree(V).toString();
    }


    public int[] eval(Instance instance) {
        double[] y = ROOT.F(instance);
        int[] ret = new int[y.length];
        for (int i = 0; i < ret.length; i++) {
            if (ret[i] > 0.5)
                y[i] = 1;
            else
                y[i] = 0;
        }
        return ret;
    }


    public Error ErrorOfTree(ArrayList<Instance> V) {
        Error error = new Error(CLASS_COUNT, V.size());
        for (Instance instance : V) {
            double[] y = ROOT.F(instance);
            int[] y_class = eval(instance);
            int[] r = instance.r;
            for (int i = 0; i < y.length; i++) {
                if (r[i] != 0 && y[i] != 0)
                    error.addCrossEntropy(r[i] * Math.log(y[i]));
                error.addClassification(y_class[i], r[i], i);
            }
        }
        return error;
    }

}