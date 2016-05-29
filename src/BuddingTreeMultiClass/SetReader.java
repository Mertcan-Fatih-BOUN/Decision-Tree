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
    private static ArrayList<double[]> github_gist;

    public static int tag_size = 0;

    public static final String[] POTENTIAL_LABELS = new String[]{"bird", "baby", "animals", "car", "clouds", "dog", "female", "flower",
            "food", "indoor", "lake", "male", "night", "people", "plant_life", "portrait", "river", "sea",
            "sky", "structures", "sunset", "transport", "tree", "water", "bird_r1", "baby_r1", "car_r1", "clouds_r1", "dog_r1", "female_r1",
            "flower_r1", "male_r1", "night_r1", "people_r1", "portrait_r1", "river_r1", "sea_r1",
            "tree_r1"};

    public static int[] class_counts(ArrayList<Instance> X){
        int[] class_counts = new int[38];
        for(Instance i:X){
            for(int t = 0; t < i.r.length; t++){
                if(i.r[t] == 1)
                    class_counts[t]++;
            }
        }
        return class_counts;
    }

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
        normalize(edgehistogram);
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
        normalize(homogeneoustexture);
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

        tag_size = valid_tags.size();


        return tags;
    }

    private static ArrayList<int[]> readAnnotations() throws FileNotFoundException {
        if (annotations != null)
            return annotations;

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


    private static ArrayList<double[]> readGithub(String filename) throws FileNotFoundException {


        ArrayList<double[]> list = new ArrayList<>();

        Scanner scanner = new Scanner(new BufferedReader(new FileReader("github" + File.separator + filename)));

        int attribute_count = 0;
        String line = scanner.nextLine();
        while (!line.equals("@data")) {
            if (line.contains("@attribute"))
                attribute_count++;
            line = scanner.nextLine();
        }

        for (int i = 0; i < 25000; i++) {
            double[] x = new double[attribute_count];
            line = scanner.nextLine();
            String[] ln = line.split(", ");
            for (int j = 0; j < x.length; j++) {
                x[j] = Double.parseDouble(ln[j]);
            }
            list.add(x);
        }

        normalize(list);

        return list;
    }

    public static ArrayList<Instance>[] getGithubDatasetNoTag() throws FileNotFoundException {
        ArrayList<Instance>[] ret = new ArrayList[2];
        ret[0] = new ArrayList<Instance>();
        ret[1] = new ArrayList<Instance>();

        ArrayList<double[]> gist = readGithub("complete_mirflickr.txt");

        readAnnotations();

        for (int i = 0; i < annotations.size(); i++) {
            double[] x = new double[0];
            x = gist.get(i);

            Instance instance = new Instance();
            instance.x = x;
            instance.r = annotations.get(i);
            instance.mirflicker_id = i + 1;
            instance.id = i;

            if (i % 5 < 3) {
                ret[0].add(instance);
            } else
                ret[1].add(instance);
        }

        return ret;
    }

    public static ArrayList<Instance>[] getGithubDatasetNoTag_v2() throws FileNotFoundException {
        ArrayList<Instance>[] ret = new ArrayList[2];
        ret[0] = new ArrayList<Instance>();
        ret[1] = new ArrayList<Instance>();

        ArrayList<double[]> gist = readGithub("complete_mirflickr.txt");

        readAnnotations();

        for (int i = 0; i < annotations.size(); i++) {
            double[] x = new double[0];
            x = gist.get(i);

            Instance instance = new Instance();
            instance.x = x;
            instance.r = annotations.get(i);
            instance.mirflicker_id = i + 1;
            instance.id = i;

            int add = 0;
            for(int t = 0; t < instance.r.length; t++)
                add += instance.r[t];

            if (i % 5 < 3 && add > 0) {
                ret[0].add(instance);
            } else if(add > 0)
                ret[1].add(instance);
        }

        return ret;
    }


    public static ArrayList<Instance>[] getGithubDataset() throws FileNotFoundException {
        ArrayList<Instance>[] ret = new ArrayList[2];
        ret[0] = new ArrayList<Instance>();
        ret[1] = new ArrayList<Instance>();

        ArrayList<double[]> gist = readGithub("complete_mirflickr.txt");

        readAnnotations();
        readTags();

        for (int i = 0; i < annotations.size(); i++) {
            double[] x = new double[0];
            x = concat(tags.get(i), gist.get(i));

            Instance instance = new Instance();
            instance.x = x;
            instance.r = annotations.get(i);
            instance.mirflicker_id = i + 1;
            instance.id = i;

            if (i % 5 < 3) {
                ret[0].add(instance);
            } else
                ret[1].add(instance);
        }

        return ret;
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
            instance.id = 0;
            instance.mirflicker_id = i + 1;
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


    private static void normalize(ArrayList<double[]> values) {
        for(int i = 0; i < 200; i++)
            System.out.print(values.get(0)[i] + " ");
        System.out.println();
        for (int i = 0; i < values.get(0).length; i++) {
            double mean = 0;

            for (double[] value : values) {
                mean += value[i];
            }

//            for (int j = 0; j < values.size(); j++) {
//                if(j % 5 < 3)
//                    mean += values.get(j)[i];
//            }
            mean /= (values.size() * 5 / 5);

            double stdev = 0;
            for (double[] value : values) {
                stdev += (value[i] - mean) * (value[i] - mean);
            }
//            for (int j = 0; j < values.size(); j++) {
//                if(j % 5 < 3)
//                    stdev += (values.get(j)[i] - mean) * (values.get(j)[i] - mean);
//            }


//            System.out.print(i + " " + stdev + " ");

            stdev /= ((values.size() * 5 / 5) - 1);
            stdev = Math.sqrt(stdev);

//            System.out.println(stdev);

            for (double[] value : values) {
                value[i] -= mean;
                if (stdev != 0)
                    value[i] /= stdev;
//                value[i] *= Math.sqrt(values.size() - 1);
            }

        }
//        for(int i = 0; i < 200; i++)
//            System.out.print(values.get(0)[i] + " ");
//        System.out.println();
//        for(int i = 0; i < 200; i++)
//            System.out.print(values.get(0)[i] * Math.sqrt(values.size() - 1) + " ");
//        System.out.println();
    }

    public static void to0_1(ArrayList<double[]> values) {
        double[] mins = new double[values.get(0).length];
        double[] maxs = new double[values.get(0).length];
        Arrays.fill(mins, Integer.MAX_VALUE);
        Arrays.fill(maxs, Integer.MIN_VALUE);

        for (double[] value : values) {
            for(int i = 0; i < value.length; i++){
                if(value[i] < mins[i]){
                    mins[i] = value[i];
                }
                if(value[i] > maxs[i]){
                    maxs[i] = value[i];
                }
            }
        }

        double[] as = new double[values.get(0).length];
        double[] bs = new double[values.get(0).length];

        for(int i = 0; i < values.get(0).length; i++){
            as[i] = 1.0 / (maxs[i] - mins[i]);
            bs[i] = -mins[i] * as[i];
        }

        for (double[] value : values) {
            for(int i = 0; i < value.length; i++){
                value[i] = as[i] * value[i] + bs[i];
            }
        }

    }
}
