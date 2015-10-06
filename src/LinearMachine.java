import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by mertcan on 6.10.2015.
 */
public class LinearMachine {

    public static int CLASS_COUNT = 0;
    public static int ATTRIBUTE_COUNT = 0;
    public static String[] CLASS_NAMES = new String[]{};

    public static ArrayList<Instance> instances = new ArrayList<>();

    public static void main(String[] args) {
        try {
            readDataSet("iris.data.txt");
//            readDataSet("sensor_readings_2.data.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < instances.size() - 1; i++){
            for(int j = 1; j < instances.size(); j++){
                classify(i, j);
            }
        }
    }

    private static void classify(int class_1, int class_2) {
        for(int i = class_1; i < class_1 + 50; i++)
            instances.get(i).classCode = 1;
        for(int i = class_2; i < class_2 + 50; i++)
            instances.get(i).classCode = 1;

        int size = instances.size() / CLASS_COUNT * 2;
        double[][] H = new double[size][size];

        for(int i = 0; i < size; i++){
            Instance i1 = instances.get(i);
            for(int j = 0; j < size; j++){
                Instance i2 = instances.get(j);
                H[i][j] = i1.classCode * i2.classCode * dotProduct(i1.attributes, i2.attributes);
            }
        }

        
    }

    private static double dotProduct(double[] array1, double[] array2) {
        if(array1.length != array2.length)
            return -1;
        double sum = 0;
        for(int i = 0; i < array1.length; i++){
            sum += array1[i] * array2[i];
        }
        return sum;
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

    public static class Instance {
        public double attributes[] = new double[ATTRIBUTE_COUNT];
        public String className;
        public int classCode = -1;

        public Instance(String name) {
            className = name;
        }
    }
}
