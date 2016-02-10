package MultiLayerPerceptron;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;

/**
 * Created by mertcan on 10.2.2016.
 */
public class PerceptronRunner {
    public static void main(String[] args) throws MatlabInvocationException, MatlabConnectionException {
//        BackPropagation b = new BackPropagation("iris.data.txt","iris.data.txt", 5, 150, 0.1);
        BackPropagation b = new BackPropagation("data_set_nonlinear_2.data.txt","data_set_nonlinear_2_test.data.txt", 5, 150, 10, false);
        b.runPerceptron();
    }
}
