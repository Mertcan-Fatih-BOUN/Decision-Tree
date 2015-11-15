package SDT;


import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

import static SDT.Util.rand;

public class SDT {
    public double LEARNING_RATE;
    public int MAX_STEP;
    public int EPOCH;
    public String TRAINING_SET_FILENAME;
    public String VALIDATION_SET_FILENAME;
    public String TEST_SET_FILENAME;
    public int ATTRIBUTE_COUNT;
    public ArrayList<String> CLASS_NAMES = new ArrayList<>();

    ArrayList<Instance> X = new ArrayList<>();
    ArrayList<Instance> V = new ArrayList<>();
    ArrayList<Instance> T = new ArrayList<>();

    public boolean isClassify;

    public Node ROOT;

    public SDT(String training, String validation, String test, boolean isClassify, double learning_rate, int epıch, int max_step) throws IOException {


        this.LEARNING_RATE = learning_rate;
        this.MAX_STEP = max_step;
        this.EPOCH = epıch;
        this.TRAINING_SET_FILENAME = training;
        this.VALIDATION_SET_FILENAME = validation;
        this.TEST_SET_FILENAME = test;
        this.isClassify = isClassify;


        readFile(X, TRAINING_SET_FILENAME);
        readFile(V, VALIDATION_SET_FILENAME);
        readFile(T, TEST_SET_FILENAME);
    }

    public void learnTree() {
        ROOT = new Node(ATTRIBUTE_COUNT);

        ROOT.w0 = 0;
        for (Instance i : X)
            ROOT.w0 += i.classValue;
        ROOT.w0 /= X.size();


        ROOT.splitNode(X,V,this);
    }

    public String getErrors() {
        return "Training: " + ErrorOfTree(X) + " Validation: " + ErrorOfTree(V) + " Test: " + ErrorOfTree(T);
    }


    double ErrorOfTree(ArrayList<Instance> V) {
        double error = 0;
        for (Instance instance : V) {
            if (isClassify) {
                double r = instance.classValue;
                double y = ROOT.F(instance);
                if (y > 0.5) {
                    if (r != 1)
                        error++;
                } else if (r != 0)
                    error++;
            } else {
                double r = instance.classValue;
                double y = ROOT.F(instance);
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