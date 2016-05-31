package BuddingTreeMultiClass;

import BuddingTreeMultiClass.readers.DataSet;
import BuddingTreeMultiClass.readers.FlickerDataSet;
import BuddingTreeMultiClass.readers.Instance;

import java.io.*;
import java.util.*;

import static misc.Util.argMax;
import static misc.Util.sigmoid;
import static misc.Util.softmax;

public class BTM {
    final public int ATTRIBUTE_COUNT;
    final public int CLASS_COUNT;

    public double LEARNING_RATE;
    public int EPOCH;
    public double LEARNING_RATE_DECAY;
    public double LAMBDA;

    final ArrayList<Instance> X;
    final ArrayList<Instance> V;
    final DataSet dataSet;

    Instance LAST;

    public Node ROOT;
    final DataSet.TYPE type;


    /**
     * @param dataSet dataset
     * @throws IOException
     */
    public BTM(DataSet dataSet) {
        this.dataSet = dataSet;
        this.type = dataSet.type;
        this.X = dataSet.TRAINING_INSTANCES;
        this.V = dataSet.VALIDATION_INSTANCES;
        this.ATTRIBUTE_COUNT = X.get(0).x.length;
        this.CLASS_COUNT = X.get(0).r.length;

        ROOT = new Node(this);
    }

    public int size() {
        return ROOT.size();
    }

    public int eff_size() {
        return ROOT.myEffSize();
    }

    public void learnTree(double learning_rate, int epoch, double lambda, double learning_rate_decay) throws IOException {
        this.LEARNING_RATE = learning_rate;

        this.EPOCH = epoch;
        this.LAMBDA = lambda;
        this.LEARNING_RATE_DECAY = learning_rate_decay;


        for (int e = 0; e <= EPOCH; e++) {
            if (this.type == DataSet.TYPE.MULTI_LABEL_CLASSIFICATION)
                System.out.println("Epoch :" + e + "\nSize: " + size() + " " + eff_size() + "\n" + getMAP_P50_error(X) + "\n" + getMAP_P50_error(V) + "\nEpoch :" + e + "\n-----------------------\n");
            else if (this.type == DataSet.TYPE.MULTI_CLASS_CLASSIFICATION || this.type == DataSet.TYPE.BINARY_CLASSIFICATION) {
                ClassificationError Xerror = getClassificationError(X);
                ClassificationError Verror = getClassificationError(V);
                System.out.printf("Epoch : %d Size: %d X: %.2f V: %.2f\n", e, size(), Xerror.getAccuracy(), Verror.getAccuracy());
                System.out.println("X Confusion Matrix\n" + Xerror.getStringConfusionMatrix());
                System.out.println("V Confusion Matrix\n" + Verror.getStringConfusionMatrix());
            } else if (this.type == DataSet.TYPE.REGRESSION)
                System.out.printf("Epoch: %d Size: %d MSE X: %.2f MSE V: %.2f\n", e, size(), getMeanSquareError(X), getMeanSquareError(V));

            Collections.shuffle(X);
            LAST = X.get(X.size() - 1);
            for (Instance instance : X) {
                ROOT.F(instance);
                ROOT.backPropagate(instance);
                ROOT.update(instance);
            }
            if (enableSaveFile)
                printToFile(saveFilePath);

            LEARNING_RATE *= learning_rate_decay;
        }
    }


    //<editor-fold desc="Modifiers">
    //1 only tags, 2 only image
    public double[] percentages1 = new double[]{0.565, 0.329, 0.567, 0.533, 0.680, 0.663, 0.510, 0.603, 0.418, 0.562, 0.549, 0.520, 0.507, 0.907, 0.654, 0.487, 0.596, 0.620, 0.691, 0.684, 0.607, 0.531, 0.603, 0.629
            , 0.694, 0.366, 0.534, 0.679, 0.724, 0.467, 0.737, 0.476, 0.622, 0.574, 0.488, 0.489, 0.565, 0.545, 0.000};
    public double[] percentages2 = new double[]{0.533, 0.447, 0.708, 0.744, 0.926, 0.701, 0.911, 0.684, 0.64, 0.966, 0.678, 0.932, 0.811, 0.951, 0.926, 0.949, 0.683, 0.85, 0.946, 0.947, 0.815, 0.685, 0.928, 0.743, 0.667, 0.62, 0.828, 0.905, 0.786, 0.944, 0.843, 0.958, 0.88, 0.952, 0.957, 0.659, 0.756, 0.755, 0.328};

