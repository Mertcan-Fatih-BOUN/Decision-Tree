package SDT;


import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

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
        learnIteration(ROOT, X, V);
    }

    public String getErrors() {
        return "Training: " + ErrorOfTree(X) + " Validation: " + ErrorOfTree(V) + " Test: " + ErrorOfTree(T);
    }


    private void learnIteration(Node m, ArrayList<Instance> X, ArrayList<Instance> V) {
        double error_before = ErrorOfTree(V);

        double bestlw0 = 0;
        double bestrw0 = 0;
        double[] bestw = new double[ATTRIBUTE_COUNT];
        double bestw0 = 0;
        double previous_m_w0 = m.w0;

        double best_error = 1e10;

        for (int step = 0; step < MAX_STEP; step++) {
            double rate = LEARNING_RATE / (2 ^ (step + 1));
            m.setChildren(new Node(ATTRIBUTE_COUNT), new Node(ATTRIBUTE_COUNT));
            for (int i = 0; i < EPOCH; i++) {
                //TODO Shuffle
                for (Instance x : X) {
                    double d = ROOT.F(x) - x.classValue;
                    Node t = m;
                    while (t.parent != null) {
                        Node p = t.parent;
                        if (t.isLeft)
                            d = d * p.g(x);
                        else
                            d = d * (1 - p.g(x));
                        t = p;
                    }
                    double vmx = m.g(x);
                    double b = d * (m.leftNode.F(x) - m.rightNode.F(x));
                    for (int j = 0; j < m.w.length; j++)
                        m.w[j] -= rate * b * vmx * (1 - vmx) * x.attributes[j];


                    m.leftNode.w0 -= rate * d * vmx;
                    m.rightNode.w0 -= rate * d * (1 - vmx);
                }
            }

            double new_error = ErrorOfTree(V);

            if (new_error < best_error) {
                bestw = m.w;
                bestlw0 = m.leftNode.w0;
                bestrw0 = m.rightNode.w0;
                bestw0 = m.w0;
                best_error = new_error;
            }
            m.deleteChildren();
        }

        if (best_error + 1e-3 < error_before) {
            m.setChildren(new Node(ATTRIBUTE_COUNT), new Node(ATTRIBUTE_COUNT));
            m.leftNode.w0 = bestlw0;
            m.rightNode.w0 = bestrw0;
            m.w = bestw;
            m.w0 = bestw0;
            learnIteration(m.leftNode, X, V);
            learnIteration(m.rightNode, X, V);
        } else {
            m.deleteChildren();
            m.w0 = previous_m_w0;
        }
    }

    private double ErrorOfTree(ArrayList<Instance> V) {
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