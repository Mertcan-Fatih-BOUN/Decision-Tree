package SDTUpgrade;


import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;

import static SDTUpgrade.Util.sigmoid;

public class SDT {
    SDT leftSDT;
    SDT rightSDT;
    SDT middleSDT;

    SDT parent;

    public boolean isLeaf = true;

    public double LEARNING_RATE;
    public int MAX_STEP;
    public int EPOCH;
    public String TRAINING_SET_FILENAME;
    public String VALIDATION_SET_FILENAME;
    public String TEST_SET_FILENAME;
    public int ATTRIBUTE_COUNT;
    public static ArrayList<String> CLASS_NAMES = new ArrayList<>();

    public static Queue<Node> split_q = new LinkedList<>();

    ArrayList<Instance> X = new ArrayList<>();
    ArrayList<Instance> V = new ArrayList<>();
    ArrayList<Instance> T = new ArrayList<>();

    public static boolean isClassify;

    public Node ROOT;

    public SDT(String training, String validation, String test, boolean isClassify, double learning_rate, int epıch, int max_step) throws IOException {

        CLASS_NAMES = new ArrayList<>();
        this.LEARNING_RATE = learning_rate;
        this.MAX_STEP = max_step;
        this.EPOCH = epıch;
        this.TRAINING_SET_FILENAME = training;
        this.VALIDATION_SET_FILENAME = validation;
        this.TEST_SET_FILENAME = test;
        this.isClassify = isClassify;


        readFile(X, TRAINING_SET_FILENAME);
        readFile(V, VALIDATION_SET_FILENAME);
        readFile(T, TEST_SET_FILENAME);

        if(!SDTMain.isMnist)
            normalize(X, V, T);

        isLeaf = true;

        learnTree();
    }

    public SDT(ArrayList<Instance> X, ArrayList<Instance> V, ArrayList<Instance> T, boolean isClassify, double learning_rate, int epıch, int max_step) {
        this.X = X;
        this.V = V;
        this.T = T;
        isLeaf = true;
        this.isClassify = isClassify;
        this.LEARNING_RATE = learning_rate;
        this.MAX_STEP = max_step;
        this.EPOCH = epıch;
        learnTree();
    }

