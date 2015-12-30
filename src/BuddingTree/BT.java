package BuddingTree;


import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;

import graph.Graphable;
import graph.Graph;
import misc.Instance;
import misc.Util;

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
    public double Lambda = 0.001;

    ArrayList<Instance> X = new ArrayList<>();
    ArrayList<Instance> V = new ArrayList<>();
    ArrayList<Instance> T = new ArrayList<>();

    public boolean isClassify;
    public boolean is_k_Classify;

    public static Node ROOT;
    Graph graph;

    public BT(String training, String validation, String test, boolean isClassify, double learning_rate, int epoch) throws IOException {


        this.LEARNING_RATE = learning_rate;
        this.EPOCH = epoch;
        this.TRAINING_SET_FILENAME = training;
        this.VALIDATION_SET_FILENAME = validation;
        this.TEST_SET_FILENAME = test;
        this.isClassify = isClassify;
        CLASS_NAMES = new ArrayList<>();

        readFile(X, TRAINING_SET_FILENAME);
        readFile(V, VALIDATION_SET_FILENAME);
        readFile(T, TEST_SET_FILENAME);

        is_k_Classify = X.size() > 1;
        normalize(X, V, T);
        if (isClassify && is_k_Classify)
            CLASS_COUNT = CLASS_NAMES.size();
        else
            CLASS_COUNT = 1;


        graph = new Graph((new Date().getTime()/100) % 100000 + "", this, 10);
        ROOT = new Node(this);
    }

    private void normalize(ArrayList<Instance> x, ArrayList<Instance> v, ArrayList<Instance> t) {
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
            for (Instance ins : v) {
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
                ROOT.backPropagate(X.get(j));
                ROOT.update();
            }
            graph.addEpoch(e);
            //System.out.println("Epoch " + e + " Size: " + size() + "\t" + effSize() + " \t" + getErrors());
            LEARNING_RATE *= 0.8;
        }
    }


    public String getErrors() {
        DecimalFormat format = new DecimalFormat("#.###");
        if (isClassify)
            return "Training: " + format.format(1 - ErrorOfTree(X)) + "\tValidation: " + format.format(1 - ErrorOfTree(V)) + "\tTest: " + format.format(1 - ErrorOfTree(T));
        else
//            return "Training: " +format.format( ErrorOfTree(X)) + "\tValidation: " +format.format( ErrorOfTree(V) )+ "\tTest: " + format.format(ErrorOfTree(T));
            return format.format(ErrorOfTree(X)) + "\t" + format.format(ErrorOfTree(V)) + "\t " + format.format(ErrorOfTree(T));
    }


    public double eval(Instance i) {
        double[] y = ROOT.F_last(i);
        if (isClassify) {
            if (is_k_Classify)
                return Util.argMax(y);
            else
                return y[0];
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
        for (Instance instance : V) {
            double y = eval(instance);
            if (isClassify) {
                if (y != instance.classValue)
                    error++;
            } else {
                double r = instance.classValue;
                error += (r - y) * (r - y);
            }
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
//        System.out.println(ATTRIBUTE_COUNT + " " + line);
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            s = line.split(splitter);

            double[] attributes = new double[ATTRIBUTE_COUNT];
            for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
                attributes[i] = Double.parseDouble(s[i]);
                if (isMnist)
                    attributes[i] /= 255;
            }
//            System.out.println();
            String className = s[ATTRIBUTE_COUNT];

            int classNumber;
            if (CLASS_NAMES.contains(className)) {
                classNumber = CLASS_NAMES.indexOf(className);
            } else {
                CLASS_NAMES.add(className);
                classNumber = CLASS_NAMES.indexOf(className);
            }
            I.add(new Instance(classNumber, attributes));
        }

    }
}