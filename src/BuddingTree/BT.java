package BuddingTree;


import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;

import static SDT.Util.rand;
import static SDT.Util.sigmoid;

public class BT {
    public double LEARNING_RATE;
    public int EPOCH;
    public String TRAINING_SET_FILENAME;
    public String VALIDATION_SET_FILENAME;
    public String TEST_SET_FILENAME;
    public int ATTRIBUTE_COUNT;
    public ArrayList<String> CLASS_NAMES = new ArrayList<>();
    public static double Lambda = 0.001;

    public static int count = 0;

    public static Queue<Node> split_q = new LinkedList<>();

    ArrayList<Instance> X = new ArrayList<>();
    ArrayList<Instance> V = new ArrayList<>();
    ArrayList<Instance> T = new ArrayList<>();

    public boolean isClassify;

    public Node ROOT;

    public BT(String training, String validation, String test, boolean isClassify, double learning_rate, int epoch) throws IOException {


        this.LEARNING_RATE = learning_rate;
        this.EPOCH = epoch;
        this.TRAINING_SET_FILENAME = training;
        this.VALIDATION_SET_FILENAME = validation;
        this.TEST_SET_FILENAME = test;
        this.isClassify = isClassify;
        Node.isClassify = isClassify;

        readFile(X, TRAINING_SET_FILENAME);
        readFile(V, VALIDATION_SET_FILENAME);
        readFile(T, TEST_SET_FILENAME);

        normalize(X, V, T);

        ROOT = new Node(ATTRIBUTE_COUNT);
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
                ins.attributes[i] /= stdev;
            }
            for (Instance ins : v) {
                ins.attributes[i] -= mean;
                ins.attributes[i] /= stdev;
            }
            for (Instance ins : t) {
                ins.attributes[i] -= mean;
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

    public void learnTree() {
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < X.size(); i++) indices.add(i);

//        EPOCH = 1;
        for (int e = 0; e < EPOCH; e++) {
            Collections.shuffle(indices);
            restartGradients(ROOT);
            for (int i = 0; i < X.size(); i++) {
                int j = indices.get(i);
                ROOT.backPropagate(X.get(j));
                ROOT.update();
            }
        }
    }

    private void restartGradients(Node root) {
        root.gradientSum = 0;
        if(root.leftNode != null){
            root.leftNode.gradientSum = 0;
            root.rightNode.gradientSum = 0;
        }
    }


    public String getErrors() {
        DecimalFormat format = new DecimalFormat("#.###");
        if (isClassify)
            return "Training: " + format.format(1 - ErrorOfTree(X)) + "\tValidation: " + format.format(1 - ErrorOfTree(V)) + "\tTest: " + format.format(1 - ErrorOfTree(T));
        else
            return "Training: " +format.format( ErrorOfTree(X)) + "\tValidation: " +format.format( ErrorOfTree(V) )+ "\tTest: " + format.format(ErrorOfTree(T));
    }


    double eval(Instance i) {
        if (isClassify)
            return sigmoid(ROOT.F(i));
        else
            return ROOT.F(i);
    }

    double ErrorOfTree(ArrayList<Instance> V) {
        double error = 0;
        for (Instance instance : V) {
            if (isClassify) {
                double r = instance.classValue;
                double y = eval(instance);
                if (y > 0.5) {
                    if (r != 1)
                        error++;
                } else if (r != 0)
                    error++;
            } else {
                double r = instance.classValue;
                double y = eval(instance);
//                System.out.println(r + " " + y);
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
        String[] s = line.split(" ");

        ATTRIBUTE_COUNT = s.length - 1;
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNext()) {
            double[] attributes = new double[ATTRIBUTE_COUNT];
            for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
                attributes[i] = scanner.nextDouble();

            }
            double classValue;


            if (isClassify) {
                String className = scanner.next();
                if (CLASS_NAMES.contains(className)) {
                    classValue = CLASS_NAMES.indexOf(className);
                } else {
                    CLASS_NAMES.add(className);
                    classValue = CLASS_NAMES.indexOf(className);
                }
            } else
                classValue = scanner.nextDouble();


            I.add(new Instance(classValue, attributes));
        }

    }
}