    private void normalize(ArrayList<Instance> x, ArrayList<Instance> v, ArrayList<Instance> t) {
        for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
            double mean = 0;
            for (Instance ins : x) {
                mean += ins.attributes[i];
            }
            mean /= x.size();

            double stdev = 0;
            for (Instance ins : x) {
                stdev += (ins.attributes[i] - mean) * (ins.attributes[i] - mean);
            }
            stdev /= (x.size() - 1);
            stdev = Math.sqrt(stdev);

            for (Instance ins : x) {
                ins.attributes[i] -= mean;
                if(stdev != 0)
                    ins.attributes[i] /= stdev;
            }
            for (Instance ins : v) {
                ins.attributes[i] -= mean;
                if(stdev != 0)
                    ins.attributes[i] /= stdev;
            }
            for (Instance ins : t) {
                ins.attributes[i] -= mean;
                if(stdev != 0)
                    ins.attributes[i] /= stdev;
            }

        }
    }

    public int size() {
        int i = ROOT.size();
        if (middleSDT != null) {
            i += middleSDT.size();
        }if (leftSDT != null) {
            i += leftSDT.size();
        }if (rightSDT != null) {
            i += rightSDT.size();
        }
        return i;
    }

    public int effSize() {
        int i = 1;
        if (middleSDT != null) {
            i += middleSDT.effSize();
        }if (leftSDT != null) {
            i += leftSDT.effSize();
        }if (rightSDT != null) {
            i += rightSDT.effSize();
        }
        return i;
    }

    public void learnTree() {
        ROOT = new Node(ATTRIBUTE_COUNT);

        if(isClassify && CLASS_NAMES.size() > 2){
            ROOT.rho = new double[CLASS_NAMES.size()];
            for (Instance i : X)
                ROOT.rho[(int)i.classValue] += 1.0/X.size();
        }else {
            ROOT.rho = new double[1];
            ROOT.rho[0] = 0;
            for (Instance i : X)
                ROOT.rho[0] += i.classValue;
            ROOT.rho[0] /= X.size();

            if(parent != null)
                ROOT.rho[0] = parent.ROOT.rho[0];
        }
        ROOT.splitNode(X, V, this);

//        split_q.add(ROOT);
//        while (!split_q.isEmpty()){
//            Node n = split_q.remove();
//            n.splitNode(X, V, this);
//        }
    }

    public void splitTree() {
        double err = ErrorOfTree(V);

        ArrayList<Instance> X1 = new ArrayList<>();
        ArrayList<Instance> X2 = new ArrayList<>();
        ArrayList<Instance> X3 = new ArrayList<>();

        ArrayList<Instance> V1 = new ArrayList<>();
        ArrayList<Instance> V2 = new ArrayList<>();
        ArrayList<Instance> V3 = new ArrayList<>();

        ArrayList<Integer> shuffler = new ArrayList<>();
        for(int i = 0; i < X.size(); i++) shuffler.add(i);
        Collections.shuffle(shuffler);

        for (int j = 0; j < shuffler.size(); j++) {
            Instance i = X.get(shuffler.get(j));
            if(ROOT.rho.length == 1) {
                double t = eval(i);
                if (t < SDTMain.leftBound)
                    X1.add(i);
                else if (t < SDTMain.rightBound)
                    X2.add(i);
                else
                    X3.add(i);
            }else{
                double[] softmax = (ROOT.sigmoid_F_rho(i));
                if (Util.max_value(softmax) < SDTMain.leftBound) {
                    X1.add(i);
                } else if (Util.max_value(softmax) > SDTMain.leftBound && Util.max_value(softmax) < SDTMain.rightBound) {
                    X2.add(i);
                } else if (Util.max_value(softmax) > SDTMain.rightBound) {
                    X3.add(i);
                }
            }
        }
        //System.out.println(X1.size() + " " + X2.size() + " " + X3.size() + " " + (X1.size() + X2.size() + X3.size()) + " " + X.size());

        shuffler = new ArrayList<>();
        for(int i = 0; i < V.size(); i++) shuffler.add(i);
        Collections.shuffle(shuffler);

        for (int j = 0; j < shuffler.size(); j++) {
            Instance i = V.get(shuffler.get(j));
            if(ROOT.rho.length == 1) {
                double t = eval(i);
                if (t < SDTMain.leftBound)
                    V1.add(i);
                else if (t < SDTMain.rightBound)
                    V2.add(i);
                else
                    V3.add(i);
            }else {
                double[] softmax = ROOT.sigmoid_F_rho(i);
                if (Util.max_value(softmax) < SDTMain.leftBound) {
                    V1.add(i);
                } else if (Util.max_value(softmax) > SDTMain.leftBound && Util.max_value(softmax) < SDTMain.rightBound) {
                    V2.add(i);
                } else if (Util.max_value(softmax) > SDTMain.rightBound) {
                    V3.add(i);
                }
            }
        }
        isLeaf = false;
        leftSDT = new SDT(X1, V1, T,true, LEARNING_RATE, EPOCH, MAX_STEP);
        leftSDT.parent = this;
        double newErrLeft = ErrorOfTree(V);

        SDT tempLeft = leftSDT;
        leftSDT = null;

        middleSDT = new SDT(X2, V2, T,true, LEARNING_RATE, EPOCH, MAX_STEP);
        middleSDT.parent = this;
        double newErrMiddle = ErrorOfTree(V);

        SDT tempMiddle = middleSDT;
        middleSDT = null;

        rightSDT = new SDT(X3, V3, T,true, LEARNING_RATE, EPOCH, MAX_STEP);
        rightSDT.parent = this;
        double newErrRight = ErrorOfTree(V);

        SDT tempRight = rightSDT;
        rightSDT = null;


        double newErr = ErrorOfTree(V);

//        System.out.println(err + " " + newErrLeft + " " + newErrMiddle + " " + newErrRight);

        isLeaf = true;

        if (err - newErrLeft > SDTMain.splitRate) {
            leftSDT = tempLeft;
            leftSDT.splitTree();
            isLeaf = false;
        }

        if (err - newErrMiddle > SDTMain.splitRate) {
            middleSDT = tempMiddle;
            middleSDT.splitTree();
            isLeaf = false;
        }

        if (err - newErrRight > SDTMain.splitRate) {
            rightSDT = tempRight;
            rightSDT.splitTree();
            isLeaf = false;
        }

    }

    public String getErrors() {
        DecimalFormat format = new DecimalFormat("#.###");
        if (isClassify)
            return "Training: " + format.format(1 - ErrorOfTree(X)) + "\tValidation: " + format.format(1 - ErrorOfTree(V)) + "\tTest: " + format.format(1 - ErrorOfTree(T));
        else
            return "Training: " +format.format( ErrorOfTree(X)) + "\tValidation: " +format.format( ErrorOfTree(V) )+ "\tTest: " + format.format(ErrorOfTree(T));
    }


    public double eval(Instance i) {
        if (isClassify) {
            if(ROOT.rho.length == 1) {
                double s = sigmoid(ROOT.F(i));
                if (s < SDTMain.leftBound && leftSDT != null) {
                    return leftSDT.eval(i);
                } else if (s > SDTMain.leftBound && s < SDTMain.rightBound && middleSDT != null) {
                    return middleSDT.eval(i);
                } else if (s > SDTMain.rightBound && rightSDT != null) {
                    return rightSDT.eval(i);
                } else
                    return s;
            }else{
                double[] softmax = (ROOT.sigmoid_F_rho(i));
                if (Util.max_value(softmax) < SDTMain.leftBound && leftSDT != null) {
                    return leftSDT.eval(i);
                } else if (Util.max_value(softmax) > SDTMain.leftBound && Util.max_value(softmax) < SDTMain.rightBound && middleSDT != null) {
                    return middleSDT.eval(i);
                } else if (Util.max_value(softmax) > SDTMain.rightBound && rightSDT != null) {
                    return rightSDT.eval(i);
                } else
                    return Util.argMax(softmax);
            }
        } else
            return ROOT.F(i);
    }


    public double ErrorOfTree(ArrayList<Instance> V) {
        double error = 0;
        for (Instance instance : V) {
            if (isClassify) {
                if(ROOT.rho.length == 1) {
                    double r = instance.classValue;
                    double y = eval(instance);
                    if (y > 0.5) {
                        if (r != 1)
                            error++;
                    } else if (r != 0)
                        error++;
                }else{
                    double r = instance.classValue;
                    double y = eval(instance);
                    if(y != r)
                        error++;
                }
            } else {
                double r = instance.classValue;
                double y = eval(instance);
                error += (r - y) * (r - y);
            }
        }
        return error / V.size();

    }

    public String toString() {
        return ROOT.toString(1);
    }

    private void readFile(ArrayList<Instance> I, String filename) throws IOException {
        String line;

        InputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);

        line = br.readLine();

        br.close();
        String[] s;
        String splitter;
        if(!line.contains(","))
            splitter = "\\s+";
        else
            splitter = ",";
        s = line.split(splitter);

        ATTRIBUTE_COUNT = s.length - 1;
//        System.out.println(ATTRIBUTE_COUNT + line);
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            s = line.split(splitter);

            double[] attributes = new double[ATTRIBUTE_COUNT];
            for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
                attributes[i] = Double.parseDouble(s[i]);
                if(SDTMain.isMnist)
                    attributes[i] /= 255;
            }
            String className = s[ATTRIBUTE_COUNT];

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
}