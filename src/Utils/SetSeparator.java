package Utils;

import BuddingTree.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.PrimitiveIterator;
import java.util.Scanner;


public class SetSeparator {
    static final String DIRECTORY_NAME = "C:\\Users\\Fatih\\Desktop\\multi-class";
    static final String[] SETS = new String[]{"balance-scale.data", "cmc.data", "dermatology.data", "ecoli.data", "glass.data", "optdigits.data", "page-blocks.data", "pendigits.data", "yeast.data"};

    static final double TRAIN = 3.0 / 5;
    static final double VALIDATION = 1.0 / 5;
    static final double TEST = 1.0 / 5;

    public static void main(String[] args) throws IOException {
        for (String set_name : SETS)
            read(set_name);
    }

    private static void read(final String set_name) throws IOException {
        ArrayList<Instance> instances = new ArrayList<>();
        int ATTRIBUTE_COUNT = 0;

        String line;


        InputStream fis = new FileInputStream(DIRECTORY_NAME + File.separator + set_name);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);

        line = br.readLine();

        br.close();
        String[] s = line.split(" ");

        ATTRIBUTE_COUNT = s.length - 1;
        Scanner scanner = new Scanner(new File(DIRECTORY_NAME + File.separator + set_name));
        while (scanner.hasNext()) {
            double[] attributes = new double[ATTRIBUTE_COUNT];
            for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
                attributes[i] = scanner.nextDouble();

            }
            instances.add(new Instance(scanner.nextInt(), attributes));
        }

        separate(instances, set_name);
    }

    public static void separate(ArrayList<Instance> instances, final String set_name) throws IOException {
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < instances.size(); i++) indices.add(i);
        Collections.shuffle(indices);

        int index = 0;
        ArrayList<Instance> tmp = new ArrayList<>();
        for (; index < instances.size() * TRAIN; index++) {
            tmp.add(instances.get(indices.get(index)));
        }
        write(tmp, set_name + "-train.txt");

        tmp = new ArrayList<>();
        for (; index < instances.size() * (TRAIN + VALIDATION); index++) {
            tmp.add(instances.get(indices.get(index)));
        }
        write(tmp, set_name + "-validation.txt");

        tmp = new ArrayList<>();
        for (; index < instances.size() * (TRAIN + VALIDATION + TEST); index++) {
            tmp.add(instances.get(indices.get(index)));
        }
        write(tmp, set_name + "-test.txt");
    }

    private static void write(ArrayList<Instance> instances, final String filename) throws IOException {
        File file = new File(DIRECTORY_NAME + File.separator + filename);
        BufferedWriter output = new BufferedWriter(new FileWriter(file));
        final int attribute_size = instances.get(0).attributes.length;
        for (Instance instance : instances) {
            for (int j = 0; j < attribute_size; j++) {
                output.write(instance.attributes[j] + " ");
            }
            output.write(instance.classNumber + "\n");
        }
        output.flush();
        output.close();
    }
}
