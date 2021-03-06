package mains;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import BuddingTree.BT;
import SDT.SDT;

@SuppressWarnings({"unused", "Duplicates"})
public class TreeRunner {
    public static double LEARNING_RATE = 0.2;//2 0.001 is nice for msd
    public static int EPOCH = 40;
    public static int MAX_STEP = 5;
    public static boolean isMnist = false;

    static final String[] MULTICLASS = new String[]{"balance-scale.data", "cmc.data", "dermatology.data", "ecoli.data", "glass.data", "optdigits.data", "page-blocks.data", "pendigits.data", "yeast.data", "segment.data"};
    static final String[] CLASSIFY = new String[]{"breast", "spambase", "twonorm", "ringnorm", "german", "magic", "pima", "polyadenylation", "satellite47", "musk2"};
    static final String[] REGRESS = new String[]{"abalone", "boston", "add10", "comp", "california", "concrete", "puma8fh", "puma8nh", "puma8fm", "puma8nm"};

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

//SDT sdt = new SDT("mnist"+File.separator+"train.txt","mnist"+File.separator+"test.txt","mnist"+File.separator+"test.txt",true,2,10,5);
//        sdt.learnTree();

        //System.out.println("-----BT-----");
        //run_single_multi_class_set(BT.class, "balance-scale.data");
        //System.out.println("-----SDT-----");
        //run_single_multi_class_set(SDT.class, "balance-scale.data");

//        run_all_multi_classes(BT.class);
//        run_all_multi_classes(SDT.class);
        //   run_all_multi_classes(SDT.class);
//        run_single_multi_class_set(BT.class, "page-blocks.data");
//        run_single_multi_class_set(BT.class, "pendigits.data");
//        run_single_multi_class_set(BT.class, "yeast.data");
//        run_single_multi_class_set(BT.class, "segment.data");
//
//        isMnist = true;
//        run_classification_by_filename(BT.class,
//                "mnist" + File.separator + "train.txt",
//                "mnist" + File.separator + "test.txt",
//                "mnist" + File.separator + "test.txt");


//        run_single_multi_class_set(BT.class,"ecoli.data");
//        run_all_binary_classes(SDT.class);
//        run_all_regressions(BT.class);
//        run_single_binary_classification_set_fold(SDT.class, "breast", 1, 1);
//        run_all_binary_classes(BT.class);
//        run_single_binary_classification_set_fold(BT.class, "breast", 1, 1);
        //run_single_regression_set_fold("boston", 1, 1);

        //isMnist = true; // Make BT and SDT classes check this
//        run_classification_by_filename("data_sdt\\mnist\\mnist.txt");

