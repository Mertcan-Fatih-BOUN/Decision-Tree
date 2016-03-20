package BuddingTree2;


import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;

import Flickr.ReadFlickr;
import graph.Graphable;
import graph.Graph;
import mains.TreeRunner;
import misc.IndexComparator;
import misc.Instance;
import misc.Util;

import static mains.TreeRunner.LEARNING_RATE;
import static mains.TreeRunner.isMnist;

public class BT implements Graphable {
    public double LEARNING_RATE;
    public int EPOCH;
    public String TRAINING_SET_FILENAME;
    public String VALIDATION_SET_FILENAME;
    public String TEST_SET_FILENAME;
    public int ATTRIBUTE_COUNT;
    public int CLASS_COUNT;
    public ArrayList<String> CLASS_NAMES = new ArrayList<>();
    public double[] CLASS_RATES;
    public double[] current_learning_rate;
    public static double Lambda = 0.001;//in general 0.01 is optimum. make it lower if you wish to increase the tree size and vice versa.
    public int tag_size = 457;
    public int precision_at_k = 50;

    ArrayList<Instance> X = new ArrayList<>();
    ArrayList<Instance> T = new ArrayList<>();

    public boolean isClassify;
    public boolean is_k_Classify;
    public boolean is_multiple_label = false;

    public DecimalFormat format = new DecimalFormat("#.###");

    public static Node ROOT;
    Graph graph;
    //1 only tags, 2 only image
    public double[] percentages1 = new double[]{0.565,0.329,0.567,0.533,0.680,0.663,0.510,0.603,0.418,0.562,0.549,0.520,0.507,0.907,0.654,0.487,0.596,0.620,0.691,0.684,0.607,0.531,0.603,0.629
            ,0.694,0.366,0.534,0.679,0.724,0.467,0.737,0.476,0.622,0.574,0.488,0.489,0.565,0.545,0.000};
    public double[] percentages2 = new double[]{0.533,0.447,0.708,0.744,0.926,0.701,0.911,0.684,0.64,0.966,0.678,0.932,0.811,0.951,0.926,0.949,0.683,0.85,0.946,0.947,0.815,0.685,0.928,0.743,0.667,0.62,0.828,0.905,0.786,0.944,0.843,0.958,0.88,0.952,0.957,0.659,0.756,0.755,0.328};

