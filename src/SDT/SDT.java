package SDT;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class SDT {

    public static final int LEARNING_RATE = 1;
    public static final int EPOCH = 20;
    public static final String TRAINING_SET_FILENAME = "ringnorm-train-1-1.txt";
    public static final String VALIDATION_SET_FILENAME = "ringnorm-validation-1-1.txt";
    public static int ATTRIBUTE_COUNT;
    public static Node ROOT;


    static class Node {
        Node parent = null;
        Node leftNode = null;
        Node rightNode = null;
        boolean isLeaf = true;
        boolean isLeft;
        double w0;
        double[] w = new double[ATTRIBUTE_COUNT];


        double F(Instance instance) {
            if (this.isLeaf) {
                return this.w0;
            } else {
                double g = this.g(instance);
                return this.leftNode.F(instance) * g + this.rightNode.F(instance) * (1 - g);
            }
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


//        for (Instance i : X)
//            System.out.println(i.toString());

        ROOT = new Node();
        //TODO C kodunda gördüm, paperda bulamadım
        ROOT.w0 = 0;
        for (Instance instance : X)
            ROOT.w0 += instance.classNumber;
        ROOT.w0 /= X.size();
        //TODO end

        LearnSoftTree(ROOT, X, V);

        System.out.println(1 - ErrorOfTree(X));
        System.out.println(1 - ErrorOfTree(V));


    }

    private static void readFile(ArrayList<Instance> I, String filename) throws IOException {
        String line;

        InputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);

        line = br.readLine();

        br.close();
        String[] s = line.split(" ");

        ATTRIBUTE_COUNT = s.length - 1;
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextDouble()) {
            double[] attributes = new double[ATTRIBUTE_COUNT];
            for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
                attributes[i] = scanner.nextDouble();
            }
            int classNumber = scanner.nextInt();
            I.add(new Instance(classNumber, attributes));
        }

    }

    private static void LearnSoftTree(Node m, ArrayList<Instance> X, ArrayList<Instance> V) {
        System.out.println("here");
        double error_before = ErrorOfTree(V);
        for (int i = 0; i < m.w.length; i++)
            m.w[i] = rand(-0.01, 0.01);


        m.leftNode = new Node();
        m.leftNode.isLeft = true;

        m.rightNode = new Node();
        m.rightNode.isLeft = false;

        m.leftNode.w0 = rand(-0.01, 0.01);
        m.rightNode.w0 = rand(-0.01, 0.01);


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
                    m.w[j] = m.w[j] - LEARNING_RATE * b * vmx * (1 - vmx) * x.attributes[j];
                m.leftNode.w0 -= LEARNING_RATE * d * vmx;
                m.rightNode.w0 -= LEARNING_RATE * d * (1 - vmx);
            }
        }

        double error_after = ErrorOfTree(V);

        if (error_after < error_before) {
            LearnSoftTree(m.leftNode, X, V);
            LearnSoftTree(m.rightNode, X, V);
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
            error += r * Math.log(y) + (1 - r) * Math.log(1 - y);

        }
        return error / V.size();

    }


    private static double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }

    private static double dotProduct(double[] x, double[] y) {
        double result = 0;
        for (int i = 0; i < x.length; i++)
            result += x[i] * y[i];
        return result;
    }
}
