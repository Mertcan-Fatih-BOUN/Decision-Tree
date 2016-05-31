package MultiLayerPerceptronClean;

import Readers.DataSet;
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
    public static void main(String[] args) throws IOException, MatlabInvocationException, MatlabConnectionException {

        DataSet dataSet = Readers.FlickerReader.getGithubDatasetNoTag();
//        DataSet dataSet = Readers.MSDReader.getBoth();
        System.out.println("File read");

        int neuron_number = 150;
        int epoch = 50;
        double learn_rate = 0.01;
        boolean isdrawable = false;
        boolean print_each_epoch = true;

        BackPropagation b = new BackPropagation(dataSet, neuron_number, epoch, learn_rate, isdrawable, print_each_epoch);
        b.runPerceptron();

//        BackPropagation b = new BackPropagation("iris.data_no_third.txt","iris.data_no_third.txt", DataSet.TYPE.MULTI_CLASS_CLASSIFICATION, neuron_number, epoch, learn_rate, isdrawable, print_each_epoch);
//        b.runPerceptron();
//        System.out.println(b.percentages);
    }
}
