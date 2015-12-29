package MultiLayerPerceptron;

import Utils.Instance;
import Utils.Util;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

import java.io.IOException;
import java.util.*;

/**
 * Created by mertcan on 21.10.2015.
 */
public class BackPropagation {

    public static MultiLayerNetwork multi_perceptron;
    public static Random r = new Random();
    public final static int input_number = 60000;
    public final static int hidden_neuron_number = 28*28*2/3;
    public final static int input_dimension = 28*28;
    public final static int output_dimension = 10;
    public final static int number_of_epochs = 100;
    public static ArrayList<Instance> instances = new ArrayList<>();
    public static double[][] inputs = new double[input_number][input_dimension + 1];
    public static double[][] outputs = new double[input_number][output_dimension];
    public static double[][] output_hats = new double[input_number][output_dimension];

    public static double[] B2 = new double[output_dimension];
    public static double[][] G2 = new double[output_dimension][hidden_neuron_number + 1];
    public static double[] B1 = new double[hidden_neuron_number + 1];
    public static double[][] G1 = new double[hidden_neuron_number][input_dimension + 1];

    public static MatlabProxyFactory factory;
    public static MatlabProxy proxy;
    public static MatlabTypeConverter processor;

    public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException {

        if(hidden_neuron_number == 0)
            multi_perceptron = new MultiLayerNetwork(input_dimension + 1,hidden_neuron_number,output_dimension);
        else
            multi_perceptron = new MultiLayerNetwork(input_dimension + 1,hidden_neuron_number + 1,output_dimension);


        try {
//            Util.readFile(instances, "iris.data.txt");
//            Util.readFile(instances, "iris.data.v2.txt");
//            Util.readFile(instances, "data_set_nonlinear_1.data.txt");
            Util.readFile(instances, "data_sdt\\mnist\\mnist.txt");
//            Util.readFile(instances, "data_sdt\\mnist\\mnist_ordered_01.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Util.ATTRIBUTE_COUNT == 2){
            factory = new MatlabProxyFactory();
            proxy = factory.getProxy();
            processor = new MatlabTypeConverter(proxy);
        }




        createArrays();

        train_backPropagate();

        test();

        plotPoints();
        graph_all();
    }


