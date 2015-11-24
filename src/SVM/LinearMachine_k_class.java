package SVM;

import Utils.Instance;
import Utils.Node;
import Utils.Util;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


/**
 * Created by mertcan on 6.10.2015.
 */
public class LinearMachine_k_class {

    public static int REQUIRED_CLASS_COUNT = 0;
    public static int SAMPLE_SIZE = 0;

    static int graphs = 0;

    public static MatlabProxyFactory factory;
    public static MatlabProxy proxy;
    public static MatlabTypeConverter processor;


    public static ArrayList<Node> nodes = new ArrayList<>();
    public static ArrayList<Instance> instances = new ArrayList<>();


    public static void main(String[] args) throws Exception {
        factory = new MatlabProxyFactory();
        proxy = factory.getProxy();
        processor = new MatlabTypeConverter(proxy);

        try {
//            Util.readFile(instances, "data_set_66.data.txt");
//            Util.readFile(instances, "iris.data.txt");
            Util.readFile(instances, "iris.data.v2.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SAMPLE_SIZE = instances.size() / Util.CLASS_COUNT;

        plotPoints();

        createLeafNodes();

        for(int i = 0; i < log(REQUIRED_CLASS_COUNT); i++)
            findClosestNodes(i);

        for(int i = nodes.size() - 1; i >= REQUIRED_CLASS_COUNT - (Util.CLASS_COUNT - REQUIRED_CLASS_COUNT); i--) {
            String classes1 = "";
            String classes2 = "";
            for(int j = 0; j < nodes.get(i).classes[0].length; j++){
                classes1 += Util.CLASS_NAMES.get(nodes.get(i).classes[0][j]) + " ";
            }
            for(int j = 0; j < nodes.get(i).classes[1].length; j++){
                classes2 += Util.CLASS_NAMES.get(nodes.get(i).classes[1][j]) + " ";
            }
            String empty = "";
            for(int j = 0; j < (int)log(nodes.size() - i); j++)
                empty += "\t";
            System.out.println(empty + "The hyperplane to seperate classes " + classes1 + "from " + classes2 + "is:");
            classify(nodes.get(i), empty);
        }
        plotPoints2();
        graph_all();
        proxy.disconnect();
    }



    private static void createLeafNodes() {
        double log_c_count = log(Util.CLASS_COUNT);
        for(int i = 0; i < Util.CLASS_COUNT; i++)
            nodes.add(new Node(Util.CLASS_NAMES.get(i), findMassCenter(i, instances), i));
        if(Math.abs(log_c_count - (int) log_c_count) > Math.pow(10, -10)){
            REQUIRED_CLASS_COUNT = (int) Math.pow(2, (int) log_c_count);
            groupSomeClasses();
        }else{
            REQUIRED_CLASS_COUNT = Util.CLASS_COUNT;
        }


    }

    private static void groupSomeClasses() {
        double[][] distanceMatrix = createInitialDistancesMatrix(nodes);

        int size = Util.CLASS_COUNT;


        int[] columns = new int[size - 1];
        double[] zeros = new double[size];
        for(int i = 0; i < size; i++) zeros[i] = 9999999;
        for(int i = 0; i < size - 1; i++){
            if(Arrays.equals(distanceMatrix[i], zeros))
                continue;
            double cost = estimateCost(i, i + 1, distanceMatrix, size);
            columns[i] = i + 1;
            for(int j = i + 2; j < size; j++){
                double cost2 = estimateCost(i , j, distanceMatrix, size);
                if(cost2 <= cost){
                    cost = cost2;
                    columns[i] = j;
                }
            }
            distanceMatrix[i] = zeros;
            distanceMatrix[columns[i]] = zeros;
            for(int j = 0; j < size; j++) distanceMatrix[j][columns[i]] = 9999999;
            for(int j = 0; j < size; j++) distanceMatrix[j][i] = 9999999;
        }
        for(int i = 0; i < size -1; i++)
            System.out.println(columns[i]);
        int grouped = 0;
        int[] nodes_to_remove = new int[(Util.CLASS_COUNT - REQUIRED_CLASS_COUNT) * 2];
        while(grouped != Util.CLASS_COUNT - REQUIRED_CLASS_COUNT) {
            Random r = new Random();
            int i = r.nextInt(size - 1);
            if(columns[i] == 0)
                continue;
            double[] massCenter = new double[Util.ATTRIBUTE_COUNT];
            for(int j = 0; j < Util.ATTRIBUTE_COUNT; j++) {
                massCenter[j] = (nodes.get(i).massCenter[j] + nodes.get(columns[i]).massCenter[j]) / 2;
            }
            Node n = new Node(nodes.get(i).name + "___" + nodes.get(columns[i]).name, massCenter);
            n.initiallyCreated = true;
            n.leftNode = nodes.get(i);
            n.rightNode = nodes.get(columns[i]);
            nodes.get(i).parent = n;
            nodes.get(columns[i]).parent = n;
            n.isLeaf = false;
            n.classes = new int[2][1];
            n.classes[0][0] = n.leftNode.id;
            n.classes[1][0] = n.rightNode.id;
            System.out.println(i + " and " + columns[i] + " are grouped");
            nodes.add(n);
            nodes_to_remove[grouped * 2] = i;
            nodes_to_remove[grouped * 2 + 1] = columns[i];
            columns[i] = 0;
            grouped++;
        }
        Arrays.sort(nodes_to_remove);
        for(int i = nodes_to_remove.length - 1; i >= 0; i--)
            nodes.remove(nodes_to_remove[i]);

        for(int i = 0; i < nodes.size(); i++){
            String classes1 = "";
            String classes2 = "";
            if(!nodes.get(i).isLeaf)
            for(int j = 0; j < nodes.get(i).classes[0].length; j++){
                classes1 += Util.CLASS_NAMES.get(nodes.get(i).classes[0][j]) + " ";
                classes2 += Util.CLASS_NAMES.get(nodes.get(i).classes[1][j]) + " ";
            }
            System.out.println(nodes.get(i).id + " " + nodes.get(i).name + " " + classes1 + " " + classes2);
        }

    }

    private static double[][] createInitialDistancesMatrix(ArrayList<Node> nodes) {
        int size = Util.CLASS_COUNT;
        double[][] distances = new double[size][size];
        for(int i = 0; i < size; i++){
            for(int j = i + 1; j < size; j++){
                distances[i][j] = distance(nodes.get(i).massCenter, nodes.get(j).massCenter);
            }
        }
        return distances;
    }

    private static void findClosestNodes(int level) {
        double[][] distanceMatrix = createDistancesMatrix(nodes, level);

        int size = (int)(REQUIRED_CLASS_COUNT / Math.pow(2, level));

        for(int i = 0; i < size; i++){
            String s = "";
            for(int j = 0; j < size; j++){
                s += distanceMatrix[i][j] + " ";
            }
            System.out.println(s);
        }


        int[] columns = new int[size - 1];
        double[] zeros = new double[size];
        for(int i = 0; i < size; i++) zeros[i] = 9999999;
        for(int i = 0; i < size - 1; i++){
            if(Arrays.equals(distanceMatrix[i], zeros))
                continue;
            double cost = 9999999;
            columns[i] = 0;
            for(int j = i + 1; j < size; j++){
                double cost2 = 0;
                if(Arrays.equals(distanceMatrix[j], zeros))
                    cost2 = 9999999;
                else
                    cost2 = estimateCost(i , j, distanceMatrix, size);
                if(cost2 <= cost){
                    cost = cost2;
                    columns[i] = j;
                }
            }
            distanceMatrix[i] = zeros;
            distanceMatrix[columns[i]] = zeros;
            for(int j = 0; j < size; j++) distanceMatrix[j][columns[i]] = 9999999;
            for(int j = 0; j < size; j++) distanceMatrix[j][i] = 9999999;
        }
        for(int i = 0; i < size -1; i++)
            System.out.println(columns[i]);
        for(int i = 0; i < size - 1; i++) {
            if(columns[i] == 0)
                continue;
            int nodeOrder = sum_2_powers_times_class_count(level);
            double[] massCenter = new double[Util.ATTRIBUTE_COUNT];
            for(int j = 0; j < Util.ATTRIBUTE_COUNT; j++) {
                massCenter[j] = (nodes.get(i + nodeOrder).massCenter[j] + nodes.get(columns[i] + nodeOrder).massCenter[j]) / 2;
            }
            Node n = new Node(nodes.get(i + nodeOrder).name + "___" + nodes.get(columns[i] + nodeOrder).name, massCenter);
            n.leftNode = nodes.get(i + nodeOrder);
            n.rightNode = nodes.get(columns[i] + nodeOrder);
            nodes.get(i + nodeOrder).parent = n;
            nodes.get(columns[i] + nodeOrder).parent = n;
            n.isLeaf = false;
            n.classes = new int[2][];
            if(!n.leftNode.isLeaf) {
                n.classes[0] = new int[n.leftNode.classes[0].length + n.leftNode.classes[1].length];
                for(int t = 0; t < n.leftNode.classes[0].length; t++){
                    n.classes[0][t] = n.leftNode.classes[0][t];
                }
                for(int t = n.leftNode.classes[0].length; t < n.classes[0].length; t++){
                    n.classes[0][t] = n.leftNode.classes[1][t - n.leftNode.classes[0].length];
                }
            }else {
                n.classes[0] = new int[1];
                n.classes[0][0] = n.leftNode.id;
            }
            if(!n.rightNode.isLeaf) {
                n.classes[1] = new int[n.rightNode.classes[0].length + n.rightNode.classes[1].length];
                for(int t = 0; t < n.rightNode.classes[0].length; t++){
                    n.classes[1][t] = n.rightNode.classes[0][t];
                }
                for(int t = n.rightNode.classes[0].length; t < n.classes[1].length; t++){
                    n.classes[1][t] = n.rightNode.classes[1][t - n.rightNode.classes[0].length];
                }
            }else {
                n.classes[1] = new int[1];
                n.classes[1][0] = n.rightNode.id;
            }
            nodes.add(n);
        }


    }

    private static double estimateCost(int i, int i1, double[][] distanceMatrix, int size) {
        double sum = 0;
        for(int a = 0; a < size; a++)
            sum -= distanceMatrix[i][a];
        for(int a = 0; a < size; a++)
            sum -= distanceMatrix[a][i];
        for(int a = 0; a < size; a++)
            sum -= distanceMatrix[i1][a];
        for(int a = 0; a < size; a++)
            sum -= distanceMatrix[a][i1];
        sum += 3 * distanceMatrix[i][i1];
        return sum;
    }

    private static double[][] createDistancesMatrix(ArrayList<Node> nodes, int level) {
        int size = (int)(REQUIRED_CLASS_COUNT / Math.pow(2, level));
        System.out.println(size + " " + REQUIRED_CLASS_COUNT + " " + level + " " + nodes.size());
        double[][] distances = new double[size][size];
        for(int i = 0; i < size; i++){
            for(int j = i + 1; j < size; j++){
                distances[i][j] = distance(nodes.get(i + sum_2_powers_times_class_count(level)).massCenter, nodes.get(j + sum_2_powers_times_class_count(level)).massCenter);
            }
        }
        return distances;
    }

    private static int sum_2_powers_times_class_count(int level){
        double sum = 0;
        for(int i = 0; i < level; i++){
            sum += Math.pow(2, -i);
        }
        return (int)(sum * REQUIRED_CLASS_COUNT);
    }

    private static double distance(double[] massCenter, double[] massCenter1) {
        double sum = 0;
        for(int i = 0; i < massCenter.length; i++){
            sum += Math.pow(massCenter[i] - massCenter1[i], 2);
        }
        sum = Math.sqrt(sum);
        return sum;
    }

    private static double log(double v) {
        return Math.log(v) / Math.log(2);
    }

    private static double[] findMassCenter(int i, ArrayList<Instance> instances) {
        double massCenter[] = new double[Util.ATTRIBUTE_COUNT];
        for(int k = i * SAMPLE_SIZE; k < i * SAMPLE_SIZE + SAMPLE_SIZE; k++){
            for(int j = 0; j < Util.ATTRIBUTE_COUNT; j++){
                massCenter[j] += instances.get(k).attributes[j];
            }
        }
        for(int j = 0; j < Util.ATTRIBUTE_COUNT; j++){
            massCenter[j] /= SAMPLE_SIZE;
        }

        return massCenter;
    }

    private static void classify(Node n, String empty) throws Exception {
        int classInstanceSize = SAMPLE_SIZE;
        ArrayList<Instance> currentInstances = new ArrayList<>();
        Random r = new Random();
        for(int j = 0; j < n.classes[0].length; j++) {
           // System.out.println(n.classes[0][j]);
            for (int i = n.classes[0][j] * classInstanceSize; i < n.classes[0][j] * classInstanceSize + classInstanceSize; i++) {
                instances.get(i).classCode = 1;
                currentInstances.add(instances.get(i));
            }
        }
        System.out.println("");
        for(int j = 0; j < (n.classes[1].length - n.classes[0].length) * classInstanceSize * 2; j++){
            currentInstances.add(currentInstances.get(j));
        }
        for(int j = 0; j < n.classes[1].length; j++) {
           // System.out.println(n.classes[1][j]);
            for (int i = n.classes[1][j] * classInstanceSize; i < n.classes[1][j] * classInstanceSize + classInstanceSize; i++) {
                instances.get(i).classCode = -1;
                currentInstances.add(instances.get(i));
            }
        }
        for(int j = 0; j < (n.classes[0].length - n.classes[1].length) * classInstanceSize; j++){
            currentInstances.add(currentInstances.get(n.classes[0].length * classInstanceSize + r.nextInt(50)));
        }


        int size = currentInstances.size();

        double[][] H = new double[size][size];

        for(int i = 0; i < size; i++){
            Instance i1 = currentInstances.get(i);
            for(int j = 0; j < size; j++){
                Instance i2 = currentInstances.get(j);
                H[i][j] = i1.classCode * i2.classCode * dotProduct(i1.attributes, i2.attributes);
            }
        }


        double[][] A = new double[1][size];
        double[][] Aeq = new double[1][size];

        for(int i = 0; i < size; i++) {Aeq[0][i] = currentInstances.get(i).classCode; A[0][i] = 0;}


        //Send the array to MATLAB, transpose it, then retrieve it and convert it to a 2D double array

        processor.setNumericArray("H", new MatlabNumericArray(H, null));
        proxy.eval("q = ones(1, " + size + ") * -1");
        processor.setNumericArray("Aeq", new MatlabNumericArray(Aeq, null));
        processor.setNumericArray("A", new MatlabNumericArray(A, null));
        proxy.eval("b = ones(1,1) * 1");
        proxy.eval("beq = ones(1,1) * 0");
        proxy.eval("lb = ones(1, " + size + ") * -0.000001");
        proxy.eval("ub = ones(1, " + size + ") * 10000");

        //proxy.eval("options = optimoptions(@quadprog, 'Algorithm', 'active-set')");
        proxy.eval("x = quadprog(H,q,A,b,Aeq,beq,lb,ub)");

        double[][] solution = processor.getNumericArray("x").getRealArray2D();
        double sol[] = new double[size];

        for(int i = 0; i < solution.length; i++)
        {
            sol[i] = solution[i][0];
        }

        ArrayList<Integer> supports = new ArrayList<>();

        for(int i = 0; i < size; i++){
            if(sol[i] < Math.pow(10, -10) && sol[i] > -1 * Math.pow(10, -10))
                sol[i] = 0;
            else{
                supports.add(i);
            }
        }

        double[] w = new double[Util.ATTRIBUTE_COUNT];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < Util.ATTRIBUTE_COUNT; j++){
                w[j] += sol[i] * currentInstances.get(i).classCode * currentInstances.get(i).attributes[j];
            }
        }



        double bb = 0;
        for(int i = 0; i < supports.size(); i++){
            double tmp = currentInstances.get(supports.get(i)).classCode;
            for(int j = 0; j < supports.size(); j++){
                tmp -= sol[supports.get(j)] * currentInstances.get(supports.get(j)).classCode * dotProduct(currentInstances.get(supports.get(i)).attributes, currentInstances.get(supports.get(j)).attributes);
            }
            bb += tmp;
        }
        bb = 1.0 / supports.size() * bb;




        n.w0 = bb;
        n.w = w;

        int trues = 0;
        int falses = 0;
        for(int i = 0; i <  n.classes[0].length * classInstanceSize; i++){
            if(dotProduct(w, currentInstances.get(i).attributes) + bb > 0.0001)
                trues++;
            else
                falses++;
        }
        for(int i =  n.classes[0].length * classInstanceSize; i < size; i++){
            if(dotProduct(w, currentInstances.get(i).attributes) + bb < -0.0001)
                trues++;
            else
                falses++;
        }
        if(falses < 5000) {
            for (double d : w)
                System.out.println(empty + Util.doubleFormat(d));
            System.out.println(empty + "b vector\n" + empty + Util.doubleFormat(bb));
            graph(w, bb);
        }else
            classify(n, empty);
        System.out.println(empty + "trues: " + trues + " falses: " + falses);
    }

