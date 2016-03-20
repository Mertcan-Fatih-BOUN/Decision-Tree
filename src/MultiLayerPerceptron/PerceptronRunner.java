package MultiLayerPerceptron;

import Utils.Instance;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mertcan on 10.2.2016.
 */


//Backpropagation: train_file_name, test_file_name, neuron_Number, epoch, learn_rate, drawable, print results at each epoch
public class PerceptronRunner {
    public static void main(String[] args) throws MatlabInvocationException, MatlabConnectionException {
//        BackPropagation b = new BackPropagation("iris.data.txt","iris.data.txt", 5, 150, 0.1,false,false);
//        BackPropagation b = new BackPropagation("data_set_nonlinear_2.data.txt","data_set_nonlinear_2_test.data.txt", 5, 150, 10, false, true);
//        BackPropagation b = new BackPropagation("millionsong_yearpred_clsfirst_small.txt","millionsong_yearpred_clsfirst_small.txt", 300, 100, 0.0006, false, true);
//        BackPropagation b;
//        double learn_rate = 0.006;
//            for (int j = 0; j < 3; j++) {
//                b = new BackPropagation("data_sdt" + File.separator + "mnist" + File.separator + "mnist.txt", "data_sdt" + File.separator + "mnist" + File.separator + "test.txt", 40 + j * 50, 200, learn_rate, false, false);
//                b.runPerceptron();
//                System.out.println("\n");
//            }



//        for(int i = 0; i < 20; i++) {
//            double learn_rate = 0.000000005 * Math.pow(10,i);
//            System.out.println("Rate is: " + learn_rate);
//            BackPropagation b = new BackPropagation("data_multi" + File.separator + "millionsong_yearpred_clsfirst-train.txt", "data_multi" + File.separator + "millionsong_yearpred_clsfirst-test.txt", 100, 5, learn_rate, false, true);
////        BackPropagation b = new BackPropagation("data_multi" + File.separator + "millionsong_yearpred_clsfirst_small-train.txt","data_multi" + File.separator + "millionsong_yearpred_clsfirst_small-test.txt", 200, 100, 0.5, false, true);
//            b.runPerceptron();
//            System.out.println("\n");
//        }
//        BackPropagation b = new BackPropagation("data_multi" + File.separator + "millionsong_yearpred_clsfirst_small-train.txt", "data_multi" + File.separator + "million_song_rnd_sample_clsfirst_test.txt", 200, 100, 0.5, false, true);

//        BackPropagation b1 = new BackPropagation("iris.data_third_as_class.txt", "iris.data_third_as_class.txt", 5, 100, 0.5, false, false);
//        b1.runPerceptron();
//
//        BackPropagation b = new BackPropagation("iris.data.txt", "iris.data.txt", 5, 100, 0.5, false, false);
//        b.runPerceptron();
//
//        BackPropagation b3 = new BackPropagation("iris.data_no_third.txt","iris.data_no_third.txt", 5, 10, 0.5, false, false);
//        b3.runPerceptron();
//
//        ArrayList<Instance> no_third = new ArrayList<>();
//        try {
//            b1.readTestFile(no_third, "iris.data_no_third.txt", b2);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(b2.test(b1.predicted_attribute(no_third, 2)));

        BackPropagation b = new BackPropagation("data_multi" + File.separator + "complete_mirflickr_notags-train.txt", "data_multi" + File.separator + "complete_mirflickr_notags-test.txt", 150, 50, 0.01, false, true);
//        BackPropagation b = new BackPropagation("get_flickr", "data_multi" + File.separator + "complete_mirflickr_notags-test.txt", 150, 50, 0.06, false, true);
//        BackPropagation b = new BackPropagation( "data_multi" + File.separator + "complete_mirflickr-train.txt", "data_multi" + File.separator + "complete_mirflickr-test.txt", 150, 50, 0.06, false, true);
//        BackPropagation b = new BackPropagation("data_multi" + File.separator + "complete_mirflickr-train.txt", "data_multi" + File.separator + "complete_mirflickr-test.txt", 80, 100, 0.05, false, true);
//        BackPropagation b = new BackPropagation("data_multi" + File.separator + "mnist-train.txt", "data_multi" + File.separator + "mnist-test.txt", 40, 100, 0.05, false, true);
        b.test_mode = -1;
//        BackPropagation b = new BackPropagation("data_multi" + File.separator + "millionsong_yearpred_clsfirst-train.txt", "data_multi" + File.separator + "millionsong_yearpred_clsfirst-test.txt", 200, 100, 0.05, false, true);
        b.runPerceptron();
        System.out.println(b.percentages);
    }
}
