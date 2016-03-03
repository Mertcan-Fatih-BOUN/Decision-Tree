package BuddingTreeMultiClass;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class SetReader {
    static {
        Locale.setDefault(Locale.US);
    }

    private static ArrayList<double[]> edgehistogram;
    private static ArrayList<double[]> homogeneoustexture;
    private static ArrayList<double[]> tags;
    private static ArrayList<int[]> annotations;

    private static ArrayList<double[]> readEdgehistogram() throws FileNotFoundException {
        if (edgehistogram != null)
            return edgehistogram;

        edgehistogram = new ArrayList<>();
        for (int i = 0; i < 25000; i++) {
            double[] values = new double[150];
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(
                    "mirflickr" + File.separator +
                            "features edgehistogram30k" + File.separator +
                            i / 10000 + File.separator +
                            i + ".txt")));

            for (int j = 0; j < values.length; j++) {
                values[j] = scanner.nextDouble();
            }
            edgehistogram.add(values);
        }
        return edgehistogram;
    }

    private static ArrayList<double[]> readhomogeneoustexture() throws FileNotFoundException {
        if (homogeneoustexture != null)
            return homogeneoustexture;

        homogeneoustexture = new ArrayList<>();
        for (int i = 0; i < 25000; i++) {
            double[] values = new double[43];
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(
                    "mirflickr" + File.separator +
                            "features homogeneoustexture30k" + File.separator +
                            i / 10000 + File.separator +
                            i + ".txt")));

            for (int j = 0; j < values.length; j++) {
                values[j] = scanner.nextDouble();
            }
            homogeneoustexture.add(values);
        }
        return homogeneoustexture;
    }

    private static ArrayList<double[]> readTags() throws FileNotFoundException {
        if (tags != null)
            return tags;
        tags = new ArrayList<>();
        ArrayList<ArrayList<String>> tag_strings = new ArrayList<>();
        HashMap<String, Integer> tag_count = new HashMap<>();

        for (int i = 1; i <= 25000; i++) {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(
                    "mirflickr" + File.separator +
                            "tags" + File.separator +
                            "tags" +
                            i + ".txt")));
            ArrayList<String> t = new ArrayList<>();
            while (scanner.hasNext()) {
                String s = scanner.next();
                t.add(s);
                if (tag_count.get(s) == null)
                    tag_count.put(s, 1);
                else
                    tag_count.put(s, tag_count.get(s) + 1);
            }
            tag_strings.add(t);
        }
        ArrayList<String> valid_tags = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : tag_count.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            if (value >= 50) {
                valid_tags.add(key);
            }
        }

        for (ArrayList<String> tag_string : tag_strings) {
            double[] values = new double[valid_tags.size()];
            Arrays.fill(values, 0);
            for (String s : tag_string) {
                int index = valid_tags.indexOf(s);
                if (index != -1)
                    values[index] = 1;
            }
            tags.add(values);
        }
        return tags;
    }

    private static ArrayList<int[]> readAnnotations() throws FileNotFoundException {
        if (annotations != null)
            return annotations;

        final String[] POTENTIAL_LABELS = new String[]{"sky", "clouds", "water", "sea", "river", "lake", "people", "portrait",
                "male", "female", "baby", "night", "plant_life", "tree", "flower", "animals",
                "dog", "bird", "structures", "sunset", "indoor", "transport", "car"};

        annotations = new ArrayList<>();
        for (int i = 0; i < 25000; i++) {
            int[] values = new int[POTENTIAL_LABELS.length];
            Arrays.fill(values, 0);
            annotations.add(values);
        }

        for (int i = 0; i < POTENTIAL_LABELS.length; i++) {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(
                    "mirflickr" + File.separator + "mirflickr25k_annotations_v080" + File.separator +
                            POTENTIAL_LABELS[i] + ".txt"
            )));

            while (scanner.hasNextInt()) {
                annotations.get(scanner.nextInt() - 1)[i] = 1;
            }
        }
        return annotations;
    }

    public static ArrayList<Instance>[] getDataset(boolean includeEdgeHistogram, boolean includeHomogeneousTexture, boolean includeTags) throws FileNotFoundException {
        ArrayList<Instance>[] ret = new ArrayList[2];
        ret[0] = new ArrayList<Instance>();
        ret[1] = new ArrayList<Instance>();

        if (!includeEdgeHistogram && !includeHomogeneousTexture && !includeTags)
            return null;

        if (includeEdgeHistogram)
            readEdgehistogram();
        if (includeHomogeneousTexture)
            readhomogeneoustexture();
        if (includeTags)
            readTags();

        readAnnotations();

        for (int i = 0; i < annotations.size(); i++) {
            double[] x = new double[0];
            if (includeEdgeHistogram)
                x = concat(x, edgehistogram.get(i));
            if (includeHomogeneousTexture)
                x = concat(x, homogeneoustexture.get(i));
            if (includeTags)
                x = concat(x, tags.get(i));

            Instance instance = new Instance();
            instance.x = x;
            instance.r = annotations.get(i);

            if (i % 5 < 3) {
                ret[0].add(instance);
            } else
                ret[1].add(instance);
        }

        return ret;
    }

    private static double[] concat(double[] a, double[] b) {
        if (b.length == 0)
            return a;
        if (a.length == 0)
            return b;

        int aLen = a.length;
        int bLen = b.length;
        double[] c = new double[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
