import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by mertcan on 30.9.2015.
 */
public class C45 {

    public static ArrayList<Instance> instances = new ArrayList<Instance>();
    public static String mostFreqClass;

    public static void main(String[] args) {
//        System.out.println(log(4) + " " + log(8) + " " + log(10));
        try {
            readDataSet("iris.data.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mostFreqClass = findMostFreqClass(instances);


        System.out.println(createDecisionTree(instances));
    }

    private static String findMostFreqClass(ArrayList<Instance> instances) {
        HashMap<String, Integer> names = new HashMap<String, Integer>();
        String freqName = "";
        int freq = 0;
        for (int i = 0; i < instances.size(); i++) {
            if (names.get(instances.get(i).className) == null) {
                names.put(instances.get(i).className, 1);
                if (freq == 0) {
                    freqName = instances.get(i).className;
                    freq = 1;
                }
            } else {
                names.put(instances.get(i).className, names.get(instances.get(i).className) + 1);
                if (freq < names.get(instances.get(i).className)) {
                    freqName = instances.get(i).className;
                    freq = names.get(instances.get(i).className);
                }
            }
        }
        return freqName;
    }

    private static String createDecisionTree(ArrayList<Instance> instances) {
        if (checkEmptiness(instances)) {
            return "failure";
        } else if (checkAllSame(instances)) {
            return instances.get(0).className;
        } else if (checkEmptyAttribute(instances)) {
            return mostFreqClass;
        } else {
            int attributeNumber = findBestGain(instances);
            return formTree();
        }
    }

    private static boolean checkAllSame(ArrayList<Instance> instances) {
        String temp = instances.get(0).className;
        for (int i = 1; i < instances.size(); i++) {
            if (!instances.get(i).className.equals(temp))
                return false;
        }
        return true;
    }

    private static boolean checkEmptiness(ArrayList<Instance> instances) {
        if (instances.size() > 0)
            return false;
        else
            return true;
    }

    private static boolean checkEmptyAttribute(ArrayList<Instance> instances) {
        if (instances.get(0).attributes.size() == 0)
            return true;
        else
            return false;
    }

    private static int findBestGain(ArrayList<Instance> instances) {
        double bestGain = 0;
        int bestGainAttribute = -1;
        double info = findEntropy(instances);
        for (int i = 0; i < instances.get(0).attributes.size(); i++) {
            double tempGain = findGain(instances, i, info);
            if (bestGain <= tempGain) {
                bestGain = tempGain;
                bestGainAttribute = i;
            }
        }
        return bestGainAttribute;
    }

    private static double findGain(ArrayList<Instance> instances, int attributeNumber, double info) {
        double values[] = new double[instances.size()];
        System.out.print(instances.size());
        for (int j = 0; j < values.length; j++) {
            values[j] = instances.get(j).attributes.get(attributeNumber);
        }
        Arrays.sort(values);

        double bestOption = values[0];
        double bestGain = findGainForLessThan(instances, values[0], 0, attributeNumber, info);
        for (int i = 1; i < values.length - 1; i++) {
            double tempGain = findGainForLessThan(instances, values[i], i, attributeNumber, info);
        }
        return 0;
    }

    private static double findGainForLessThan(ArrayList<Instance> instances, double value, int place, int attributeNumber, double info) {
        HashMap<String, Frequencies> map = hashMapOfClassNames(instances, attributeNumber, value);
        double infoThis = 0;
        double temp1 = 0;
        for (String key : map.keySet()) {
            int temp = map.get(key).freqLess;
            temp1 += temp;
            temp1 -= (double) temp / place * log((double) temp / place);
        }
        double temp2 = 0;
        for (String key : map.keySet()) {
            int temp = map.get(key).fregGreater;
            temp2 -= (double) temp / (instances.size() - place) * log((double) temp / (instances.size() - place));
        }
        infoThis = (double) place / instances.size() * temp1 + (double) (instances.size() - place) / instances.size() * temp2;
        System.out.println(infoThis + "asd");
        return infoThis;
    }


    private static double findEntropy(ArrayList<Instance> instances) {
        HashMap<String, Integer> classes = hashMapOfClassNames(instances);
        int frequencies[] = new int[classes.keySet().size()];
        int i = 0;
        int total = 0;
        for (String key : classes.keySet()) {
            int temp = classes.get(key);
            frequencies[i++] = temp;
            total += temp;
        }
        double info = 0;
        for (int j = 0; j < frequencies.length; j++) {
            info -= (double) frequencies[j] / total * log((double) frequencies[j] / total);
        }
        return info;
    }

    private static double log(double v) {
        return Math.log(v) / Math.log(2);
    }

    private static HashMap<String, Integer> hashMapOfClassNames(ArrayList<Instance> instances) {
        HashMap<String, Integer> names = new HashMap<String, Integer>();
        for (int i = 0; i < instances.size(); i++) {
            if (names.get(instances.get(i).className) == null) {
                names.put(instances.get(i).className, 1);
            } else {
                names.put(instances.get(i).className, names.get(instances.get(i).className) + 1);
            }
        }
        return names;
    }

    private static HashMap<String, Frequencies> hashMapOfClassNames(ArrayList<Instance> instances, int attributeNumber, double lessThan) {
        HashMap<String, Frequencies> names = new HashMap<String, Frequencies>();
        for (int i = 0; i < instances.size(); i++) {
            if (instances.get(i).attributes.get(attributeNumber) <= lessThan) {
                if (names.get(instances.get(i).className) == null) {
                    names.put(instances.get(i).className, new Frequencies(1 , 0));
                } else {
                    names.put(instances.get(i).className, new Frequencies(names.get(instances.get(i).className).freqLess + 1, names.get(instances.get(i).className).fregGreater));
                }
            }else{
                if (names.get(instances.get(i).className) == null) {
                    names.put(instances.get(i).className, new Frequencies(0 , 1));
                } else {
                    names.put(instances.get(i).className, new Frequencies(names.get(instances.get(i).className).freqLess, names.get(instances.get(i).className).fregGreater + 1));
                }
            }
        }
        return names;
    }

    public static class Frequencies{
        int freqLess = 0;
        int fregGreater = 0;

        public Frequencies(int x, int y){
            freqLess = x;
            fregGreater = y;
        }
    }

    private static String formTree() {
        return "tree";
    }

    private static void readDataSet(String s) throws IOException {
        FileInputStream fstream = new FileInputStream(s);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

        while ((strLine = br.readLine()) != null) {
            String[] parts = strLine.split(",");
            Instance instance = new Instance(parts[parts.length - 1]);
            for (int i = 0; i < parts.length - 1; i++) {
                instance.attributes.add(Double.parseDouble(parts[i]));
            }
            instances.add(instance);
        }

        br.close();
    }

    public static class Instance {
        public ArrayList<Double> attributes;
        public String className;

        public Instance(String name) {
            attributes = new ArrayList<Double>();
            className = name;
        }

        public Instance(String name, ArrayList<Double> atts) {
            attributes = atts;
            className = name;
        }
    }
}
