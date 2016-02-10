package MultiLayerPerceptron;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;

/**
 * Created by mertcan on 10.2.2016.
 */


//Backpropagation: train_file_name, test_file_name, neuron_Number, epoch, learn_rate, drawable, print results at each epoch
public class PerceptronRunner {
    public static void main(String[] args) throws MatlabInvocationException, MatlabConnectionException {
//        BackPropagation b = new BackPropagation("iris.data.txt","iris.data.txt", 5, 150, 0.1,false,false);
//        BackPropagation b = new BackPropagation("data_set_nonlinear_2.data.txt","data_set_nonlinear_2_test.data.txt", 5, 150, 10, false, true);
        BackPropagation b = new BackPropagation("millionsong_yearpred_clsfirst_small.txt","millionsong_yearpred_clsfirst_small.txt", 300, 100, 0.006, false, true);
//        BackPropagation b = new BackPropagation("data_sdt\\mnist\\mnist.txt","data_sdt\\mnist\\test.txt", 40, 100, 0.006, false, true);
        b.runPerceptron();
    }
}
