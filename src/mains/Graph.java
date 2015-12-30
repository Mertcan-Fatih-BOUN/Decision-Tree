package mains;


import BuddingTree.Instance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Graph {
    ArrayList<Instance> instances = new ArrayList<>();
    Evaluable evaluable;
    String tag;

    public Graph(String tag, Evaluable evaluable, int res) throws IOException {
        this.evaluable = evaluable;
        if (evaluable.getAttributeCount() != 2)
            return;


        double min_x1 = 10000;
        double max_x1 = -10000;
        double min_x2 = 10000;
        double max_x2 = -10000;
        for (Instance instance : evaluable.getInstances()) {
            if (instance.attributes[0] < min_x1)
                min_x1 = instance.attributes[0];
            if (instance.attributes[0] > max_x1)
                max_x1 = instance.attributes[0];

            if (instance.attributes[1] < min_x2)
                min_x2 = instance.attributes[1];
            if (instance.attributes[1] > max_x2)
                max_x2 = instance.attributes[1];
        }


        this.tag = tag;
        double res_x1 = (max_x1 - min_x1) / res;
        double res_x2 = (max_x2 - min_x2) / res;

        for (int i = 0; i < res; i++) {
            for (int j = 0; j < res; j++) {
                double[] attributes = new double[2];
                attributes[0] = min_x1 + i * res_x1;
                attributes[1] = min_x2 + j * res_x2;
                instances.add(new Instance(0, attributes));
            }
        }


        deleteDirectory(new File("log" + File.separator + tag));

        File file = new File("log" + File.separator + tag + File.separator + "header.txt");
        file.getParentFile().mkdirs();
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
        writer.write("min_x1 max_x1 min_x2 max_x2 res class_count\n");
        writer.write(min_x1 + " " + max_x1 + " " + min_x2 + " " + max_x2 + " " + res + " " + evaluable.getClassCount() + "\n");
        writer.flush();
        writer.close();

        File file2 = new File("log" + File.separator + tag + File.separator + "grid-input.txt");
        BufferedWriter writer2 = new BufferedWriter(new FileWriter(file2, false));
        writer2.write("x1 x2\n");
        for (int j = 0; j < res; j++) {
            writer2.write((min_x1 + j * res_x1) + " " + (min_x2 + j * res_x2) + "\n");
        }
        writer2.flush();
        writer2.close();

        File file3 = new File("log" + File.separator + tag + File.separator + "input.txt");
        BufferedWriter writer3 = new BufferedWriter(new FileWriter(file3, false));
        writer3.write("x1 x2 y\n");
        for (Instance instance : evaluable.getInstances())
            writer3.write(instance.attributes[0] + " " + instance.attributes[1] + " " + instance.classValue + "\n");
        writer3.flush();
        writer3.close();

    }

    public void addEpoch(int epoch) {
        if (evaluable.getAttributeCount() != 2)
            return;
        try {
            File file = new File("log" + File.separator + tag + File.separator + "epoch-" + epoch + ".txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write("y\n");
            for (Instance instance : instances) {
                int d = (int) evaluable.predicted_class(instance);
                writer.write(d + "\n");
            }

            writer.flush();

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        return (directory.delete());
    }
}
