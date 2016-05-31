package MultiLayerPerceptronClean;

import Readers.DataSet;
import Readers.Instance;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;
import misc.IndexComparator;
import misc.Util;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by mertcan on 21.10.2015.
 */
public class BackPropagation {

    public MultiLayerNetwork multi_perceptron;
    public Random r = new Random();
    public int input_number_train = 60000;
    public int input_number_test = 60000;
    public int hidden_neuron_number = 40;//28*28*2/3;
    public int input_dimension = 28 * 28;
    public int output_dimension = 10;
    public int number_of_epochs = 50;
    public boolean drawable = false;
    public boolean print_each_epoch = false;
    public ArrayList<Instance> train_instances = new ArrayList<>();
    public ArrayList<Instance> test_instances = new ArrayList<>();
    public DecimalFormat format = new DecimalFormat("#.###");
//    public static double[][] outputs_train = new double[input_number_train][output_dimension];
//    public static double[][] output_hats_train = new double[input_number_train][output_dimension];
//    public static double[][] outputs_test = new double[input_number_test][output_dimension];
//    public static double[][] output_hats_test = new double[input_number_train][output_dimension];

    public double[] B2 = new double[output_dimension];
    public double[][] G2 = new double[output_dimension][hidden_neuron_number + 1];
    public double[] B1 = new double[hidden_neuron_number + 1];
    public double[][] G1 = new double[hidden_neuron_number][input_dimension + 1];

    public String percentages = "";
    public int precision_at_k = 50;

    public DataSet dataSet;
    public DataSet.TYPE type;


    public static MatlabProxyFactory factory;
    public static MatlabProxy proxy;
    public static MatlabTypeConverter processor;

    public BackPropagation() {

    }

    public BackPropagation(DataSet dataSet, int hidden_number, int epochs, double learn_rate, boolean draw, boolean print_each) throws MatlabConnectionException, MatlabInvocationException {
        this.dataSet = dataSet;
        this.type = dataSet.type;
        train_instances = dataSet.TRAINING_INSTANCES;
        test_instances = dataSet.VALIDATION_INSTANCES;
        CLASS_NAMES = dataSet.CLASS_NAMES;
        CLASS_COUNT = CLASS_NAMES.size();
        input_dimension = train_instances.get(0).x.length;

        normalize(train_instances, test_instances);
        input_number_train = train_instances.size();
        input_number_test = test_instances.size();
        output_dimension = CLASS_NAMES.size();
        MultiLayerNetwork.learn_rate_main = learn_rate;
        MultiLayerNetwork.learn_rate = learn_rate;
        number_of_epochs = epochs;
        hidden_neuron_number = hidden_number;
        drawable = draw;
        print_each_epoch = print_each;

        B2 = new double[output_dimension];
        G2 = new double[output_dimension][hidden_neuron_number + 1];
        B1 = new double[hidden_neuron_number + 1];
        G1 = new double[hidden_neuron_number][input_dimension + 1];
    }