    boolean use_g_new_version = false;

    public void enable_g_new_version() {
        if (dataSet.getClass().isInstance(FlickerDataSet.class))
            use_g_new_version = true;
    }
    //</editor-fold>

    //<editor-fold desc="Draw graph">
    public TreeNode treeNodeRoot;

    public double min_rho = 10000;
    public double max_rho = -10000;

    public double min_rho_times_gama = 10000;
    public double max_rho_times_gama = -10000;

    public double a1;
    public double b1;
    public double a2;
    public double b2;

    public void findAllMinDifferences(ArrayList<Instance> X) {
        ROOT.findAllMinMaxDifferences(X, treeNodeRoot);
    }

    public void findScaledRhos() {
        findMinMaxRho();
        ROOT.findScaledRhos();
    }

    public void findCumulativeG(ArrayList<Instance> X) {
        for (int i = 0; i < X.size(); i++) {
            ROOT.cumulative_g(X.get(i));
        }
        ROOT.max_cumulative_g();
    }

    private void findMinMaxRho() {
        ROOT.findMinMaxRho();
        a1 = 255 / (max_rho - min_rho);
        b1 = -min_rho * a1;
        a2 = 255 / (max_rho_times_gama - min_rho_times_gama);
        b2 = -min_rho_times_gama * a2;
    }

    public void find_ymeans(ArrayList<Instance> A) {
        for (Instance instance : A) {
            LAST = instance;
            ROOT.F(instance);
            ROOT.update_ymeans(A.size());
        }
    }

    public void followInstance(Instance i) {
        double[] y = ROOT.F(i).clone();
        for (int t = 0; t < i.r.length; t++) {
            System.out.print(i.r[t] + " ");
        }
        System.out.println();
        System.out.println(ROOT.toString(0) + "\n\n");
    }

    public void write_ymeans() {
        System.out.println(ROOT.toStringy_means(0) + "\n\n");
    }
    //</editor-fold>

    //<editor-fold desc="Save/Load File">
    private boolean enableSaveFile = false;
    private String saveFilePath = "";

