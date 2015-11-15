package Utils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by mertcan on 15.11.2015.
 */
public class Util {
    public static int ATTRIBUTE_COUNT;
    public static ArrayList<String> CLASS_NAMES = new ArrayList<>();
    public static int CLASS_COUNT;

    public static void readFile(ArrayList<Instance> I, String filename) throws IOException {
        String line;

        InputStream fis = new FileInputStream(filename);
        InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);

        line = br.readLine();

        br.close();
        String[] s;
        String splitter;
        if(!line.contains(","))
            splitter = " ";
        else
            splitter = ",";
        s = line.split(splitter);

        ATTRIBUTE_COUNT = s.length - 1;
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            s = line.split(splitter);

            double[] attributes = new double[ATTRIBUTE_COUNT];
            for (int i = 0; i < ATTRIBUTE_COUNT; i++) {
                attributes[i] = Double.parseDouble(s[i]);
            }
            String className = s[ATTRIBUTE_COUNT];

            int classNumber;
            if (CLASS_NAMES.contains(className)) {
                classNumber = CLASS_NAMES.indexOf(className);
            } else {
                CLASS_NAMES.add(className);
                classNumber = CLASS_NAMES.indexOf(className);
            }
            I.add(new Instance(className, classNumber, attributes));
        }

        CLASS_COUNT = CLASS_NAMES.size();
    }

    public static double rand(double s, double e) {
        if (e < s) {
            double t = e;
            e = s;
            s = t;
        }

        return (e - s) * Math.random() + s;
    }

    public static double sigmoid(double x) {
        return 1.0 / (1 + Math.exp(-x));
    }

    public static double dotProduct(double[] x, double[] y) {
        double result = 0;
        for (int i = 0; i < x.length; i++)
            result += x[i] * y[i];
        return result;
    }
}
