package MultiLayerPerceptron;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * Created by mertcan on 21.10.2015.
 */
public class BackPropagation {

    public static MultiLayerNetwork multi_perceptron;
    public static Random r = new Random();
    public final static int input_number = 150;
    public final static int hidden_neuron_number = 2;
    public final static int input_dimension = 4;
    public final static int output_dimension = 3;
    public final static int number_of_epochs = 1000;
    public static double[][] inputs = new double[input_number][input_dimension];
    public static double[][] outputs = new double[input_number][output_dimension];
    public static double[][] output_hats = new double[input_number][output_dimension];

    public static double[] B2 = new double[output_dimension];
    public static double[][] G2 = new double[output_dimension][hidden_neuron_number];
    public static double[] B1 = new double[hidden_neuron_number];
    public static double[][] G1 = new double[hidden_neuron_number][input_dimension];

    public static void main(String[] args){
        multi_perceptron = new MultiLayerNetwork(input_dimension,hidden_neuron_number,output_dimension);

        try {
//            readDataSet("data_set_1_4.data.txt");
            readDataSet("iris.data.txt");
//            readDataSet("sensor_readings_2.data.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*inputs[0][0] = 0.35;
        inputs[0][1] = 0.9;

        outputs[0][0] = 0.5;

        for(int i = 1; i < 100; i++){
            inputs[i][0] = 0.35;
            inputs[i][1] = 0.9;

            outputs[i][0] = 0.5;
        }

        multi_perceptron.W1[0][0] = 0.1;
        multi_perceptron.W1[1][0] = 0.4;

        multi_perceptron.W1[0][1] = 0.8;
        multi_perceptron.W1[1][1] = 0.6;

        multi_perceptron.W2[0][0] = 0.3;
        multi_perceptron.W2[0][1] = 0.9;*/

       /* inputs[0][0] = 0.1;
        inputs[0][1] = 0.1;

        inputs[1][0] = 0.2;
        inputs[1][1] = 0.2;

        inputs[2][0] = 0.3;
        inputs[2][1] = 0.3;

        outputs[0][0] = 0.1;
        outputs[0][1] = 0.1;

        outputs[1][0] = 0.2;
        outputs[1][1] = 0.2;

        outputs[2][0] = 0.2;
        outputs[2][1] = 0.2;

        for(int i = 1; i < 30; i++){
            inputs[i * 3][0] = 0.1;
            inputs[i * 3][1] = 0.1;

            inputs[i * 3 + 1][0] = 0.2;
            inputs[i * 3 + 1][1] = 0.2;

            inputs[i * 3 + 2][0] = 0.3;
            inputs[i * 3 + 2][1] = 0.3;

            outputs[i * 3][0] = 0.1;
            outputs[i * 3][1] = 0.1;

            outputs[i * 3 + 1][0] = 0.2;
            outputs[i * 3 + 1][1] = 0.2;

            outputs[i * 3 + 2][0] = 0.3;
            outputs[i * 3 + 2][1] = 0.3;
        }*/


        train_backPropagate();

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
            if(maxIndex == i / 50)
                trues++;
            else
                falses++;
        }
        System.out.println("True: " + trues + " False: " + falses + " Percentage: " + ((double) trues / input_number));
    }

    private static void train_backPropagate() {
        System.out.println("W1: " + toString2dArray(multi_perceptron.W1));

        System.out.println("W2: " + toString2dArray(multi_perceptron.W2));

        System.out.println("inputs: " + toString2dArray(inputs));


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
                    G2 = new double[output_dimension][input_dimension];
                    for (int j = 0; j < B2.length; j++) {
                        B2[j] = -2 * (outputs[theInput][j] - output_hat[j]) * multi_perceptron.hidden_neurons[j].derivative_sigma();
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
        }

        System.out.println("W1: " + toString2dArray(multi_perceptron.W1));

        System.out.println("W2: " + toString2dArray(multi_perceptron.W2));

        System.out.println("real outputs: " + toString2dArray(outputs));

        System.out.println("outputs: " + toString2dArray(output_hats));


    }

    private static double[] feed_forward(double[] input) {
        double[] output_hat = new double[output_dimension];

        for(int i = 0; i < multi_perceptron.input_layer; i++) {
            multi_perceptron.input_neurons[i].feed_neuron(input[i]);
        }

        if(multi_perceptron.hidden_layer != 0) {
            for(int i = 0; i < multi_perceptron.hidden_layer; i++){
                double sum_hidden_neuron_i = 0;
                for(int j = 0; j < multi_perceptron.input_layer; j++){
                    sum_hidden_neuron_i += multi_perceptron.input_neurons[j].output * multi_perceptron.W1[i][j];
                }
                multi_perceptron.hidden_neurons[i].feed_neuron(sum_hidden_neuron_i);
            }

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

    private static void readDataSet(String s) throws IOException {
        FileInputStream fstream = new FileInputStream(s);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        boolean firstLine = true;
        int a = 0;
        while ((strLine = br.readLine()) != null) {
            String[] parts = strLine.split(",");
            if (firstLine) {
                firstLine = false;
            }
            for (int i = 0; i < parts.length - 1; i++) {
                inputs[a][i] = Double.parseDouble(parts[i]);
            }
            if(parts[parts.length - 1].equals("Iris-setosa")){
                outputs[a] = new double[]{1, 0, 0};
            }else if(parts[parts.length - 1].equals("Iris-versicolor")){
                outputs[a] = new double[]{0, 1, 0};
            }else if(parts[parts.length - 1].equals("Iris-virginica")){
                outputs[a] = new double[]{0, 0, 1};
            }
//            if(parts[parts.length - 1].equals("a")){
//                outputs[a] = new double[]{1, 0, 0, 0};
//            }else if(parts[parts.length - 1].equals("b")){
//                outputs[a] = new double[]{0, 1, 0, 0};
//            }else if(parts[parts.length - 1].equals("c")){
//                outputs[a] = new double[]{0, 0, 1, 0};
//            }else if(parts[parts.length - 1].equals("d")){
//                outputs[a] = new double[]{0, 0, 0, 1};
//            }
//            if(parts[parts.length - 1].equals("a")){
//                outputs[a] = new double[]{1, 0, 0};
//            }else if(parts[parts.length - 1].equals("b")){
//                outputs[a] = new double[]{0, 1, 0};
//            }else if(parts[parts.length - 1].equals("c")){
//                outputs[a] = new double[]{0, 0, 1};
//            }
            a++;
        }

        br.close();
    }
}
