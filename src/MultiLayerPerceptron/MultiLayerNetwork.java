package MultiLayerPerceptron;

import java.util.Random;

/**
 * Created by mertcan on 21.10.2015.
 */
public class MultiLayerNetwork {
    public Random r = new Random();
    int input_layer;
    int hidden_layer;
    int output_layer;
    double[][] W1;
    double[][] W2;
    Neuron[] input_neurons;
    Neuron[] hidden_neurons;
    Neuron[] output_neurons;
    double learn_rate = 1;

    public MultiLayerNetwork(int input_layer, int hidden_layer, int output_layer){
        this.hidden_layer = hidden_layer;
        this.input_layer = input_layer;
        this.output_layer = output_layer;
        createW1();
        createW2();
        createInputNeurons();
        createHiddenNeurons();
        createOutputNeurons();
    }

    private void createInputNeurons() {
        input_neurons = new Neuron[input_layer];
        for(int i = 0; i < input_layer; i++){
            input_neurons[i] = new Neuron(true);
        }
    }

    private void createHiddenNeurons() {
        hidden_neurons = new Neuron[hidden_layer];
        for(int i = 0; i < hidden_layer; i++){
            hidden_neurons[i] = new Neuron(false);
        }
    }

    private void createOutputNeurons() {
        output_neurons = new Neuron[output_layer];
        for(int i = 0; i < output_layer; i++){
            output_neurons[i] = new Neuron(false);
        }
    }

    private void createW2() {
        W2 = new double[hidden_layer][output_layer];
        for(int i = 0; i < hidden_layer; i++){
            for(int j = 0; j < output_layer; j++){
                W2[i][j] = r.nextDouble();
            }
        }
    }

    private void createW1() {
        W1 = new double[input_layer][hidden_layer];
        for(int i = 0; i < input_layer; i++){
            for(int j = 0; j < hidden_layer; j++){
                W1[i][j] = r.nextDouble();
            }
        }
    }

    public class Neuron{
        double row;
        double output;
        boolean isInputLayer = false;
        
        public Neuron(boolean isInputLayer){
            this.isInputLayer = isInputLayer;
        }
        
        public void feed_neuron(double row){
            this.row = row;
            if(!isInputLayer)
                output = sigma(row);
            else
                output = row;
        }

        private double sigma(double row) {
            return 1/(1 + (Math.exp(-row)));
        }

        public double derivative_sigma(){
            return output * (1 - output);
        }
    }
}
