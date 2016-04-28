package SDTMultiClass;

import BuddingTreeMultiClass.Instance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class SDTM {
    public Random random = new Random(31564);

    public double LEARNING_RATE;
    public int MAX_STEP;
    public int EPOCH;
    public int ATTRIBUTE_COUNT;
    public int CLASS_COUNT;
    public final int DEPTH;
    final ArrayList<Instance> X;
    final ArrayList<Instance> V;

    public Node ROOT;

    public SDTM(ArrayList<Instance> X, ArrayList<Instance> V, double learning_rate, int epoch, int depth) throws IOException {

        this.LEARNING_RATE = learning_rate;
        this.EPOCH = epoch;
        this.X = X;
        this.V = V;
        this.ATTRIBUTE_COUNT = X.get(0).x.length;
        this.CLASS_COUNT = X.get(0).r.length;
        this.DEPTH = depth;

        ROOT = new Node(this, 0, null);
        System.out.printf("Size: %d, Learning Rate: %.3f\n", size(), learning_rate);
    }

    public int size() {
        return ROOT.size();
    }

    public void learnTree() {
        for (int e = 0; e < EPOCH; e++) {
            Collections.shuffle(X, random);
            double i = 0;
            for (Instance instance : X) {
                //    System.out.printf("%d\n", (int) (((++i) / X.size()) * 100));
                instance.setY(ROOT.F(instance).clone());
                ROOT.learn(instance);
            }
            LEARNING_RATE *= 0.99;
            System.out.printf("Epoch: %d Training MAP: %.3f Validation MAP: %.3f\n", e, MAP_error(X).getAverageMAP(), MAP_error(V).getAverageMAP());
        }
    }


    public String getErrors() {
        return "Training \n" + MAP_error(X) + "\n\nValidation: \n" + MAP_error(V);
    }

    public Error MAP_error(ArrayList<Instance> A) {
        Error error2 = new Error(CLASS_COUNT, A.size());

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

    public String toString() {
        return ROOT.toString(1);
    }


}