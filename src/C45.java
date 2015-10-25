import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class C45 {
    static Random r = new Random();

    public static int CLASS_COUNT = 3;
    public static int ATTRIBUTE_COUNT = 4;

    public static String[] CLASS_NAMES = {"Iris-virginica", "Iris-versicolor", "Iris-setosa"};

    public static ArrayList<Instance> instances = new ArrayList<>();


    public static void main(String[] args) {
        try {
            readDataSet("iris.data.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        double LEARNING_RATE = 0.01;
        int HIDDEN_LAYER = 10;
        double[][] WEIGHT1 = new double[ATTRIBUTE_COUNT][HIDDEN_LAYER];
        double[][] WEIGHT2 = new double[HIDDEN_LAYER][CLASS_COUNT];
        double[] HIDDEN = new double[HIDDEN_LAYER];
        double[] HIDDEN_ERROR = new double[HIDDEN_LAYER];
        double[] OUT = new double[CLASS_COUNT];
        double[] OUT_ERROR = new double[CLASS_COUNT];

        for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
            for (int j = 0; j < HIDDEN_LAYER; j++) {
                WEIGHT1[i][j] = (0.001 - 0.0001) * r.nextDouble() + 0.0001;
            }
        }
        for (int i = 0; i < HIDDEN_LAYER; i++) {
            for (int j = 0; j < CLASS_COUNT; j++) {
                WEIGHT2[i][j] = (0.001 - 0.0001) * r.nextDouble() + 0.0001;
            }
        }

        double max_change = 10;
       for(int z =0; z < 30;z++)
       {
            //System.out.println(max_change);
            max_change = 0;

            for (Instance instance : instances) {
                for (int indexH = 0; indexH < HIDDEN_LAYER; indexH++) {
                    HIDDEN[indexH] = 0;
                    for (int indexA = 0; indexA < ATTRIBUTE_COUNT; indexA++) {
                        HIDDEN[indexH] += WEIGHT1[indexA][indexH] * instance.inputs[indexA];
                    }
                    HIDDEN[indexH] = 1.0 / (1 + (Math.exp(-HIDDEN[indexH])));
                }
                for (int indexO = 0; indexO < CLASS_COUNT; indexO++) {
                    double old_out = OUT[indexO];
                    OUT[indexO] = 0;
                    for (int indexH = 0; indexH < HIDDEN_LAYER; indexH++) {
                        OUT[indexO] += WEIGHT2[indexH][indexO] * HIDDEN[indexH];
                    }
                    OUT[indexO] =1.0 / (1 + (Math.exp(-OUT[indexO])));
                   // System.out.println(old_out - OUT[indexO]);
                    max_change = Math.max(max_change, Math.abs(old_out - OUT[indexO]));
                }

                for (int indexO = 0; indexO < CLASS_COUNT; indexO++) {
                    OUT_ERROR[indexO] = (instance.outputs[indexO] - OUT[indexO]) * (1 - OUT[indexO]) * OUT[indexO];
                }

                for (int indexH = 0; indexH < HIDDEN_LAYER; indexH++) {
                    for (int indexO = 0; indexO < CLASS_COUNT; indexO++) {
                        double change = LEARNING_RATE * OUT_ERROR[indexO] * HIDDEN[indexH];
                        WEIGHT2[indexH][indexO] = WEIGHT2[indexH][indexO] + change;
                    }
                }
                for (int indexH = 0; indexH < HIDDEN_LAYER; indexH++) {
                    double lastMultiplier = 0;
                    for (int indexO = 0; indexO < CLASS_COUNT; indexO++) {
                        lastMultiplier += OUT_ERROR[indexO] * WEIGHT2[indexH][indexO];
                    }
                    HIDDEN_ERROR[indexH] = HIDDEN[indexH] * (1 - HIDDEN[indexH]) * lastMultiplier;
                }

                for (int indexA = 0; indexA < ATTRIBUTE_COUNT; indexA++) {
                    for (int indexH = 0; indexH < HIDDEN_LAYER; indexH++) {
                        double change = LEARNING_RATE * HIDDEN_ERROR[indexH] * instance.inputs[indexA];
                        WEIGHT1[indexA][indexH] = WEIGHT1[indexA][indexH] + change;
                    }
                }
            }
        }


        ///TEST
        int passed = 0;
        int failed = 0;
        for (Instance instance : instances) {
            long[] result = new long[CLASS_COUNT];
            for (int indexH = 0; indexH < HIDDEN_LAYER; indexH++) {
                HIDDEN[indexH] = 0;
                for (int indexA = 0; indexA < ATTRIBUTE_COUNT; indexA++) {
                    HIDDEN[indexH] += WEIGHT1[indexA][indexH] * instance.inputs[indexA];
                }
                HIDDEN[indexH] = 1.0 / (1 + (Math.exp(-HIDDEN[indexH])));
            }
            for (int indexO = 0; indexO < CLASS_COUNT; indexO++) {
                OUT[indexO] = 0;
                for (int indexH = 0; indexH < HIDDEN_LAYER; indexH++) {
                    OUT[indexO] += WEIGHT2[indexH][indexO] * HIDDEN[indexH];
                }
                OUT[indexO] = 1.0 / (1 + (Math.exp(-OUT[indexO])));

                result[indexO] = Math.round(OUT[indexO]);
            }

            boolean isPass = true;
            for (int i = 0; i < CLASS_COUNT; i++) {
                System.out.println();
                System.out.println(instance.outputs[0] + " " + instance.outputs[1] + " " + instance.outputs[2]);
                System.out.println(result[0] + " " + result[1] + " " + result[2]);
                if (instance.outputs[i] != result[i]) {
                    isPass = false;
                    break;
                }
            }
            if (isPass)
                passed++;
            else
                failed++;


        }

        System.out.println("Pass " + passed + " Failed " + failed);

    }


    private static void readDataSet(String s) throws IOException {
        FileInputStream fstream = new FileInputStream(s);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        while ((strLine = br.readLine()) != null) {
            String[] parts = strLine.split(",");
            Instance instance = new Instance(parts[parts.length - 1]);
            for (int i = 0; i < parts.length - 1; i++) {
                instance.inputs[i] = Double.parseDouble(parts[i]);
            }
            instances.add(instance);
        }

        br.close();


    }


    public static class Instance {
        public double inputs[] = new double[ATTRIBUTE_COUNT];
        public int[] outputs = new int[CLASS_COUNT];

        public Instance(String name) {
            Arrays.fill(outputs, 0);
            for (int i = 0; i < CLASS_NAMES.length; i++) {
                if (CLASS_NAMES[i].equals(name)) {
                    outputs[i] = 1;
                    break;
                }
            }
        }
    }
}
