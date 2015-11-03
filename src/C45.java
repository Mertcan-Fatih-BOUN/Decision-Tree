import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class C45 {

    public static int CLASS_COUNT = 0;
    public static int ATTRIBUTE_COUNT = 0;
    public static String[] CLASS_NAMES = new String[]{};
    public static MatlabProxyFactory factory;
    public static MatlabProxy proxy;

    public static ArrayList<String> matlab = new ArrayList<>();

    public static ArrayList<Instance> instances = new ArrayList<>();

    public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException {
        factory = new MatlabProxyFactory();
        proxy = factory.getProxy();
        try {
            readDataSet("data_set_66.data.txt");
//            readDataSet("iris.data.txt");
//            readDataSet("sensor_readings_2.data.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        plotPoints();

        double infoT = info(instances);


        Node root = getNode(instances, infoT, -1, -1);

        boolean cleaned = cleanTree(root);
        while (cleaned)
            cleaned = cleanTree(root);

        System.out.println(root.toString(0));

        int trueC = 0;
        int falseC = 0;
        for (Instance instance : instances) {
            if (test(root, instance))
                trueC++;
            else
                falseC++;
        }

        System.out.println("Test: " + (double) trueC / (trueC + falseC));
        graph_all();
        proxy.disconnect();
    }

    private static boolean cleanTree(Node node) {
        if (!node.isLeaf) {
            if (node.leftNode.isLeaf && node.rightNode.isLeaf) {
                if (node.leftNode.name.equals(node.rightNode.name)) {
                    node.isLeaf = true;
                    node.name = node.rightNode.name;
                    return true;
                }
                if (node.leftNode.name.equals("failure")) {
                    node.isLeaf = true;
                    node.name = node.rightNode.name;
                    return true;
                } else if (node.rightNode.name.equals("failure")) {
                    node.isLeaf = true;
                    node.name = node.leftNode.name;
                    return true;
                }
            } else {
                return cleanTree(node.leftNode) || cleanTree(node.rightNode);
            }
        }
        return false;
    }

    private static boolean test(Node node, Instance instance) {
        if (node.isLeaf) {
            return node.name.equals(instance.className);
        } else if (instance.attributes[node.attributeNumber] <= node.value)
            return test(node.leftNode, instance);
        else
            return test(node.rightNode, instance);
    }

    private static String findMostFreqClass(ArrayList<Instance> instances) {
        int[] frequencies = new int[CLASS_COUNT];
        Arrays.fill(frequencies, 0);

        for (Instance instance : instances) {
            for (int i = 0; i < CLASS_COUNT; i++) {
                if (instance.className.equals(CLASS_NAMES[i])) {
                    frequencies[i]++;
                    break;
                }
            }
        }

        int maxIndex = 0;
        for (int i = 1; i < frequencies.length; i++) {
            int tmp = frequencies[i];
            if ((tmp > frequencies[maxIndex])) {
                maxIndex = i;
            }
        }

        return CLASS_NAMES[maxIndex];
    }


    private static boolean checkAllSame(ArrayList<Instance> instances) {
        String temp = instances.get(0).className;
        for (int i = 1; i < instances.size(); i++) {
            if (!instances.get(i).className.equals(temp))
                return false;
        }
        return true;
    }


    private static double log(double v) {
        return Math.log(v) / Math.log(2);
    }


    private static Node getNode(ArrayList<Instance> T, double infoT, int previousBestAttribute, double previousBestValue) throws MatlabInvocationException {
        if (T.size() == 0)
            return new Node("failure");
        else if (checkAllSame(T)) {
            return new Node(T.get(0).className);
        } else {
            double bestGain = -1;
            int bestAttribute = -1;
            double bestValue = -1;

            ArrayList<Instance> bestT1 = new ArrayList<>();
            ArrayList<Instance> bestT2 = new ArrayList<>();
            double bestInfoT1 = -1;
            double bestInfoT2 = -2;


            for (int attribute_id = 0; attribute_id < ATTRIBUTE_COUNT; attribute_id++) {

                double values[] = new double[T.size()];
                for (int j = 0; j < values.length; j++) {
                    values[j] = T.get(j).attributes[attribute_id];
                }
                Arrays.sort(values);

                for (int i = 0; i < values.length - 1; i++) {
                    ArrayList<Instance> T1 = new ArrayList<>();
                    ArrayList<Instance> T2 = new ArrayList<>();
                    for (Instance instance : T) {
                        if (instance.attributes[attribute_id] <= values[i])
                            T1.add(instance);
                        else
                            T2.add(instance);
                    }

                    double tmp1 = (double) T1.size() / T.size();
                    double tmp2 = (double) T2.size() / T.size();
                    double info1 = info(T1);
                    double info2 = info(T2);
                    double infoX = tmp1 * info1 + tmp2 * info2;

                    double gain = infoT - infoX;
                    if (gain > bestGain) {
                        bestGain = gain;
                        bestValue = values[i];
                        bestAttribute = attribute_id;
                        bestT1 = T1;
                        bestT2 = T2;
                        bestInfoT1 = info1;
                        bestInfoT2 = info2;
                    }
                }
            }
            if (bestAttribute == previousBestAttribute && bestValue == previousBestValue)
                return new Node(findMostFreqClass(T));

            Node ln = getNode(bestT1, bestInfoT1, bestAttribute, bestValue);
            Node rn = getNode(bestT2, bestInfoT2, bestAttribute, bestValue);

            graph(bestAttribute, bestValue);

            return new Node(bestAttribute, bestValue, ln, rn);
        }
    }
    static int graphs = 0;
    private static void graph(int attributeNumber, double value) throws MatlabInvocationException {
        if(ATTRIBUTE_COUNT == 2){
            if(attributeNumber == 0){
                matlab.add("[" + value + " " + value + "], get(gca,'ylim')");
            }else {
                proxy.eval("y" + graphs + "= " + value);
                graphs++;
            }

        }
    }

    private static double info(ArrayList<Instance> T) {
        double sum = 0;
        int[] freq = freq(T);
        double size = (double) T.size();
        for (int i = 0; i < CLASS_COUNT; i++) {
            if (freq[i] > 0)
                sum -= (freq[i] / size) * log(freq[i] / size);
        }
        return sum;
    }

    private static int[] freq(ArrayList<Instance> T) {
        int[] freq = new int[CLASS_COUNT];
        Arrays.fill(freq, 0);

        for (Instance instance : T) {
            for (int i = 0; i < CLASS_COUNT; i++) {
                if (instance.className.equals(CLASS_NAMES[i])) {
                    freq[i]++;
                    break;
                }
            }
        }
        return freq;
    }

    private static void readDataSet(String s) throws IOException {
        FileInputStream fstream = new FileInputStream(s);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        boolean firstLine = true;
        while ((strLine = br.readLine()) != null) {
            String[] parts = strLine.split(",");
            if(firstLine){
                firstLine = false;
                ATTRIBUTE_COUNT = parts.length - 1;
            }
            Instance instance = new Instance(parts[parts.length - 1]);
            for (int i = 0; i < parts.length - 1; i++) {
                instance.attributes[i] = Double.parseDouble(parts[i]);
            }
            instances.add(instance);
        }

        br.close();

        findDataSetsAttributes(instances);
    }

    private static void findDataSetsAttributes(ArrayList<Instance> instances) {
        ArrayList<String> classNames = new ArrayList<>();
        for(int i = 0; i < instances.size(); i++){
            if(i == 0)
                ATTRIBUTE_COUNT = instances.get(0).attributes.length;
            if(!classNames.contains(instances.get(i).className)){
                CLASS_COUNT++;
                classNames.add(instances.get(i).className);
            }
        }
        CLASS_NAMES = classNames.toArray(CLASS_NAMES);
    }
    private static void graph_all() throws MatlabInvocationException {
        proxy.eval("figure");
        String plot = "";
        plot = "plot(";
        for(int i = 0; i < graphs; i++){
            plot += "xlin,y" + i + ",";
        }
        for(int i = 0; i < matlab.size(); i++){
            plot += matlab.get(i) + ",";
        }
        plot += "points_x, points_y, '.')";
        System.out.println(plot);
        proxy.eval(plot);
        proxy.eval(plot);
    }

    private static void plotPoints() throws MatlabInvocationException {
        if(ATTRIBUTE_COUNT == 2){
            String points_x = "[" + instances.get(0).attributes[0];
            String points_y = "[" + instances.get(0).attributes[1];
            for(int i = 1; i < instances.size(); i++){
                points_x += "," + instances.get(i).attributes[0];
                points_y += "," + instances.get(i).attributes[1];
            }
            points_x += "]";
            points_y += "]";
            proxy.eval("points_x = " + points_x);
            proxy.eval("points_y = " + points_y);
            proxy.eval("xmin = min(points_x) - 50");
            proxy.eval("xmax = max(points_x) + 50");
            proxy.eval("xlin = linspace(xmin, xmax)");
        }
    }

    public static class Instance {
        public double attributes[] = new double[ATTRIBUTE_COUNT];
        public String className;

        public Instance(String name) {
            className = name;
        }
    }
}