    public BT(String training, String validation, String test, boolean isClassify, double learning_rate, int epoch) throws IOException {
//        this.isClassify = isClassify;
//        readFile(X, "data_multi" + File.separator + "complete_mirflickr.txt");
//        ReadFlickr flickr = new ReadFlickr();
//        ArrayList<Instance> instances = flickr.get_flickr_instances();
//        CLASS_NAMES = flickr.get_class_names_flickr();
//        int tag_size = instances.get(0).attributes.length;
//        System.out.println(tag_size);
//        File file1 = new File("complete_mirflickr_tags-train.txt");
//        File file2 = new File("complete_mirflickr_tags-test.txt");
//        BufferedWriter writer1 = new BufferedWriter(new FileWriter(file1, true));
//        BufferedWriter writer2 = new BufferedWriter(new FileWriter(file2, true));
//        File file3 = new File("complete_mirflickr_notags-train.txt");
//        File file4 = new File("complete_mirflickr_notags-test.txt");
//        BufferedWriter writer3 = new BufferedWriter(new FileWriter(file3, true));
//        BufferedWriter writer4 = new BufferedWriter(new FileWriter(file4, true));
//
//        for(int i = 0; i < 25000; i++){
//            String s = "";
//            String s1 = "";
//            for(int j = 0; j < 1715; j++){
//                s += X.get(i).attributes[j] + ",";
//                s1 += X.get(i).attributes[j] + ",";
//            }
//            for(int j = 0; j < tag_size; j++){
//                s += instances.get(i).attributes[j] + ",";
//            }
//            for(int j = 0; j < instances.get(i).classNumbers.size() - 1; j++){
//                s += CLASS_NAMES.get(instances.get(i).classNumbers.get(j)) + ",";
//                s1 += CLASS_NAMES.get(instances.get(i).classNumbers.get(j)) + ",";
//            }
//            s += CLASS_NAMES.get(instances.get(i).classNumbers.get(instances.get(i).classNumbers.size() - 1));
//            s1 += CLASS_NAMES.get(instances.get(i).classNumbers.get(instances.get(i).classNumbers.size() - 1));
//            if(i % 5 < 3)
//                writer1.write(s + "\n");
//            else
//                writer2.write(s + "\n");
////            if(i % 5 < 3)
////                writer3.write(s1 + "\n");
////            else
////                writer4.write(s1 + "\n");
//        }
//        writer1.flush();
//        writer1.close();
//        writer2.flush();
//        writer2.close();
//        writer3.flush();
//        writer3.close();
//        writer4.flush();
//        writer4.close();

        this.LEARNING_RATE = learning_rate;
        this.EPOCH = epoch;
        this.TRAINING_SET_FILENAME = training;
        this.VALIDATION_SET_FILENAME = validation;
        this.TEST_SET_FILENAME = test;
        this.isClassify = isClassify;
        CLASS_NAMES = new ArrayList<>();

        if(TRAINING_SET_FILENAME.contains("get_flickr")){
            ReadFlickr flickr = new ReadFlickr();
            ArrayList<Instance> instances = flickr.get_flickr_instances();
            for(int i = 0; i < instances.size(); i++){
                if(i % 5 < 3){
                    X.add(instances.get(i));
                }else{
                    T.add(instances.get(i));
                }
            }
            CLASS_NAMES= flickr.get_class_names_flickr();
            CLASS_COUNT = CLASS_NAMES.size();
            ATTRIBUTE_COUNT = X.get(0).attributes.length;
            is_multiple_label = true;
            is_k_Classify = true;
            this.isClassify = true;
            this.tag_size = flickr.tags_index.size();
        } else {
            if(TRAINING_SET_FILENAME.contains("flickr")){
                ReadFlickr flickr = new ReadFlickr();
                CLASS_NAMES= flickr.get_class_names_flickr();
                CLASS_COUNT = CLASS_NAMES.size();
                CLASS_RATES = new double[CLASS_COUNT];
                current_learning_rate = new double[CLASS_COUNT];
                is_multiple_label = true;
                is_k_Classify = true;
                this.isClassify = true;
                if(TRAINING_SET_FILENAME.contains("no"))
                    tag_size = 0;
            }
            readFile(X, TRAINING_SET_FILENAME);
            for(Instance ins:X){
                ArrayList<Integer> label_ = ins.classNumbers;
                for(Integer i:label_){
                    CLASS_RATES[i] += 1.0/X.size();
                }
            }
//            readFile(V, VALIDATION_SET_FILENAME);
            readFile(T, TEST_SET_FILENAME);
            is_k_Classify = CLASS_NAMES.size() > 2;
            if (isClassify && is_k_Classify)
                CLASS_COUNT = CLASS_NAMES.size();
            else
                CLASS_COUNT = 1;
        }
        System.out.println(CLASS_COUNT + " " + X.size() + " " + T.size() + " " + tag_size + " " + X.get(0).attributes.length + " " + X.get(0).classNumbers.size() + " " + ATTRIBUTE_COUNT);



        normalize(X, T);
        graph = new Graph((new Date().getTime()/100) % 100000 + "", this, 10);
        ROOT = new Node(this);
    }