    public BackPropagation(String trainfile, String testfile, DataSet.TYPE type, int hidden_number, int epochs, double learn_rate, boolean draw, boolean print_each) throws MatlabConnectionException, MatlabInvocationException {

        try {
            if (train_instances.size() == 0) {
                readFile(train_instances, trainfile);
                readFile(test_instances, testfile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.type = type;
        normalize(train_instances, test_instances);
        input_number_train = train_instances.size();
        input_number_test = test_instances.size();
        output_dimension = CLASS_NAMES.size();
        MultiLayerNetwork.learn_rate_main = learn_rate;
        MultiLayerNetwork.learn_rate = learn_rate;
        number_of_epochs = epochs;
        hidden_neuron_number = hidden_number;
        drawable = draw;
        print_each_epoch = print_each;

        B2 = new double[output_dimension];
        G2 = new double[output_dimension][hidden_neuron_number + 1];
        B1 = new double[hidden_neuron_number + 1];
        G1 = new double[hidden_neuron_number][input_dimension + 1];

    }


    public void runPerceptron() throws MatlabInvocationException, MatlabConnectionException {
        if (hidden_neuron_number == 0)
            multi_perceptron = new MultiLayerNetwork(input_dimension + 1, hidden_neuron_number, output_dimension);
        else
            multi_perceptron = new MultiLayerNetwork(input_dimension + 1, hidden_neuron_number + 1, output_dimension);


        if (input_dimension == 2 && drawable) {
            factory = new MatlabProxyFactory();
            proxy = factory.getProxy();
            processor = new MatlabTypeConverter(proxy);
        }


        createArrays();

        train_backPropagate();

        if (type == DataSet.TYPE.MULTI_LABEL_CLASSIFICATION)
            System.out.println("Epoch :" + number_of_epochs + "\n" + getMAP_P50_error(train_instances) + "\n" + getMAP_P50_error(test_instances) + "\nEpoch :" + number_of_epochs + "\n-----------------------\n");
        else
            System.out.println("Train: " + test(train_instances) + "\t\tTest: " + test(test_instances));

        if (drawable) {
            plotPoints();
            graph_all();
        }
    }

    private void createArrays() {
        for (int a = 0; a < train_instances.size(); a++) {
            Instance T = train_instances.get(a);
            double[] x_ = new double[T.x.length + 1];
            for (int i = 0; i < input_dimension; i++) {
                x_[i] = T.x[i];
            }
            x_[input_dimension] = 1;
            T.x = new double[0];
            T.x = x_;
        }
    }

    private void train_backPropagate() {
        ArrayList<Integer> shuffler = new ArrayList<>();
        for (int i = 0; i < input_number_train; i++) shuffler.add(i);

        for (int trial = 0; trial < number_of_epochs; trial++) {
            Collections.shuffle(shuffler);
            for (int i = 0; i < input_number_train; i++) {
                int theInput = shuffler.get(i);
                double[] output_hat = feed_forward(train_instances.get(theInput).x);
                int[] output = train_instances.get(theInput).r;
//                System.out.println(toString1dArray(inputs[theInput]) + " " + toString1dArray(output_hat) + " " + toString1dArray(outputs_train[theInput]));
//                output_hats_train[theInput] = output_hat;
                if (multi_perceptron.hidden_layer != 0) {
                    for (int j = 0; j < B2.length; j++) {
                        B2[j] = -2 * (output[j] - output_hat[j]) * multi_perceptron.output_neurons[j].derivative_sigma();
                    }
                    for (int j = 0; j < G2.length; j++) {
                        for (int t = 0; t < G2[0].length; t++) {
                            G2[j][t] = B2[j] * multi_perceptron.hidden_neurons[t].output;
                        }
                    }

                    for (int j = 0; j < B1.length; j++) {
                        double temp = 0;
                        for (int t = 0; t < B2.length; t++) {
                            temp += B2[t] * multi_perceptron.W2[t][j];
                        }
                        if (j != B1.length - 1)
                            temp *= multi_perceptron.hidden_neurons[j].derivative_sigma();
                        B1[j] = temp;
                    }
                    for (int j = 0; j < G1.length; j++) {
                        for (int t = 0; t < G1[0].length; t++) {
                            G1[j][t] = B1[j] * multi_perceptron.input_neurons[t].output;
                        }
                    }


                    for (int j = 0; j < G1.length; j++) {
                        for (int t = 0; t < G1[0].length; t++) {
                            multi_perceptron.W1[j][t] -= multi_perceptron.learn_rate * G1[j][t];
                        }
                    }
                    for (int j = 0; j < G2.length; j++) {
                        for (int t = 0; t < G2[0].length; t++) {
                            multi_perceptron.W2[j][t] -= multi_perceptron.learn_rate * G2[j][t];
                        }
                    }
                } else {
                    G2 = new double[output_dimension][input_dimension + 1];
                    for (int j = 0; j < B2.length; j++) {
                        if (j != B2.length - 1)
                            B2[j] = -2 * (output[j] - output_hat[j]) * multi_perceptron.hidden_neurons[j].derivative_sigma();
                        else
                            B2[j] = -2 * (output[j] - output_hat[j]);
                    }
                    for (int j = 0; j < G2.length; j++) {
                        for (int t = 0; t < G2[0].length; t++) {
                            G2[j][t] = B2[j] * multi_perceptron.input_neurons[t].output;
                        }
                    }
                    for (int j = 0; j < G2.length; j++) {
                        for (int t = 0; t < G2[0].length; t++) {
                            multi_perceptron.W1[j][t] -= multi_perceptron.learn_rate * G2[j][t];
                        }
                    }
                }

            }
            if (print_each_epoch) {
                if (type == DataSet.TYPE.MULTI_LABEL_CLASSIFICATION) {
                    System.out.println("Epoch :" + trial + "\n" + getMAP_P50_error(train_instances) + "\n" + getMAP_P50_error(test_instances) + "\nEpoch :" + trial + "\n-----------------------\n");
                } else {
                    System.out.println("Epoch: " + trial + "\t\t\tTrain: " + test(train_instances) + "\t\tTest: " + test(test_instances));
                }
            }
            MultiLayerNetwork.learn_rate *= 0.99;
        }

//        System.out.println("W1: " + toString2dArray(multi_perceptron.W1));
//
//        System.out.println("W2: " + toString2dArray(multi_perceptron.W2));
//
//        System.out.println("real outputs_train: " + toString2dArray(outputs_train));
//
//        System.out.println("outputs_train: " + toString2dArray(output_hats_train));


    }

    private void outputToFile(int e) throws IOException {
        File file2 = new File("log" + File.separator + "mnist_multilayer_train__test_200" + File.separator + "learning_rate_" + (int) (MultiLayerNetwork.learn_rate_main * 10000) + "_hidden_" + hidden_neuron_number + ".txt");
        file2.getParentFile().mkdirs();
        BufferedWriter writer2 = new BufferedWriter(new FileWriter(file2, true));
        writer2.write("Epoch: " + e + "\t\t\tTrain: " + test(train_instances) + "\t\tTest: " + test(test_instances) + "\n");
        writer2.flush();
        writer2.close();
    }

    public double[] feed_forward(double[] input) {
        double[] output_hat = new double[output_dimension];

        for (int i = 0; i < multi_perceptron.input_layer; i++) {
            multi_perceptron.input_neurons[i].feed_neuron(input[i]);
        }

        if (multi_perceptron.hidden_layer != 0) {
            for (int i = 0; i < multi_perceptron.hidden_layer - 1; i++) {
                double sum_hidden_neuron_i = 0;
                for (int j = 0; j < multi_perceptron.input_layer; j++) {
                    sum_hidden_neuron_i += multi_perceptron.input_neurons[j].output * multi_perceptron.W1[i][j];
                }
                multi_perceptron.hidden_neurons[i].feed_neuron(sum_hidden_neuron_i);
            }
            multi_perceptron.hidden_neurons[multi_perceptron.hidden_layer - 1].output = 1;
            multi_perceptron.hidden_neurons[multi_perceptron.hidden_layer - 1].row = 1;

            for (int i = 0; i < multi_perceptron.output_layer; i++) {
                double sum_hidden_neuron_i = 0;
                for (int j = 0; j < multi_perceptron.hidden_layer; j++) {
                    sum_hidden_neuron_i += multi_perceptron.hidden_neurons[j].output * multi_perceptron.W2[i][j];
                }
                multi_perceptron.output_neurons[i].feed_neuron(sum_hidden_neuron_i);

                output_hat[i] = multi_perceptron.output_neurons[i].output;
            }
        } else {
            for (int i = 0; i < multi_perceptron.output_layer; i++) {
                double sum_hidden_neuron_i = 0;
                for (int j = 0; j < multi_perceptron.input_layer; j++) {
                    sum_hidden_neuron_i += multi_perceptron.input_neurons[j].output * multi_perceptron.W1[i][j];
                }
                multi_perceptron.hidden_neurons[i].feed_neuron(sum_hidden_neuron_i);
                output_hat[i] = multi_perceptron.hidden_neurons[i].output;
//                output_hat[i] = sum_hidden_neuron_i;
            }
        }


        return output_hat;
    }


    public MAPError getMAP_P50_error(ArrayList<Instance> instances) {
        if (type != DataSet.TYPE.MULTI_LABEL_CLASSIFICATION)
            return null;

        int CLASS_COUNT = instances.get(0).r.length;
        MAPError MAPError = new MAPError(CLASS_COUNT);

        for (Instance instance : instances) {
            double[] atts = Arrays.copyOf(instance.x, instance.x.length + 1);
            atts[atts.length - 1] = 1;
            instance.y = feed_forward(atts);
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


    public String test(ArrayList<Instance> T) {
        int trues = 0;
        int falses = 0;
        double diff_squared = 0;
        double abs_diff = 0;
        for (int i = 0; i < T.size(); i++) {
            double[] atts = Arrays.copyOf(T.get(i).x, T.get(i).x.length + 1);
            atts[atts.length - 1] = 1;
            int prediction = Util.argMax(feed_forward(atts));
//            if(i % 25000 == 0)
//                System.out.println(CLASS_NAMES.get(prediction) + "  " + CLASS_NAMES.get(T.get(i).classNumber));
            if (T.get(i).r[prediction] == 1)
                trues++;
            else
                falses++;
            if (type == DataSet.TYPE.REGRESSION) {
                diff_squared += Math.pow(Double.parseDouble(CLASS_NAMES.get(prediction)) - Double.parseDouble(CLASS_NAMES.get(T.get(i).r[prediction])), 2);
                abs_diff += Math.abs(Double.parseDouble(CLASS_NAMES.get(prediction)) - Double.parseDouble(CLASS_NAMES.get(T.get(i).r[prediction])));
            }
        }
        //System.out.println("True: " + trues + " False: " + falses + " Percentage: " + ((double) trues / input_number_train));
        if (type == DataSet.TYPE.BINARY_CLASSIFICATION || type == DataSet.TYPE.MULTI_CLASS_CLASSIFICATION) {
            return "True: " + trues + " False: " + falses + " Percentage: " + format.format(((double) trues / T.size()));
        } else if (type == DataSet.TYPE.REGRESSION) {
            return "True: " + trues + " False: " + falses + " Percentage: " + format.format(((double) trues / T.size() + " Diff Squared Average: " + Math.sqrt(diff_squared / T.size()) + " Absolute Diff Average: " + (abs_diff / T.size())));
        }
        return "";
    }

    public static String toString2dArray(double[][] a) {
        String s = "";
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                s += a[i][j] + " ";
            }
            s += "\n";
        }
        return s;
    }

    public static String toString1dArray(double[] a) {
        String s = "";
        for (int i = 0; i < a.length; i++) {
            s += a[i] + " ";
        }
        return s;
    }

    public ArrayList<String> CLASS_NAMES = new ArrayList<>();
    public int CLASS_COUNT = 0;

    public void readFile(ArrayList<Instance> I, String filename) throws IOException {
        String line;

        InputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);

        line = br.readLine();

        br.close();
        String[] s;
        String splitter;
        if (!line.contains(","))
            splitter = "\\s+";
        else
            splitter = ",";
        s = line.split(splitter);

        if (!line.contains("INPUT_DIMENSION"))
            input_dimension = s.length - 1;
        else
            input_dimension = Integer.parseInt(s[1]);

        if (filename.contains(("complete_mirflickr"))) {
            if (filename.contains("no"))
                input_dimension = 1715;
            else
                input_dimension = 1715 + 457;
        }

//        System.out.println(input_dimension + " " + line);
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            s = line.split(splitter);
            if (line.contains("INPUT_DIMENSION"))
                continue;
            double[] attributes = new double[input_dimension];
            String[] className;
            if (s.length > input_dimension)
                className = new String[s.length - input_dimension];
            else
                className = new String[]{"not_given"};
            if (filename.contains("clsfirst")) {
                for (int i = 0; i < input_dimension; i++) {
                    attributes[i] = Double.parseDouble(s[i + 1]);
//                    System.out.print(attributes[i] + "  ");
                }
                className[0] = s[0].substring(0, 4);
            } else {
                for (int i = 0; i < input_dimension; i++) {
                    attributes[i] = Double.parseDouble(s[i]);
                }
                for (int i = input_dimension; i < s.length; i++)
                    className[i - input_dimension] = s[i];
            }
            double classNumber = -1;
            ArrayList<Integer> classNumbers = new ArrayList<>();
            for (int i = input_dimension; i < s.length; i++) {
                if (CLASS_NAMES.contains(className[i - input_dimension])) {
                    classNumber = CLASS_NAMES.indexOf(className[i - input_dimension]);
                    classNumbers.add(CLASS_NAMES.indexOf(className[i - input_dimension]));
                } else {
                    CLASS_NAMES.add(className[i - input_dimension]);
                    classNumber = CLASS_NAMES.indexOf(className[i - input_dimension]);
                    classNumbers.add(CLASS_NAMES.indexOf(className[i - input_dimension]));
                }
            }
            if (s.length == input_dimension) {
                if (CLASS_NAMES.contains(className[0])) {
                    classNumber = CLASS_NAMES.indexOf(className[0]);
                    classNumbers.add(CLASS_NAMES.indexOf(className[0]));
                } else {
                    CLASS_NAMES.add(className[0]);
                    classNumber = CLASS_NAMES.indexOf(className[0]);
                    classNumbers.add(CLASS_NAMES.indexOf(className[0]));
                }
            }

            Instance ins = new Instance();
            ins.r = new int[classNumbers.size()];
            for (int i = 0; i < classNumbers.size(); i++) {
                ins.r[i] = classNumbers.get(i);
            }
            ins.x = attributes;
            I.add(ins);
        }
        CLASS_COUNT = CLASS_NAMES.size();
        for (Instance ins : I) {
            int[] r_ = new int[CLASS_COUNT];
            Arrays.fill(r_, 0);
            for (int i = 0; i < ins.r.length; i++) {
                r_[ins.r[i]] = 1;
            }
            ins.r = r_;
        }
//        for(int i = 0; i < CLASS_COUNT; i++)
//            System.out.print(CLASS_NAMES.get(i) + "  ");
//        System.out.println(CLASS_COUNT);
    }

    public void normalize(ArrayList<Instance> x, ArrayList<Instance> t) {
        for (int i = 0; i < input_dimension; i++) {
            double mean = 0;
            for (Instance ins : x) {
                mean += ins.x[i];
            }
            mean /= x.size();

            double stdev = 0;
            for (Instance ins : x) {
                stdev += (ins.x[i] - mean) * (ins.x[i] - mean);
            }
            stdev /= (x.size() - 1);
            stdev = Math.sqrt(stdev);

            for (Instance ins : x) {
                ins.x[i] -= mean;
                if (stdev != 0)
                    ins.x[i] /= stdev;
            }
            for (Instance ins : t) {
                ins.x[i] -= mean;
                if (stdev != 0)
                    ins.x[i] /= stdev;
            }

        }
    }

    private void plotPoints() throws MatlabInvocationException {
        if (input_dimension == 2) {
            String points_x[] = new String[CLASS_COUNT];
            String points_y[] = new String[CLASS_COUNT];
            int class_size = train_instances.size() / CLASS_COUNT;
            for (int i = 0; i < CLASS_COUNT; i++) {
                points_x[i] = "[" + train_instances.get(i * train_instances.size() / CLASS_COUNT).x[0];
                points_y[i] = "[" + train_instances.get(i * train_instances.size() / CLASS_COUNT).x[1];
                for (int j = 1 + i * class_size; j < (i + 1) * class_size; j++) {
                    points_x[i] += "," + train_instances.get(j).x[0];
                    points_y[i] += "," + train_instances.get(j).x[1];
                }
                points_x[i] += "]";
                points_y[i] += "]";
            }
            for (int i = 0; i < CLASS_COUNT; i++) {
                proxy.eval("points_x" + i + " = " + points_x[i]);
                proxy.eval("points_y" + i + " = " + points_y[i]);
            }
            String eval1 = "[";
            String eval2 = "[";
            for (int i = 0; i < CLASS_COUNT; i++) {
                eval1 += "points_x" + i + " ";
                eval2 += "points_y" + i + " ";
            }
            eval1 += "]";
            eval2 += "]";

            proxy.eval("points_x = " + eval1);
            proxy.eval("points_y = " + eval2);

            proxy.eval("xmin = min(points_x)");
            proxy.eval("xmax = max(points_x)");
            proxy.eval("difference = xmax - xmin");
            proxy.eval("xmin = xmin - (difference) / 3");
            proxy.eval("xmax = xmax + (difference) / 3");
            proxy.eval("xlin = linspace(xmin, xmax)");
            proxy.eval("xlin2 = linspace(xmin, xmax,50)");
            proxy.eval("ymin = min(points_y)");
            proxy.eval("ymax = max(points_y)");
            proxy.eval("difference_y = ymax - ymin");
            proxy.eval("ymin = ymin - (difference_y) / 3");
            proxy.eval("ymax = ymax + (difference_y) / 3");
            proxy.eval("ylin = linspace(ymin, ymax,50)");
            proxy.eval("[xg, yg] = meshgrid(xlin2, ylin)");
            double[][] xg = processor.getNumericArray("xg").getRealArray2D();
            double[][] yg = processor.getNumericArray("yg").getRealArray2D();
            double[][] zg = new double[xg.length][xg[0].length];
            for (int i = 0; i < xg.length; i++) {
                for (int j = 0; j < xg[0].length; j++) {
                    Instance ins = new Instance();
                    ins.x = new double[]{xg[i][j], yg[i][j], 1};
                    zg[i][j] = findClass(ins);
//                    System.out.print(zg[i][j]);
                }
//                System.out.println();
            }
            processor.setNumericArray("zg", new MatlabNumericArray(zg, null));
        }
    }

    private double findClass(Instance i) {
        double[] results = feed_forward(i.x);
//        System.out.println(toString1dArray(i.attributes) + " " + toString1dArray(results));
        double max = 0;
        int maxIndex = 0;
        for (int j = 0; j < results.length; j++) {
            if (results[j] > max) {
                max = results[j];
                maxIndex = j;
            }
        }
        return maxIndex;
    }

    private void graph_all() throws MatlabInvocationException {
        if (input_dimension == 2) {
            proxy.eval("figure");
            proxy.eval("surf(xg,yg,zg)");
//            proxy.eval("figure");
//            proxy.eval("surfc(xg,yg,zg)");
//            proxy.eval("figure");
//            proxy.eval("surfc(zg)");
            proxy.eval("figure");
            String v = "[1";
            for (int i = 2; i < CLASS_COUNT; i++)
                v += " " + i;
            v += "]";
            proxy.eval("contour(xg,yg,zg, " + v + ", 'ShowText','on')");

            String plot2 = "";
            for (int i = 0; i < CLASS_COUNT; i++) {
                plot2 += ",points_x" + i + ", points_y" + i + ", '.'";
            }
            plot2 += ")";
            proxy.eval("hold on");
            plot2 = "(" + plot2.substring(1);
            proxy.eval("plot" + plot2);

        }
    }

}
