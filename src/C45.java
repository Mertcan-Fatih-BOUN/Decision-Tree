import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by mertcan on 30.9.2015.
 */
public class C45 {

    public static ArrayList<Instance> instances = new ArrayList<Instance>();

    public static void main(String[] args){
        try {
            readDataSet("iris.data.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        createDecisionTree(instances);
    }

    private static void createDecisionTree(ArrayList<Instance> instances) {
        checkBase1();
        checkBase2();
        checkBase3();
        findBestGain();
        formTree();
    }

    private static void checkBase1() {

    }

    private static void checkBase2() {

    }

    private static void checkBase3() {

    }

    private static void findBestGain() {

    }

    private static void formTree() {

    }

    private static void readDataSet(String s) throws IOException {
        FileInputStream fstream = new FileInputStream(s);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;

        while ((strLine = br.readLine()) != null)   {
            String[] parts = strLine.split(",");
            Instance instance = new Instance(parts[parts.length - 1]);
            for(int i = 0; i < parts.length - 1; i++){
                instance.attributes.add(Integer.parseInt(parts[i]));
            }
            instances.add(instance);
        }

        br.close();
    }

    public static class Instance{
        public ArrayList<Integer> attributes;
        public String className;

        public Instance(String name){
            attributes = new ArrayList<Integer>();
            className = name;
        }
        public Instance(String name, ArrayList<Integer> atts){
            attributes = atts;
            className = name;
        }
    }
}