    private void normalize(ArrayList<Instance> x, ArrayList<Instance> t) {
        for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
            double mean = 0;
            for (Instance ins : x) {
                mean += ins.attributes[i];
            }
            mean /= x.size();

            double stdev = 0;
            for (Instance ins : x) {
                stdev += (ins.attributes[i] - mean) * (ins.attributes[i] - mean);
            }
            stdev /= (x.size() - 1);
            stdev = Math.sqrt(stdev);

            for (Instance ins : x) {
                ins.attributes[i] -= mean;
                if (stdev != 0)
                    ins.attributes[i] /= stdev;
            }
            for (Instance ins : t) {
                ins.attributes[i] -= mean;
                if (stdev != 0)
                    ins.attributes[i] /= stdev;
            }

        }
    }

    public int size() {
        return ROOT.size();
    }

    public int effSize() {
        return ROOT.myEffSize();
    }

    public void learnTree() throws IOException {
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < X.size(); i++) indices.add(i);

        for (int e = 0; e < EPOCH; e++) {
            Collections.shuffle(indices);
            for (int i = 0; i < X.size(); i++) {
                int j = indices.get(i);
                Instance temp = X.get(j);
                for(int t = 0; t < CLASS_COUNT; t++){
//                    System.out.println((CLASS_RATES[t]));
                    current_learning_rate[t] = (CLASS_RATES[t]);
                }
                for(Integer t:temp.classNumbers){
                    current_learning_rate[t] = (1 - (CLASS_RATES[t]));
                }

                for(int t = 0; t < CLASS_COUNT; t++){
//                    System.out.println((CLASS_RATES[t]));
                    current_learning_rate[t] = 1;
                }

                ROOT.backPropagate(X.get(j));
                ROOT.update();
            }
            graph.addEpoch(e);
            System.out.println("Epoch " + e + " Size: " + size() + "\t" + effSize() + " \t" + getErrors());
//            outputToFile(e);
            LEARNING_RATE *= 0.99;
        }
    }

    private void outputToFile(int e) throws IOException {
        File file2 = new File("log" + File.separator + "mnist" + File.separator + "learning_rate_" + (int)(TreeRunner.LEARNING_RATE * 100) + ".txt");
        file2.getParentFile().mkdirs();
        BufferedWriter writer2 = new BufferedWriter(new FileWriter(file2, true));
        writer2.write("Epoch " + e + " Size: " + size() + "\t" + effSize() + " \t" + getErrors() + "\n");
        writer2.flush();
        writer2.close();
    }


    public String getErrors() {
        DecimalFormat format = new DecimalFormat("#.###");
        if (isClassify)
            return "Training: \n" + format.format(1 - ErrorOfTree(X))  + "\nTest: \n" + format.format(1 - ErrorOfTree(T));
        else
//            return "Training: " +format.format( ErrorOfTree(X)) + "\tValidation: " +format.format( ErrorOfTree(V) )+ "\tTest: " + format.format(ErrorOfTree(T));
            return format.format(ErrorOfTree(X)) + "\t " + format.format(ErrorOfTree(T)) + "\t Absolute: " + format.format(ErrorOfTree_absolutedifference(X)) + " " + format.format(ErrorOfTree_absolutedifference(T));
    }


    public double eval(Instance i) {
        double[] y = ROOT.F_last(i);
        if (isClassify) {
            if (is_k_Classify)
                return Util.argMax(y);
            else {
                return y[0];
            }
        } else return y[0];
    }

    @Override
    public double predicted_class(Instance instance) {
        return eval(instance);
    }

    @Override
    public int getClassCount() {
        return CLASS_COUNT;
    }

    @Override
    public int getAttributeCount() {
        return ATTRIBUTE_COUNT;
    }

    @Override
    public ArrayList<Instance> getInstances() {
        return X;
    }

    public double ErrorOfTree(ArrayList<Instance> V) {
        double error = 0;
        if(is_multiple_label) {
//            return ErrorOfTree_multiple(V);
            return mean_average_precision(V);
        }
        for (Instance instance : V) {
            double y = eval(instance);
//            System.out.println(y + " " + eval(instance) + " " + eval(instance));
            if (isClassify) {
                if (is_k_Classify) {
                    if (y != instance.classValue)
                        error++;
                    Random rr = new Random();
//                    if(rr.nextDouble() < 0.001 || (y == instance.classValue && rr.nextDouble() < 0.01))
//                        System.out.println(instance.classValue + " " + y);
                }else{
                    double r = instance.classValue;
                    if (y > 0.5) {
                        if (r != 1)
                            error++;
                    } else if (r != 0)
                        error++;
                }
            } else {
                double r = instance.classValue;
                error += (r - y) * (r - y);
            }
        }
//        if(!isClassify)
//            return Math.sqrt(error / V.size());
        return error / V.size();
    }


    public Double mean_average_precision(ArrayList<Instance> T) {
        String result = "";
        double[][] F_ = new double[T.size()][CLASS_NAMES.size()];
        int[] CLASS_RATES = new int[CLASS_COUNT];
        for (int i = 0; i < T.size(); i++) {
            F_[i] = ROOT.F_last(T.get(i));
            for(int j = 0; j < T.get(i).classNumbers.size(); j++){
                CLASS_RATES[T.get(i).classNumbers.get(j)]++;
            }
        }
        ArrayList<ArrayList<Double>> responses = new ArrayList<>();
        ArrayList<ArrayList<Integer>> indexes = new ArrayList<>();
        ArrayList<IndexComparator> index_comparators = new ArrayList<>();
        for (int i = 0; i < CLASS_COUNT; i++) {
            responses.add(new ArrayList<Double>());
            indexes.add(new ArrayList<Integer>());
        }

        for (int j = 0; j < CLASS_COUNT; j++) {
            for (int i = 0; i < T.size(); i++) {
                double prediction = F_[i][j];
//                if (prediction > 0.5) {
                    responses.get(j).add(prediction);
                    indexes.get(j).add(i);
//                }
            }
        }
        for (int i = 0; i < CLASS_COUNT; i++) {
            index_comparators.add(new IndexComparator(responses.get(i)));
        }

        Integer[][] regular_indexes = new Integer[CLASS_COUNT][0];
        for (int i = 0; i < CLASS_COUNT; i++) {
            regular_indexes[i] = index_comparators.get(i).createIndexArray();
            Arrays.sort(regular_indexes[i], index_comparators.get(i));
        }

//        System.out.println(ROOT.gama);
//        for(int i = 0; i < CLASS_COUNT; i++){
//            for(int j = 0; j < responses.get(i).size(); j++){
//                System.out.print(responses.get(i).get(regular_indexes[i][j]) + " " + regular_indexes[i][j] + "   ");
//            }
//            System.out.println();
//        }

        double[] average_precisions = new double[CLASS_COUNT];
        double[] precision_at = new double[precision_at_k];
        double sum = 0;
        for (int i = 0; i < CLASS_COUNT; i++) {
            double tmp = 0;
            double trues = 0;
            double trues_at_k = 0;
            for(int j = 0; j < indexes.get(i).size(); j++){
                if(T.get(indexes.get(i).get(regular_indexes[i][j])).classNumbers.contains(i)){
//                    if(responses.get(i).get(regular_indexes[i][j]) > 0.5) {
                        trues++;
                        if (j < precision_at_k)
                            trues_at_k++;
//                    }
                    tmp += (trues / (j + 1.0));
                }
            }
            tmp /= CLASS_RATES[i];
            average_precisions[i] = tmp;
            precision_at[i] = trues_at_k / precision_at_k;
            sum += tmp;
            result += Util.indent(CLASS_NAMES.get(i)) + "Average Precision = " + Util.indent(format.format(tmp)) + "Precision@" + precision_at_k + ": " + format.format(precision_at[i]) + "\n";
        }
        result += "\n" + (sum / CLASS_COUNT);
        System.out.println(result);
        return 1-sum / CLASS_COUNT;
    }

    public double ErrorOfTree_multiple(ArrayList<Instance> T) {
        double[][] F_ = new double[T.size()][CLASS_NAMES.size()];
        for (int i = 0; i < T.size(); i++) {
            F_[i] = ROOT.F_last(T.get(i));
        }
        int trues = 0;
        int falses = 0;
        int trues2 = 0;
        int falses2 = 0;
        for(int j = 0; j < CLASS_COUNT; j++) {
            int true_positives = 0;
            int true_negatives = 0;
            int false_positives = 0;
            int false_negatives = 0;
            for (int i = 0; i < T.size(); i++) {
                double prediction = F_[i][j];
//            if(i % 25000 == 0)
//                System.out.println(CLASS_NAMES.get(prediction) + "  " + CLASS_NAMES.get(T.get(i).classNumber));
                if (prediction > 0.5){
                    if( T.get(i).classNumbers.contains(j))
                        true_positives++;
                    else
                        false_positives++;
                } else{
                    if( !T.get(i).classNumbers.contains(j))
                        true_negatives++;
                    else
                        false_negatives++;
                }
            }
            trues += true_positives;
            falses += false_negatives;
            trues2 += true_negatives;
            falses2 += false_positives;
            //System.out.println("True: " + trues + " False: " + falses + " Percentage: " + ((double) trues / input_number_train));
            String result;
            result = CLASS_NAMES.get(j) + "\tTrue: " + " (true positives = " + true_positives + "  true negatives = " + true_negatives + ")\t" + "False: " + " (false negatives = " + false_negatives + "  false positives = " + false_positives + ")\t" + " Percentage: " + format.format((double) true_positives / (true_positives + false_negatives)) + " " + format.format((double) true_negatives / (true_negatives + false_positives)) + "\n";
            System.out.print(result);
        }
        System.out.println("\nGeneral" + ((double)trues + trues2)/(trues + trues2 + falses + falses2) + " TP: " + (double) trues / (trues + falses) + " TN: " + (double) trues2 / (trues2 + falses2));
//        if(!isClassify)
//            return Math.sqrt(error / V.size());
        return (double) falses / (trues + falses);
    }

    public double ErrorOfTree_absolutedifference(ArrayList<Instance> V) {
        double error = 0;
        for (Instance instance : V) {
            double y = eval(instance);
//            System.out.println(y + " " + eval(instance) + " " + eval(instance));
            double r = instance.classValue;
            error += Math.abs((r - y));
        }
        return error / V.size();
    }

    public String toString() {
        return ROOT.toString(1);
    }

    private void readFile(ArrayList<Instance> I, String filename) throws IOException {
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

        ATTRIBUTE_COUNT = s.length - 1;
        if(filename.contains(("complete_mirflickr"))){
            if(filename.contains("no"))
                ATTRIBUTE_COUNT = 1715;
            else
                ATTRIBUTE_COUNT = 1715 + 457;
        }
//        System.out.println(ATTRIBUTE_COUNT + " " + line);
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            s = line.split(splitter);
            if(line.contains("INPUT_DIMENSION"))
                continue;
            double[] attributes = new double[ATTRIBUTE_COUNT];
            String[] className;
            if(s.length > ATTRIBUTE_COUNT)
                className = new String[s.length - ATTRIBUTE_COUNT];
            else
                className = new String[]{"not_given"};
            if(filename.contains("clsfirst")) {
                for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
                    attributes[i] = Double.parseDouble(s[i + 1]);
//                    System.out.print(attributes[i] + "  ");
                }
                className[0] = s[0].substring(0,4);
            }else{
                for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
                    attributes[i] = Double.parseDouble(s[i]);
                }
                for(int i = ATTRIBUTE_COUNT; i < s.length; i++)
                    className[i - ATTRIBUTE_COUNT] = s[i];
            }
            double classNumber = -1;
            ArrayList<Integer> classNumbers = new ArrayList<>();
            for(int i = ATTRIBUTE_COUNT; i < s.length; i++) {
                if (CLASS_NAMES.contains(className[i - ATTRIBUTE_COUNT])) {
                    classNumber = CLASS_NAMES.indexOf(className[i - ATTRIBUTE_COUNT]);
                    classNumbers.add(CLASS_NAMES.indexOf(className[i - ATTRIBUTE_COUNT]));
                } else {
                    CLASS_NAMES.add(className[i - ATTRIBUTE_COUNT]);
                    classNumber = CLASS_NAMES.indexOf(className[i - ATTRIBUTE_COUNT]);
                    classNumbers.add(CLASS_NAMES.indexOf(className[i - ATTRIBUTE_COUNT]));
                }
            }
            if(s.length == ATTRIBUTE_COUNT){
                if (CLASS_NAMES.contains(className[0])) {
                    classNumber = CLASS_NAMES.indexOf(className[0]);
                    classNumbers.add(CLASS_NAMES.indexOf(className[0]));
                } else {
                    CLASS_NAMES.add(className[0]);
                    classNumber = CLASS_NAMES.indexOf(className[0]);
                    classNumbers.add(CLASS_NAMES.indexOf(className[0]));
                }
            }

            I.add(new Instance((int)classNumber, classNumbers, attributes));
        }
        CLASS_COUNT = CLASS_NAMES.size();
//        for(int i = 0; i < CLASS_COUNT; i++)
//            System.out.print(CLASS_NAMES.get(i) + "  ");
//        System.out.println(CLASS_COUNT);

    }
}