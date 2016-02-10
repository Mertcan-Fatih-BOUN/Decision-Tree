package c45;
import Utils.Instance;
import Utils.Node;
import Utils.Util;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class C45 {

//    public static int Util.CLASS_COUNT = 0;
//    public static int Util.ATTRIBUTE_COUNT = 0;
//    public static String[] Util.CLASS_NAMES = new String[]{};
    public static MatlabProxyFactory factory;
    public static MatlabProxy proxy;
    public static MatlabTypeConverter processor;

    public static ArrayList<String> matlab = new ArrayList<>();

    public static ArrayList<Instance> instances = new ArrayList<>();

    public static Node root;

    public static void main(String[] args) throws MatlabConnectionException, MatlabInvocationException {
        factory = new MatlabProxyFactory();
        proxy = factory.getProxy();
        processor = new MatlabTypeConverter(proxy);
        try {
//            Util.readFile(train_instances, "data_set_66.data.txt");
//            Util.readFile(train_instances, "iris.data.txt");
            Util.readFile(instances, "iris.data.v2.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }


        double infoT = info(instances);


        root = getNode(instances, infoT, -1, -1);

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
        plotPoints();
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
        int[] frequencies = new int[Util.CLASS_COUNT];
        Arrays.fill(frequencies, 0);

        for (Instance instance : instances) {
            for (int i = 0; i < Util.CLASS_COUNT; i++) {
                if (instance.className.equals(Util.CLASS_NAMES.get(i))) {
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

        return Util.CLASS_NAMES.get(maxIndex);
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


            for (int attribute_id = 0; attribute_id < Util.ATTRIBUTE_COUNT; attribute_id++) {

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
        if(Util.ATTRIBUTE_COUNT == 2){
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
        for (int i = 0; i < Util.CLASS_COUNT; i++) {
            if (freq[i] > 0)
                sum -= (freq[i] / size) * log(freq[i] / size);
        }
        return sum;
    }

    private static int[] freq(ArrayList<Instance> T) {
        int[] freq = new int[Util.CLASS_COUNT];
        Arrays.fill(freq, 0);

        for (Instance instance : T) {
            for (int i = 0; i < Util.CLASS_COUNT; i++) {
                if (instance.className.equals(Util.CLASS_NAMES.get(i))) {
                    freq[i]++;
                    break;
                }
            }
        }
        return freq;
    }

    private static void graph_all() throws MatlabInvocationException {
        if(Util.ATTRIBUTE_COUNT == 2) {
            proxy.eval("figure");
            String plot = "";
            plot = "plot(";
            for (int i = 0; i < graphs; i++) {
                plot += "xlin,y" + i + ",";
            }
            for (int i = 0; i < matlab.size(); i++) {
                plot += matlab.get(i) + ",";
            }
            String plot2 = "";
            for(int i = 0; i < Util.CLASS_COUNT; i++){
                plot2 += ",points_x" + i + ", points_y" + i + ", '.'";
            }
            plot2 += ")";
            proxy.eval(plot + plot2.substring(1));
            proxy.eval(plot + plot2.substring(1));
            proxy.eval("figure");
            proxy.eval("surf(xg,yg,zg)");
//            proxy.eval("figure");
//            proxy.eval("surfc(xg,yg,zg)");
//            proxy.eval("figure");
//            proxy.eval("surfc(zg)");
            proxy.eval("figure");
            String v = "[1";
            for(int i = 2; i < Util.CLASS_COUNT; i++)
                v += " " + i;
            v += "]";
            proxy.eval("contour(xg,yg,zg, " + v + ", 'ShowText','on')");
            proxy.eval("hold on");
            plot2 = "(" + plot2.substring(1);
            proxy.eval("plot" + plot2);
        }
    }

    private static void plotPoints() throws MatlabInvocationException {
        if(Util.ATTRIBUTE_COUNT == 2){
            String points_x[] = new String[Util.CLASS_COUNT];
            String points_y[] = new String[Util.CLASS_COUNT];
            int class_size = instances.size() / Util.CLASS_COUNT;
            for(int i = 0; i < Util.CLASS_COUNT; i++){
                points_x[i] = "[" + instances.get(i * instances.size() / Util.CLASS_COUNT).attributes[0];
                points_y[i] = "[" + instances.get(i * instances.size() / Util.CLASS_COUNT).attributes[1];
                for(int j = 1 + i * class_size; j < (i + 1) * class_size; j++){
                    points_x[i] += "," + instances.get(j).attributes[0];
                    points_y[i] += "," + instances.get(j).attributes[1];
                }
                points_x[i] += "]";
                points_y[i] += "]";
            }
            for(int i = 0; i < Util.CLASS_COUNT; i++) {
                proxy.eval("points_x" + i + " = " + points_x[i]);
                proxy.eval("points_y" + i + " = " + points_y[i]);
            }
            String eval1 = "[";
            String eval2 = "[";
            for(int i = 0; i < Util.CLASS_COUNT; i++){
                eval1 += "points_x" + i + " ";
                eval2 += "points_y" + i + " ";
            }
            eval1 += "]";
            eval2 += "]";

            proxy.eval("points_x = " + eval1);
            proxy.eval("points_y = " + eval2);

            proxy.eval("xmin = min(points_x)");
            proxy.eval("xmax = max(points_x)");
            proxy.eval("difference = xmax - xmin");
            proxy.eval("xmin = xmin - (difference) / 3");
            proxy.eval("xmax = xmax + (difference) / 3");
            proxy.eval("xlin = linspace(xmin, xmax)");
            proxy.eval("xlin2 = linspace(xmin, xmax,30)");
            proxy.eval("ymin = min(points_y)");
            proxy.eval("ymax = max(points_y)");
            proxy.eval("difference_y = ymax - ymin");
            proxy.eval("ymin = ymin - (difference_y) / 3");
            proxy.eval("ymax = ymax + (difference_y) / 3");
            proxy.eval("ylin = linspace(ymin, ymax,30)");
            proxy.eval("[xg, yg] = meshgrid(xlin2, ylin)");
            double[][] xg = processor.getNumericArray("xg").getRealArray2D();
            double[][] yg = processor.getNumericArray("yg").getRealArray2D();
            double[][] zg = new double[xg.length][xg[0].length];
            for(int i = 0; i < xg.length; i++){
                for(int j = 0; j < xg[0].length; j++){
                    zg[i][j] = findClass(root, new Instance(new double[]{xg[i][j], yg[i][j]}));
//                    System.out.print(zg[i][j]);
                }
//                System.out.println();
            }
            processor.setNumericArray("zg", new MatlabNumericArray(zg, null));
        }
    }

    private static double findClass(Node node, Instance i) {
        if (node.isLeaf) {
            return Util.CLASS_NAMES.indexOf(node.name);
        } else if (i.attributes[node.attributeNumber] <= node.value)
            return findClass(node.leftNode, i);
        else
            return findClass(node.rightNode, i);
    }

}