import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by mertcan on 30.9.2015.
 */
public class C45 {

    public static ArrayList<Instance> instances = new ArrayList<Instance>();
    public static String mostFreqClass;

    public static void main(String[] args) {
        try {
            readDataSet("iris.data.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mostFreqClass = findMostFreqClass(instances);
        System.out.println(mostFreqClass);

        createDecisionTree(instances);
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
        }  else if (checkEmptyAttribute(instances)) {
            return mostFreqClass;
        } else {
            findBestGain();
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
            return true;
        else
            return false;
    }

    private static boolean checkEmptyAttribute(ArrayList<Instance> instances) {
        if (instances.get(0).attributes.size() == 0)
            return true;
        else
            return false;
    }

    private static void findBestGain() {

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