        //run_classification_by_filename(BT.class, "iris.data.v2.txt");
//        run_classification_by_filename(SDT.class, "data_set_nonlinear_4.data.txt");
        //run_classification_by_filename(SDT.class, "iris.data.txt");

//        run_single_multi_class_set(BT.class,"million_song_rnd_sample_clsfirst");
//        run_single_regression_by_filename(BT.class, "data_multi" + File.separator + "million_song_rnd_sample_clsfirst-train.txt", "data_multi" + File.separator + "million_song_rnd_sample_clsfirst-test.txt", "data_multi" + File.separator + "million_song_rnd_sample_clsfirst-test.txt");
//        run_single_multi_class_set(BT.class,"flickr_r1_labels");
//        run_single_multi_class_set(BT.class,"complete_mirflickr");
//        run_single_multi_class_set(BT.class,"millionsong_yearpred_clsfirst");
//        run_single_regression_set(BT.class, "millionsong_yearpred_clsfirst");
//        run_single_regression_set(SDT.class, "millionsong_yearpred_clsfirst");
        run_single_multi_class_set(BT.class,"mnist");
//        run_single_multi_class_set(SDT.class,"mnist");

//        run_single_multi_class_set(BuddingTree2.BT.class,"get_flickr");
//        run_single_multi_class_set(BuddingTree2.BT.class,"complete_mirflickr_notags");
//        run_single_multi_class_set(BuddingTree.BT.class,"complete_mirflickr_tags");
    }


    private static void run_tree(final Class<?> cls, boolean isClassify, final String training, final String validation, final String test) throws IOException {
        if (cls == BT.class) {
            BT bt = new BT(
                    training,
                    validation,
                    test,
                    isClassify,
                    LEARNING_RATE,
                    EPOCH);
            bt.learnTree();
            System.out.println("Size: " + bt.size() + "\t" + bt.getErrors());
        } else if (cls == SDT.class) {
            SDT sdt = new SDT(
                    training,
                    validation,
                    test,
                    isClassify,
                    LEARNING_RATE,
                    EPOCH,
                    MAX_STEP
            );
            sdt.learnTree();
            System.out.println("Size: " + sdt.size() + "\t" + sdt.getErrors());
        }else if(cls == BuddingTree.BT.class){
            BuddingTree.BT bt = new BuddingTree.BT(
                    training,
                    validation,
                    test,
                    isClassify,
                    LEARNING_RATE,
                    EPOCH);
            bt.learnTree();
            System.out.println("Epoch " + (EPOCH - 1) + " Size: " + bt.size() + "\t" + bt.effSize() + " \t" + bt.getErrors());
        }
    }

    private static void run_single_multi_class_set(Class<?> cls, final String set_name) throws IOException {
        run_classification_by_filename(
                cls,
                "data_multi" + File.separator + set_name + "-train.txt",
                "data_multi" + File.separator + set_name + "-validation.txt",
                "data_multi" + File.separator + set_name + "-test.txt"
        );
    }

    private static void run_single_regression_set(Class<?> cls, final String set_name) throws IOException {
        run_single_regression_by_filename(
                cls,
                "data_multi" + File.separator + set_name + "-train.txt",
                "data_multi" + File.separator + set_name + "-validation.txt",
                "data_multi" + File.separator + set_name + "-test.txt"
        );
    }

    private static void run_classification_by_filename(Class<?> cls, final String training, final String validation, final String test) throws IOException {
        run_tree(cls, true, training, validation, test);
    }

    private static void run_classification_by_filename(Class<?> cls, final String filename) throws IOException {
        run_classification_by_filename(cls, filename, filename, filename);
    }

    private static void run_single_regression_by_filename(Class<?> cls, final String filename) throws IOException {
        run_single_regression_by_filename(cls, filename, filename, filename);
    }

    private static void run_single_regression_by_filename(Class<?> cls, final String training, final String validation, final String test) throws IOException {
        run_tree(cls, false, training, validation, test);
    }

    private static void run_single_binary_classification_set_fold(Class<?> cls, final String set_name, int fold1, int fold2) throws IOException {
        run_classification_by_filename(
                cls,
                "data_sdt" + File.separator + set_name + File.separator + set_name + "-train-" + fold1 + "-" + fold2 + ".txt",
                "data_sdt" + File.separator + set_name + File.separator + set_name + "-validation-" + fold1 + "-" + fold2 + ".txt",
                "data_sdt" + File.separator + set_name + File.separator + set_name + "-test.txt"
        );
    }

    private static void run_single_regression_set_fold(Class<?> cls, final String set_name, int fold1, int fold2) throws IOException {
        run_single_regression_by_filename(
                cls,
                "data_sdt" + File.separator + set_name + File.separator + set_name + "-train-" + fold1 + "-" + fold2 + ".txt",
                "data_sdt" + File.separator + set_name + File.separator + set_name + "-validation-" + fold1 + "-" + fold2 + ".txt",
                "data_sdt" + File.separator + set_name + File.separator + set_name + "-test.txt"
        );
    }

    private static void run_all_multi_classes(Class<?> cls) throws IOException {
        for (String s : MULTICLASS) {
            System.out.println("\n\nSet name: " + s);
            run_single_multi_class_set(cls, s);
        }
    }

    private static void run_all_binary_classes(Class<?> cls) throws IOException {
        for (String s : CLASSIFY) {
            System.out.println("\n\nSet name: " + s);
            for (int i = 1; i <= 5; i++) {
                for (int j = 1; j <= 2; j++) {
                    run_single_binary_classification_set_fold(cls, s, i, j);
                }
            }
        }
    }

    private static void run_all_regressions(Class<?> cls) throws IOException {
        for (String s : REGRESS) {
            System.out.println("\n\nSet name: " + s);

            for (int i = 1; i <= 5; i++) {
                for (int j = 1; j <= 2; j++) {
                    run_single_regression_set_fold(cls, s, i, j);
                }
            }
        }
    }
}
