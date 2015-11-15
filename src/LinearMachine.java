import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;

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

    public static ArrayList<double[]> w_s = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        try {
            readDataSet("data_set_1.data.txt");
//            readDataSet("iris.data.txt");
//            readDataSet("sensor_readings_2.data.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < CLASS_COUNT - 1; i++){
            for(int j = i + 1; j < CLASS_COUNT; j++){
                classify(i, j);
            }
        }
    }

    private static void classify(int class_1, int class_2) throws Exception {
        System.out.println(class_1 + " " + class_2);
        int classInstanceSize = instances.size() / CLASS_COUNT;
        ArrayList<Instance> currentInstances = new ArrayList<>();
        for(int i = class_1 * classInstanceSize; i < class_1 * classInstanceSize + classInstanceSize; i++) {
            instances.get(i).classCode = 1;
            currentInstances.add(instances.get(i));
        }
        for(int i = class_2 * classInstanceSize; i < class_2 * classInstanceSize + classInstanceSize; i++) {
            instances.get(i).classCode = -1;
            currentInstances.add(instances.get(i));
        }



        int size = instances.size() / CLASS_COUNT * 2;
        double[][] H = new double[size][size];

        for(int i = 0; i < size; i++){
            Instance i1 = currentInstances.get(i);
            for(int j = 0; j < size; j++){
                Instance i2 = currentInstances.get(j);
                H[i][j] = i1.classCode * i2.classCode * dotProduct(i1.attributes, i2.attributes);
            }
        }

        double[] alpha = new double[size];
        for(int i = 0; i < size; i++) alpha[i] = 1;

        double[] q = new double[size];
        for(int i = 0; i < size; i++) q[i] = -1;

        double[][] A = new double[1][size];
        double[] b = new double[]{0};
        for(int i = 0; i < size/2; i++) A[0][i] = 1;
        for(int i = size/2; i < size; i++) A[0][i] = -1;


        PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(H, q, 0);


        ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[size];

//        double[] tmp = new double[size];
//        for(int j = 0; j < size; j++) tmp[j] = 0;
//        tmp[0] = -1;
        for(int i = 0; i < size; i++){
            double[] tmp = new double[size];
            for(int j = 0; j < size; j++) tmp[j] = 0;
            tmp[i] = -1;
            inequalities[i] = new LinearMultivariateRealFunction(tmp, 0);
//            tmp[i] = 0;
//            tmp[i + 1] = -1;
        }


        OptimizationRequest or = new OptimizationRequest();
        or.setF0(objectiveFunction);
        or.setInitialPoint(alpha);
        or.setA(A);
        or.setB(b);
        or.setFi(inequalities);
        or.setToleranceFeas(1.E-12);
        or.setTolerance(1.E-12);


        JOptimizer opt = new JOptimizer();
        opt.setOptimizationRequest(or);
        int returnCode = opt.optimize();
        double[] sol = opt.getOptimizationResponse().getSolution();
        ArrayList<Integer> supports = new ArrayList<>();
        for(int i = 0; i < size; i++){
            if(sol[i] < Math.pow(10, -10) && sol[i] > -1 * Math.pow(10, -10))
                sol[i] = 0;
            else{
                supports.add(i);
            }
        }
//        for(double d:sol)
//        System.out.println(d);
//
//        System.out.println(dotProduct(A[0], sol));

        double[] w = new double[ATTRIBUTE_COUNT];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < ATTRIBUTE_COUNT; j++){
                w[j] += sol[i] * currentInstances.get(i).classCode * currentInstances.get(i).attributes[j];
            }
        }

        System.out.println("Normal vector to the hyperplane: ");
        for(double d:w)
            System.out.println(d);

        double bb = 0;
        for(int i = 0; i < supports.size(); i++){
            double tmp = currentInstances.get(supports.get(i)).classCode;
            for(int j = 0; j < supports.size(); j++){
                tmp -= sol[supports.get(j)] * currentInstances.get(supports.get(j)).classCode * dotProduct(currentInstances.get(supports.get(i)).attributes, currentInstances.get(supports.get(j)).attributes);
            }
            bb += tmp;
        }
        bb = 1.0 / supports.size() * bb;
        System.out.println("b vector\n" + bb);

        int trues = 0;
        int falses = 0;
        for(int i = 0; i < classInstanceSize; i++){
            if(dotProduct(w, currentInstances.get(i).attributes) + bb > 0)
                trues++;
            else
                falses++;
        }
        for(int i = classInstanceSize; i < size; i++){
            if(dotProduct(w, currentInstances.get(i).attributes) + bb < 0)
                trues++;
            else
                falses++;
        }
        System.out.println("trues: " + trues + " falses: " + falses);
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
