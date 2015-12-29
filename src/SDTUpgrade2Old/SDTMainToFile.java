package SDTUpgrade2Old;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;


public class SDTMainToFile {
//    public static double LEARNING_RATE = 10;
//    public static int MAX_STEP = 10;
//    public static int EPOCH = 25;
//
//    public static void main(String[] args) throws IOException {
//        Locale.setDefault(Locale.US);
//        final String[] CLASSIFY = new String[]{"breast", "spambase", "twonorm", "ringnorm", "german", "magic", "pima", "polyadenylation", "satellite47", "musk2"};
//        final String[] REGRESS = new String[]{"abalone", "boston", "add10", "comp", "california", "concrete", "puma8fh", "puma8nh", "puma8fm", "puma8nm"};
//
//        Runnable class_runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    File regress_out = new File("classify_out.txt");
//                    BufferedWriter regressWriter = new BufferedWriter(new FileWriter(regress_out, true));
//                    for (String s : CLASSIFY) {
//                        regressWriter.write("CLASSIFY " + s + "\n");
//                        regressWriter.flush();
//                        for (int i = 1; i <= 5; i++) {
//                            for (int j = 1; j <= 2; j++) {
//                                SDT sdt = new SDT("data_sdt\\" + s + "\\" + s + "-train-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-validation-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-test.txt", false, LEARNING_RATE, EPOCH, MAX_STEP);
//                                sdt.learnTree();
//                                regressWriter.write("Size: " + sdt.size() + "\t" + sdt.getErrors() + "\n");
//                                regressWriter.flush();
//                            }
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        Runnable regress_runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    File regress_out = new File("regress_out.txt");
//                    BufferedWriter regressWriter = new BufferedWriter(new FileWriter(regress_out, true));
//                    for (String s : REGRESS) {
//                        regressWriter.write("REGRESS " + s + "\n");
//                        regressWriter.flush();
//                        for (int i = 1; i <= 5; i++) {
//                            for (int j = 1; j <= 2; j++) {
//                                SDT sdt = new SDT("data_sdt\\" + s + "\\" + s + "-train-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-validation-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-test.txt", false, LEARNING_RATE, EPOCH, MAX_STEP);
//                                sdt.learnTree();
//                                regressWriter.write("Size: " + sdt.size() + "\t" + sdt.getErrors() + "\n");
//                                regressWriter.flush();
//                            }
//                        }
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        new Thread(class_runnable).start();
//        new Thread(regress_runnable).start();
//    }
}
