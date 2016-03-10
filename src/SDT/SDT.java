package SDT;


import graph.Graph;
import graph.Graphable;
import misc.Instance;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import static mains.TreeRunner.isMnist;
import static misc.Util.argMax;

public class SDT implements Graphable {
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
    Graph graph;
    int count = 0;
    public Node ROOT;

    public final int CLASS_COUNT;

    public SDT(String training, String validation, String test, boolean isClassify, double learning_rate, int epoch, int max_step) throws IOException {

        CLASS_NAMES = new ArrayList<>();
        this.LEARNING_RATE = learning_rate;
        this.MAX_STEP = max_step;
        this.EPOCH = epoch;
        this.TRAINING_SET_FILENAME = training;
        this.VALIDATION_SET_FILENAME = validation;
        this.TEST_SET_FILENAME = test;
        this.isClassify = isClassify;

        readFile(X, TRAINING_SET_FILENAME);
        readFile(V, VALIDATION_SET_FILENAME);
        readFile(T, TEST_SET_FILENAME);

        if (!isClassify)
            CLASS_COUNT = 1;
        else if (CLASS_NAMES.size() == 2)
            CLASS_COUNT = 1;
        else
            CLASS_COUNT = CLASS_NAMES.size();


        graph = new Graph((new Date().getTime() / 100) % 100000 + "", this, 10);
        if (!isMnist)
            normalize(X, V, T);
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

    public void learnTree() {
        ROOT = new Node(this, ATTRIBUTE_COUNT);

        ROOT.rho = new double[CLASS_COUNT];

        if (CLASS_COUNT == 1) {
            for (Instance i : X)
                ROOT.rho[0] += i.classValue;
            ROOT.rho[0] /= X.size();
        } else {
            double inc = 1.0 / X.size();
            for (Instance i : X)
                ROOT.rho[(int) i.classValue] += inc;
        }

        ROOT.splitNode(X, V, this);

//        split_q.add(ROOT);
//        while (!split_q.isEmpty()){
//            Node n = split_q.remove();
//            n.splitNode(X, V, this);
//        }
    }

    public String getErrors() {
        DecimalFormat format = new DecimalFormat("#.###");
        if (isClassify)
            return "Training: " + format.format(1 - ErrorOfTree(X)) + "\tValidation: " + format.format(1 - ErrorOfTree(V)) + "\tTest: " + format.format(1 - ErrorOfTree(T));
        else
//            return "Training: " +format.format( ErrorOfTree(X)) + "\tValidation: " +format.format( ErrorOfTree(V) )+ "\tTest: " + format.format(ErrorOfTree(T));
            return format.format(ErrorOfTree(X)) + "\t" + format.format(ErrorOfTree(V)) + "\t " + format.format(ErrorOfTree(T)) + "\t Absolute: " + format.format(ErrorOfTree_absolutedifference(X)) + " " + format.format(ErrorOfTree_absolutedifference(T));
    }


    double eval(Instance i) {
        if (CLASS_COUNT == 1)
            return ROOT.F(i)[0];
        else
            return argMax(ROOT.F(i));
    }

    double ErrorOfTree(ArrayList<Instance> V) {
        double error = 0;
        for (Instance instance : V) {
            if (isClassify) {
                if (ROOT.rho.length == 1) {
                    double r = instance.classValue;
                    double y = eval(instance);
                    if (y > 0.5) {
                        if (r != 1)
                            error++;
                    } else if (r != 0)
                        error++;
                } else {
                    int r = (int) instance.classValue;
                    int y = (int) eval(instance);
//                    System.out.println(r + "  " + y);
                    if (y != r)
                        error++;
                }
            } else {
                double r = instance.classValue;
                double y = eval(instance);
                error += (r - y) * (r - y);
            }
        }
        if (!isClassify)
            return Math.sqrt(error / V.size());
        return error / V.size();

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
//        System.out.println(ATTRIBUTE_COUNT + line);
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
            String className = s[ATTRIBUTE_COUNT];

            double classNumber;
            if (isClassify) {
                if (CLASS_NAMES.contains(className)) {
                    classNumber = CLASS_NAMES.indexOf(className);
                } else {
                    CLASS_NAMES.add(className);
                    classNumber = CLASS_NAMES.indexOf(className);
                }
            } else
                classNumber = Double.parseDouble(className);
            I.add(new Instance(classNumber, attributes));
        }

    }

    @Override
    public double predicted_class(misc.Instance instance) {
        return eval(instance);
    }

    @Override
    public int getClassCount() {
        return CLASS_NAMES.size();
    }

    @Override
    public int getAttributeCount() {
        return ATTRIBUTE_COUNT;
    }

    @Override
    public ArrayList<misc.Instance> getInstances() {
        return X;
    }
}