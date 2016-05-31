package BuddingTreeMultiClass.readers;


import java.io.*;
import java.util.*;

public class MSDReader {
    static {
        Locale.setDefault(Locale.US);
    }

    static Random random = new Random(45645);
    static double training_ratio = 0.6;
    static HashMap<String, MSDInstance> msdInstances_genre = new HashMap<>();
    static HashMap<String, MSDInstance> msdInstances_lyrics = new HashMap<>();
    static ArrayList<MSDInstance> merged = new ArrayList<>();
    static ArrayList<String> genres = new ArrayList<>();
    static double[] learning_rate_modifier;

    public static void readGenre() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("msd_genre_dataset.txt"));


        boolean isReached = false;
        while (!isReached) {
            isReached = scanner.nextLine().charAt(0) == '%';
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] splits = line.split(",");
            int i = genres.indexOf(splits[0]);
            if (i == -1) {
                genres.add(splits[0]);
                i = genres.size() - 1;
            }

            MSDInstance msdInstance = new MSDInstance();
            msdInstance.class_value = i;
            msdInstance.id = splits[1];
            msdInstance.x_sound = new double[splits.length - 4];

            for (int k = 0; k < msdInstance.x_sound.length; k++) {
                msdInstance.x_sound[k] = Double.valueOf(splits[k + 4]);
            }
            msdInstances_genre.put(msdInstance.id, msdInstance);
        }

    }

    public static void readLyrics(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));

        boolean isReached = false;
        while (!isReached) {
            isReached = scanner.nextLine().charAt(0) == '%';
        }

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] splits = line.split(",");
            MSDInstance instance = new MSDInstance();
            instance.id = splits[0];
            instance.x_lyrics = new double[5000];
            Arrays.fill(instance.x_lyrics, 0);
            for (int i = 2; i < splits.length; i++) {
                int index = splits[i].indexOf(':');
                int a = Integer.valueOf(splits[i].substring(0, index));
                int b = Integer.valueOf(splits[i].substring(index + 1));
                instance.x_lyrics[a - 1] = b;
            }
            msdInstances_lyrics.put(instance.id, instance);
        }

    }


    public static void merge() {
        for (MSDInstance msdInstance : msdInstances_genre.values()) {
            MSDInstance lyric = msdInstances_lyrics.get(msdInstance.id);
            if (lyric != null) {
                msdInstance.x_lyrics = lyric.x_lyrics;
                merged.add(msdInstance);
            }
        }
    }


    public static void main(String[] args) throws IOException {
        readGenre();
        readLyrics("mxm_dataset_train.txt");
        readLyrics("mxm_dataset_test.txt");
        merge();

        BufferedWriter writer = new BufferedWriter(new FileWriter("msd.txt"));
        writer.write(genres.size() + " " + merged.get(0).x_sound.length + " " + merged.get(0).x_lyrics.length + "\n");

        for (MSDInstance msdInstance : merged) {
            writer.write(msdInstance.class_value + " ");
            for (int i = 0; i < msdInstance.x_sound.length; i++) {
                writer.write(msdInstance.x_sound[i] + " ");
            }
            for (int i = 0; i < msdInstance.x_lyrics.length; i++) {
                writer.write(msdInstance.x_lyrics[i] + " ");
            }
            writer.write("\n");
        }

        writer.close();

    }


    public static DataSet getSoundOnly() throws FileNotFoundException {
        ArrayList<Instance> instances = new ArrayList<>();
        Scanner scanner = new Scanner(new File("msd.txt"));
        int class_count = scanner.nextInt();
        int sound_count = scanner.nextInt();
        int lyric_count = scanner.nextInt();

        while (scanner.hasNext()) {
            Instance instance = new Instance();
            instance.r = new int[class_count];
            Arrays.fill(instance.r, 0);
            instance.r[scanner.nextInt()] = 1;
            instance.x = new double[sound_count];
            for (int i = 0; i < sound_count; i++) {
                instance.x[i] = scanner.nextDouble();
            }
            for (int i = 0; i < lyric_count; i++) {
                scanner.nextDouble();
            }
            instances.add(instance);
            System.out.printf("\r%.2f", (instances.size() * 1.0) / 17495);

        }

        normalize(instances);

        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();

        for (Instance instance : instances) {
            if (random.nextDouble() < training_ratio)
                tra.add(instance);
            else
                val.add(instance);
        }


        return new DataSet("MSD Sound only", tra, val, DataSet.TYPE.MULTI_CLASS_CLASSIFICATION, learning_rate_modifier);
    }


    public static DataSet getLyricsOnly() throws FileNotFoundException {
        ArrayList<Instance> instances = new ArrayList<>();
        Scanner scanner = new Scanner(new File("msd.txt"));
        int class_count = scanner.nextInt();
        int sound_count = scanner.nextInt();
        int lyric_count = scanner.nextInt();

        while (scanner.hasNext()) {
            Instance instance = new Instance();
            instance.r = new int[class_count];
            Arrays.fill(instance.r, 0);
            instance.r[scanner.nextInt()] = 1;
            instance.x = new double[lyric_count];
            for (int i = 0; i < sound_count; i++) {
                scanner.nextDouble();
            }
            for (int i = 0; i < lyric_count; i++) {
                instance.x[i] = scanner.nextDouble();
            }
            instances.add(instance);
            System.out.printf("\r%.2f", (instances.size() * 1.0) / 17495);
        }

        normalize(instances);

        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();

        for (Instance instance : instances) {
            if (random.nextDouble() < training_ratio)
                tra.add(instance);
            else
                val.add(instance);
        }

        return new DataSet("MSD Lyrics only", tra, val, DataSet.TYPE.MULTI_CLASS_CLASSIFICATION, learning_rate_modifier);
    }

    public static DataSet getBoth() throws FileNotFoundException {
        ArrayList<Instance> instances = new ArrayList<>();
        Scanner scanner = new Scanner(new File("msd.txt"));
        int class_count = scanner.nextInt();
        int sound_count = scanner.nextInt();
        int lyric_count = scanner.nextInt();

        while (scanner.hasNext()) {
            Instance instance = new Instance();
            instance.r = new int[class_count];
            Arrays.fill(instance.r, 0);
            instance.r[scanner.nextInt()] = 1;
            instance.x = new double[sound_count + lyric_count];
            for (int i = 0; i < sound_count + lyric_count; i++) {
                instance.x[i] = scanner.nextDouble();
            }

            instances.add(instance);
            System.out.printf("\r%.2f", (instances.size() * 1.0) / 17495);
        }

        normalize(instances);

        ArrayList<Instance> tra = new ArrayList<>();
        ArrayList<Instance> val = new ArrayList<>();

        for (Instance instance : instances) {
            if (random.nextDouble() < training_ratio)
                tra.add(instance);
            else
                val.add(instance);
        }

        return new DataSet("MSD Both", tra, val, DataSet.TYPE.MULTI_CLASS_CLASSIFICATION, learning_rate_modifier);
    }

    private static void normalize(ArrayList<Instance> instances) {
        learning_rate_modifier = new double[instances.get(0).x.length];
        for (int i = 0; i < instances.get(0).x.length; i++) {
            double n = 0;
            double mean = 0;

            for (Instance instance : instances)
                mean += instance.x[i];

            mean /= instances.size();

            double stdev = 0;
            for (Instance instance : instances) {
                stdev += (instance.x[i] - mean) * (instance.x[i] - mean);
            }

            stdev = stdev / (instances.size() - 1);
            stdev = Math.sqrt(stdev);

            for (Instance instance : instances) {
                instance.x[i] -= mean;
                if (stdev != 0)
                    instance.x[i] /= stdev;

                n += instance.x[i];
            }

            learning_rate_modifier[i] = 1;
        }
    }

    static class MSDInstance {
        int class_value;
        double[] x_sound;
        double[] x_lyrics;
        String id;
    }
}
