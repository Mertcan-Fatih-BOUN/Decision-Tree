package SDT;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class SDT {

    public static final double LEARNING_RATE = 10;
    public static final int MAX_STEP = 10;
    public static final int EPOCH = 25;
//    public static final String TRAINING_SET_FILENAME = "breast-train-1-1.txt";
//    public static final String VALIDATION_SET_FILENAME = "breast-validation-1-1.txt";
    public static final String TRAINING_SET_FILENAME = "data_set_sdt_2.data.txt";
    public static final String VALIDATION_SET_FILENAME = "data_set_sdt_2.data.txt";
    public static int ATTRIBUTE_COUNT;
    public static Node ROOT;

    public static ArrayList<String> CLASS_NAMES = new ArrayList<>();

    static class Node {
        Node parent = null;
        Node leftNode = null;
        Node rightNode = null;
        boolean isLeaf = true;
        boolean isLeft;
        double w0;
        double[] w = new double[ATTRIBUTE_COUNT];

        Node() {
            this.w0 = rand(-0.005, 0.005);
        }

        public void setChildren(Node l, Node r) {
            this.leftNode = l;
            this.rightNode = r;
            this.isLeaf = false;
            l.parent = this;
            r.parent = this;
            l.isLeft = true;
            r.isLeft = false;

            this.w0 = rand(-0.005, 0.005);

            for (int i = 0; i < this.w.length; i++)
                this.w[i] = rand(-0.005, 0.005);


        }

        public void deleteChilderen() {
            this.isLeaf = true;
            this.leftNode = null;
            this.rightNode = null;
        }

        private double F(Instance instance) {
            double r;
            if (this.isLeaf) {
                r = this.w0;
            } else {
                double g = this.g(instance);
                r = this.leftNode.F(instance) * g + this.rightNode.F(instance) * (1 - g);
            }
            if (this.parent == null)
                return sigmoid(r);
            else
                return r;
        }


        double g(Instance instance) {

            return sigmoid(dotProduct(this.w, instance.attributes) + this.w0);
        }

    }


    static class Instance {
        int classNumber;
        double[] attributes;

        public Instance(int classNumber, double[] attributes) {
            this.classNumber = classNumber;
            this.attributes = attributes;
        }

        public String toString() {
            String s = "";
            for (double d : attributes) {
                s += "\t" + d;
            }
            s += "\t" + classNumber;
            return s;
        }
    }


    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);
        ArrayList<Instance> X = new ArrayList<>();
        ArrayList<Instance> V = new ArrayList<>();

        readFile(X, TRAINING_SET_FILENAME);
        readFile(V, VALIDATION_SET_FILENAME);


        ROOT = new Node();
        //TODO C kodunda gördüm, paperda bulamadım
//        ROOT.w0 = 0;
//        for (Instance instance : X)
//            ROOT.w0 += instance.classNumber;
//        ROOT.w0 /= X.size();

        //TODO end

        System.out.println(ErrorOfTree(X));
        System.out.println(ErrorOfTree(V));

        LearnSoftTree(ROOT, X, V);

        System.out.println(ErrorOfTree(X));
        System.out.println(ErrorOfTree(V));


        print_results(V);
    }

    private static void readFile(ArrayList<Instance> I, String filename) throws IOException {
        String line;

        InputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);

        line = br.readLine();

        br.close();
        String[] s;
        if(!line.contains(","))
            s = line.split(" ");
        else
            s = line.split(",");

        ATTRIBUTE_COUNT = s.length - 1;
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextDouble()) {
            double[] attributes = new double[ATTRIBUTE_COUNT];
            for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
                attributes[i] = scanner.nextDouble() / 100;
            }
            String className = scanner.next();

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

    private static void LearnSoftTree(Node m, ArrayList<Instance> X, ArrayList<Instance> V) {
        System.out.println("here");
        double error_before = ErrorOfTree(V);

        double bestlw0 = 0;
        double bestrw0 = 0;
        double[] bestw = new double[ATTRIBUTE_COUNT];
        double bestw0 = 0;
        double previous_m_w0 = m.w0;

        double best_error = 1e10;

        for (int step = 0; step < MAX_STEP; step++) {
            double rate = LEARNING_RATE / (2 ^ (step + 1));
            m.setChildren(new Node(), new Node());
            for (int i = 0; i < EPOCH; i++) {
                //TODO Shuffle
                for (Instance x : X) {
                    double d = ROOT.F(x) - x.classNumber;
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
            m.deleteChilderen();
        }

        if (best_error + 1e-3 < error_before) {
            m.setChildren(new Node(), new Node());
            m.leftNode.w0 = bestlw0;
            m.rightNode.w0 = bestrw0;
            m.w = bestw;
            m.w0 = bestw0;
            LearnSoftTree(m.leftNode, X, V);
            LearnSoftTree(m.rightNode, X, V);
        } else {
            m.deleteChilderen();
            m.w0 = previous_m_w0;
        }
    }


    private static double rand(double s, double e) {
        if (e < s) {
            double t = e;
            e = s;
            s = t;
        }

        return (e - s) * Math.random() + s;
    }

    private static double ErrorOfTree(ArrayList<Instance> V) {
        //TODO C kodunda farklı, PDF'ye göre yapmaya çalıştım
        double error = 0;
        for (Instance instance : V) {
            double r = instance.classNumber;
            double y = ROOT.F(instance);
            //System.out.println(r + "\t" + y);
            if (y > 0.5) {
                if (r != 1)
                    error++;
            } else if (r != 0)
                error++;


        }
        return error / V.size();

    }

    private static void print_results(ArrayList<Instance> V) {
        int i = 0;
        for (Instance instance : V) {
            double r = instance.classNumber;
            double y = ROOT.F(instance);
            //System.out.println(r + "\t" + y);
            if (y > 0.5) {
                if (r != 1)
                    System.out.println("False:   " + i + "th instance is class 0");
                else
                    System.out.println("True:   " + i + "th instance is class 1");
            } else if (r != 0)
                System.out.println("False:   " + i + "th instance is class 1");
            else
                System.out.println("True:   " + i + "th instance is class 0");

            i++;
        }

    }

    private static double sigmoid(double x) {
        return 1.0 / (1 + Math.exp(-x));
    }

    private static double dotProduct(double[] x, double[] y) {
        double result = 0;
        for (int i = 0; i < x.length; i++)
            result += x[i] * y[i];
        return result;
    }
}