    private static void graph(double[] w, double bb) throws MatlabInvocationException {
        if(w.length == 2){

            proxy.eval("y" + graphs + "= " + w[0]/-w[1] + " * xlin" + " + " + bb/-w[1] );
            graphs++;
        }
    }

    private static void graph_all() throws MatlabInvocationException {
        if(Util.ATTRIBUTE_COUNT == 2) {
            proxy.eval("figure");
            String plot = "plot(xlin,y0";
            for (int i = 1; i < graphs; i++) {
                plot += ",xlin,y" + i;
            }
            String plot2 = "";
            for(int i = 0; i < Util.CLASS_COUNT; i++){
                plot2 += ",points_x" + i + ", points_y" + i + ", '.'";
            }
            plot2 += ")";
            proxy.eval(plot + plot2);

            proxy.eval("figure");
            proxy.eval("surf(xg,yg,zg)");
//            proxy.eval("figure");
//            proxy.eval("surfc(xg,yg,zg)");
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
        }
    }

    private static void plotPoints2() throws MatlabInvocationException {
        if(Util.ATTRIBUTE_COUNT == 2){
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
            System.out.println(nodes.size());
            for(int i = 0; i < xg.length; i++){
                for(int j = 0; j < xg[0].length; j++){
                    zg[i][j] = findClass(nodes.get(nodes.size() - 1), new Instance(new double[]{xg[i][j], yg[i][j]}));
//                    System.out.print(zg[i][j]);
                }
//                System.out.println();
            }
            processor.setNumericArray("zg", new MatlabNumericArray(zg, null));
        }
    }

    private static double findClass(Node node, Instance i) {
        if(dotProduct(node.w, i.attributes) + node.w0 > 0.0001){
            if(node.classes[0].length == 1)
                return node.classes[0][0];
            else{
                return findClass(node.leftNode, i);
            }
        }else{
            if(node.classes[1].length == 1)
                return node.classes[1][0];
            else{
                return findClass(node.rightNode, i);
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

}