    public void enableSaveFile(String saveFilePath) {
        this.enableSaveFile = true;
        this.saveFilePath = saveFilePath;
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

    public BTM(DataSet dataSet, String filename) throws FileNotFoundException {
        this.dataSet = dataSet;
        this.type = dataSet.type;
        Scanner scanner = new Scanner(new FileReader(filename));
        this.LEARNING_RATE = scanner.nextDouble();
        this.LAMBDA = scanner.nextDouble();
        this.CLASS_COUNT = scanner.nextInt();
        this.ATTRIBUTE_COUNT = scanner.nextInt();
        this.X = dataSet.TRAINING_INSTANCES;
        this.V = dataSet.VALIDATION_INSTANCES;

        ROOT = new Node(this, scanner, null);
    }
    //</editor-fold>

    //<editor-fold desc="Error calculation">
    public ClassificationError getClassificationError(ArrayList<Instance> instances) {
        if (type != DataSet.TYPE.BINARY_CLASSIFICATION && type != DataSet.TYPE.MULTI_CLASS_CLASSIFICATION)
            return null;

        ClassificationError classificationError = new ClassificationError(instances.get(0).r.length);

        for (Instance instance : instances) {
            double[] y = ROOT.F(instance);
            int _y;
            if (type == DataSet.TYPE.BINARY_CLASSIFICATION) {
                y[0] = sigmoid(y[0]);
                if (y[0] > 0.5)
                    _y = 1;
                else
                    _y = 0;
                classificationError.add(instance.r[0], _y);

            } else {
                y = softmax(y);
                _y = argMax(y);
            }

            classificationError.add(argMax(instance.r), _y);

        }
        return classificationError;
    }

    public double getAbsoluteDifference(ArrayList<Instance> instances) {
        double difference = 0;

        for (Instance instance : instances) {
            double y = ROOT.F(instance)[0];
            difference += Math.abs(y - instance.r[0]);
        }

        return difference / instances.size();
    }

    public double getMeanSquareError(ArrayList<Instance> instances) {
        double difference = 0;

        for (Instance instance : instances) {
            double y = ROOT.F(instance)[0];
            difference += Math.pow(y - instance.r[0], 2);
        }

        return difference / instances.size();
    }

    public MAPError getMAP_P50_error(ArrayList<Instance> instances) {
        if (type != DataSet.TYPE.MULTI_LABEL_CLASSIFICATION)
            return null;

        int CLASS_COUNT = instances.get(0).r.length;
        MAPError MAPError = new MAPError(CLASS_COUNT);

        for (Instance instance : instances) {
            instance.y = ROOT.F(instance).clone();
        }

        for (int i = 0; i < CLASS_COUNT; i++) {
            double error = 0;
            double positive_count = 0;
            double pre_count = 0;
            final int finalI = i;
            Collections.sort(instances, (o1, o2) -> Double.compare(o2.y[finalI], o1.y[finalI]));

            for (int j = 0; j < instances.size(); j++) {
                if (instances.get(j).r[i] == 1) {
                    if (j < 50)
                        pre_count++;
                    positive_count++;
                    error += (positive_count * 1.0) / (j + 1);
                }
            }

            error /= positive_count;

            MAPError.MAP[i] = error;
            MAPError.precision[i] = pre_count / 50.0f;
        }

        return MAPError;
    }

    public static class MAPError {
        public double[] MAP;
        public double[] precision;

        public MAPError(int class_count) {
            MAP = new double[class_count];
            precision = new double[class_count];
        }

        public String toString() {
            String s = "MAP: \n";
            double sumaMap = 0;
            for (double aMAP : MAP) {
                sumaMap += aMAP;
                s += String.format("%.3f\n", aMAP);
            }
            s += String.format("\nMAP Average : %.3f\n", sumaMap / MAP.length);

            s += "Precission: \n";
            double sumprecision = 0;
            for (double aprecission : precision) {
                sumprecision += aprecission;
                s += String.format("%.3f\n", aprecission);
            }
            s += String.format("\nPrecission Average : %.3f", sumprecision / precision.length);
            return s;
        }
    }

    public static class ClassificationError {
        int true_count;
        int false_count;
        int[][] confusion_matrix;

        final int class_count;

        public ClassificationError(int class_count) {
            if (class_count == 1)
                class_count++;
            this.class_count = class_count;
            confusion_matrix = new int[class_count][class_count];
            for (int i = 0; i < class_count; i++)
                Arrays.fill(confusion_matrix[i], 0);
        }

        public void add(int real_class, int predicted_class) {
            confusion_matrix[real_class][predicted_class]++;
            if (real_class == predicted_class)
                true_count++;
            else
                false_count++;
        }

        public double getMissClassificationError() {
            return (false_count * 1.0) / (true_count + false_count);
        }

        public double getAccuracy() {
            return 1 - getMissClassificationError();
        }

        public String getStringConfusionMatrix() {
            String s = "";
            StringBuilder stringBuilder = new StringBuilder();
            Formatter formatter = new Formatter(stringBuilder, Locale.US);

            for (int i = 0; i < class_count; i++) {
                int t = 0;
                int f = 0;
                for (int j = 0; j < class_count; j++) {
                    if (i == j)
                        t += confusion_matrix[i][j];
                    else
                        f += confusion_matrix[i][j];
                    formatter.format("%5d  ", confusion_matrix[i][j]);
                }
                formatter.format("%.2f\n", (t * 1.0) / (t + f));
            }
            return stringBuilder.toString();
        }
    }
    //</editor-fold>
}