    private static void plotPoints() throws MatlabInvocationException {
        if(Util.ATTRIBUTE_COUNT == 2){
            String points_x[] = new String[Util.CLASS_COUNT];
            String points_y[] = new String[Util.CLASS_COUNT];
            int class_size = instances.size() / Util.CLASS_COUNT;
            for(int i = 0; i < Util.CLASS_COUNT; i++){
                points_x[i] = "[" + instances.get(i * instances.size() / Util.CLASS_COUNT).attributes[0];
                points_y[i] = "[" + instances.get(i * instances.size() / Util.CLASS_COUNT).attributes[1];
                for(int j = 1 + i * class_size; j < (i + 1) * class_size; j++){
                    points_x[i] += "," + instances.get(j).attributes[0];
                    points_y[i] += "," + instances.get(j).attributes[1];
                }
                points_x[i] += "]";
                points_y[i] += "]";
            }
            for(int i = 0; i < Util.CLASS_COUNT; i++) {
                proxy.eval("points_x" + i + " = " + points_x[i]);
                proxy.eval("points_y" + i + " = " + points_y[i]);
            }
            String eval1 = "[";
            String eval2 = "[";
            for(int i = 0; i < Util.CLASS_COUNT; i++){
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
            for(int i = 0; i < xg.length; i++){
                for(int j = 0; j < xg[0].length; j++){
                    zg[i][j] = findClass(new Instance(new double[]{xg[i][j], yg[i][j], 1}));
//                    System.out.print(zg[i][j]);
                }
//                System.out.println();
            }
            processor.setNumericArray("zg", new MatlabNumericArray(zg, null));
        }
    }

    private static double findClass(Instance i) {
        double[] results = feed_forward(i.attributes);
//        System.out.println(toString1dArray(i.attributes) + " " + toString1dArray(results));
        double max = 0;
        int maxIndex = 0;
        for(int j = 0; j < results.length; j++){
            if(results[j] > max){
                max = results[j];
                maxIndex = j;
            }
        }
        return maxIndex;
    }

    private static void graph_all() throws MatlabInvocationException {
        if(Util.ATTRIBUTE_COUNT == 2) {
            proxy.eval("figure");
            proxy.eval("surf(xg,yg,zg)");
//            proxy.eval("figure");
//            proxy.eval("surfc(xg,yg,zg)");
//            proxy.eval("figure");
//            proxy.eval("surfc(zg)");
            proxy.eval("figure");
            String v = "[1";
            for(int i = 2; i < Util.CLASS_COUNT; i++)
                v += " " + i;
            v += "]";
            proxy.eval("contour(xg,yg,zg, " + v + ", 'ShowText','on')");

            String plot2 = "";
            for(int i = 0; i < Util.CLASS_COUNT; i++){
                plot2 += ",points_x" + i + ", points_y" + i + ", '.'";
            }
            plot2 += ")";
            proxy.eval("hold on");
            plot2 = "(" + plot2.substring(1);
            proxy.eval("plot" + plot2);

        }
    }


    private static void test() {
        int trues = 0;
        int falses = 0;
        for(int i = 0; i < input_number; i++){
//            double sum = 0;
            double max = 0;
            int maxIndex = 0;
            for(int j = 0; j < output_dimension; j++){
//                sum += Math.abs((outputs[i][j] - output_hats[i][j]) * (1 - output_hats[i][j]) * output_hats[i][j]);
                if(output_hats[i][j] > max){
                    max = output_hats[i][j];
                    maxIndex = j;
                }

            }
//            if(sum < Math.pow(10, -1) && sum > - Math.pow(10, -1))
//                trues++;
//            else
//                falses++;
//            if(maxIndex == i / (input_number / Util.CLASS_COUNT))
            if(outputs[i][maxIndex] == 1)
                trues++;
            else
                falses++;
        }
        System.out.println("True: " + trues + " False: " + falses + " Percentage: " + ((double) trues / input_number));
    }

    private static void createArrays() {
        for(int a = 0; a < instances.size(); a++) {
            Instance T = instances.get(a);
            for (int i = 0; i < Util.ATTRIBUTE_COUNT; i++) {
                inputs[a][i] = T.attributes[i];
            }
            inputs[a][Util.ATTRIBUTE_COUNT] = 1;
            outputs[a] = new double[Util.CLASS_COUNT];
            Arrays.fill(outputs[a], 0);
            outputs[a][T.classNumber] = 1;
        }
    }

    private static void train_backPropagate() {
//        System.out.println("W1: " + toString2dArray(multi_perceptron.W1));
//
//        System.out.println("W2: " + toString2dArray(multi_perceptron.W2));
//
//        System.out.println("inputs: " + toString2dArray(inputs));


        ArrayList<Integer> shuffler = new ArrayList<>();
        for(int i = 0; i < input_number; i++) shuffler.add(i);

        for(int trial = 0; trial < number_of_epochs; trial++) {
            Collections.shuffle(shuffler);
            for (int i = 0; i < input_number; i++) {
                int theInput = shuffler.get(i);
                double[] output_hat = feed_forward(inputs[theInput]);
//                System.out.println(toString1dArray(inputs[theInput]) + " " + toString1dArray(output_hat) + " " + toString1dArray(outputs[theInput]));
                output_hats[theInput] = output_hat;
                if(multi_perceptron.hidden_layer != 0) {
                    for (int j = 0; j < B2.length; j++) {
                        B2[j] = -2 * (outputs[theInput][j] - output_hat[j]) * multi_perceptron.output_neurons[j].derivative_sigma();
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
                        if(j != B1.length - 1)
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
                }else{
                    G2 = new double[output_dimension][input_dimension + 1];
                    for (int j = 0; j < B2.length; j++) {
                        if(j != B2.length - 1)
                            B2[j] = -2 * (outputs[theInput][j] - output_hat[j]) * multi_perceptron.hidden_neurons[j].derivative_sigma();
                        else
                            B2[j] = -2 * (outputs[theInput][j] - output_hat[j]);
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
            System.out.print("Epoch: " + trial + " ");
            test();
        }

//        System.out.println("W1: " + toString2dArray(multi_perceptron.W1));
//
//        System.out.println("W2: " + toString2dArray(multi_perceptron.W2));
//
//        System.out.println("real outputs: " + toString2dArray(outputs));
//
//        System.out.println("outputs: " + toString2dArray(output_hats));


    }

    private static double[] feed_forward(double[] input) {
        double[] output_hat = new double[output_dimension];

        for(int i = 0; i < multi_perceptron.input_layer; i++) {
            multi_perceptron.input_neurons[i].feed_neuron(input[i]);
        }

        if(multi_perceptron.hidden_layer != 0) {
            for(int i = 0; i < multi_perceptron.hidden_layer - 1; i++){
                double sum_hidden_neuron_i = 0;
                for(int j = 0; j < multi_perceptron.input_layer; j++){
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
        }else{
            for(int i = 0; i < multi_perceptron.output_layer; i++){
                double sum_hidden_neuron_i = 0;
                for(int j = 0; j < multi_perceptron.input_layer; j++){
                    sum_hidden_neuron_i += multi_perceptron.input_neurons[j].output * multi_perceptron.W1[i][j];
                }
                multi_perceptron.hidden_neurons[i].feed_neuron(sum_hidden_neuron_i);
                output_hat[i] = multi_perceptron.hidden_neurons[i].output;
//                output_hat[i] = sum_hidden_neuron_i;
            }
        }


        return  output_hat;
    }

    public static String toString2dArray(double[][] a){
        String s = "";
        for(int i = 0; i < a.length; i++){
            for(int j = 0; j < a[0].length; j++){
                s += a[i][j] + " ";
            }
            s += "\n";
        }
        return s;
    }

    public static String toString1dArray(double[] a){
        String s = "";
        for(int i = 0; i < a.length; i++){
                s += a[i] + " ";
        }
        return s;
    }

}
