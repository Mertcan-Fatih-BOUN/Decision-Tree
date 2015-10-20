package MultiLayerPerceptron;

import java.util.Random;

/**
 * Created by mertcan on 21.10.2015.
 */
public class BackPropagation {

    public static MultiLayerNetwork multi_perceptron;
    public static Random r = new Random();
    public final static int input_number = 3;
    public final static int input_dimension = 2;
    public final static int output_dimension = 2;
    public static double[][] inputs = new double[input_number][input_dimension];
    public static double[][] outputs = new double[input_number][output_dimension];

    public static double[] B2 = new double[output_dimension];
    public static double[][] G2 = new double[output_dimension][input_number];
    public static double[] B1 = new double[input_number];
    public static double[][] G1 = new double[input_number][input_dimension];

    public static void main(String[] args){
        multi_perceptron = new MultiLayerNetwork(input_dimension,input_number,output_dimension);

        /*for(int i = 0; i < input_number; i++){
            for(int j = 0; j < input_dimension; j++){
                inputs[i][j] = r.nextInt(i * 3 + j * 2 + 1);
                outputs[i][j] = i;
            }
        }*/

        inputs[0][0] = 1;
        inputs[0][1] = 2;

        inputs[1][0] = 2;
        inputs[1][1] = 3;

        inputs[2][0] = 0;
        inputs[2][1] = 1;

        outputs[0][0] = 1;
        outputs[0][1] = 1;

        outputs[1][0] = 1.5;
        outputs[1][1] = 1.5;

        outputs[2][0] = 0.5;
        outputs[2][1] = 0.5;

        train_backPropagate();

    }

    private static void train_backPropagate() {
        System.out.println("W1: " + toString2dArray(multi_perceptron.W1));

        System.out.println("W2: " + toString2dArray(multi_perceptron.W2));

        System.out.println("inputs: " + toString2dArray(inputs));

        for(int i = 0; i < input_number; i++) {
            double[] output_hat = feed_forward(inputs[i]);
            for(int j = 0; j < B2.length; j++){
                B2[j] = -2 * (outputs[i][j] - output_hat[j]) * multi_perceptron.output_neurons[j].derivative_sigma();
            }
            for(int j = 0; j < G2.length; j++){
                for(int t = 0; t < G2[0].length; t++){
                    G2[j][t] = B2[j] * multi_perceptron.hidden_neurons[t].output;
                }
            }

            for(int j = 0; j < B1.length; j++){
                double temp = 0;
                for(int t = 0; t < B2.length; t++){
                    temp += B2[t] * multi_perceptron.W2[j][t];
                }
                temp *= multi_perceptron.hidden_neurons[j].derivative_sigma();
                B1[j] = temp;
            }
            for(int j = 0; j < G1.length; j++){
                for(int t = 0; t < G1[0].length; t++){
                    G1[j][t] = B1[j] * multi_perceptron.input_neurons[t].output;
                }
            }

            for(int j = 0; j < G1.length; j++){
                for(int t = 0; t < G1[0].length; t++){
                    multi_perceptron.W1[t][j] -= multi_perceptron.learn_rate * G1[j][t];
                }
            }

            for(int j = 0; j < G2.length; j++){
                for(int t = 0; t < G2[0].length; t++){
                    multi_perceptron.W2[t][j] -= multi_perceptron.learn_rate * G2[j][t];
                }
            }
        }

        System.out.println("W1: " + toString2dArray(multi_perceptron.W1));

        System.out.println("W2: " + toString2dArray(multi_perceptron.W2));

        System.out.println("real outputs: " + toString2dArray(outputs));

        for(int i = 0; i < input_number; i++) {
            double[] output_hat = feed_forward(inputs[i]);
            System.out.println(toString1dArray(output_hat));
        }


    }

    private static double[] feed_forward(double[] input) {
        double[] output_hat = new double[output_dimension];

        for(int i = 0; i < multi_perceptron.input_layer; i++) {
            multi_perceptron.input_neurons[i].feed_neuron(input[i]);
        }

        for(int i = 0; i < multi_perceptron.hidden_layer; i++){
            double sum_hidden_neuron_i = 0;
            for(int j = 0; j < multi_perceptron.input_layer; j++){
                sum_hidden_neuron_i += multi_perceptron.input_neurons[j].output * multi_perceptron.W1[j][i];
            }
            multi_perceptron.hidden_neurons[i].feed_neuron(sum_hidden_neuron_i);
        }

        for(int i = 0; i < multi_perceptron.output_layer; i++){
            double sum_hidden_neuron_i = 0;
            for(int j = 0; j < multi_perceptron.hidden_layer; j++){
                sum_hidden_neuron_i += multi_perceptron.hidden_neurons[j].output * multi_perceptron.W2[j][i];
            }
            multi_perceptron.output_neurons[i].feed_neuron(sum_hidden_neuron_i);
            output_hat[i] = sum_hidden_neuron_i;
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
