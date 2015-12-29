package SDTUpgrade2Old;

import java.io.IOException;
import java.util.Locale;


public class SDTMain {
    public static double LEARNING_RATE = 10;
    public static int MAX_STEP = 10;
    public static int EPOCH = 25;
    public static boolean isClassify = true;

    public static double leftBound = 0.333;
    public static double rightBound = 0.666;

    public static double splitRate = 1e-4;

    public static SDT sdt;

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);
        String[] CLASSIFY = new String[]{"breast", "spambase", "twonorm", "ringnorm", "german", "magic", "pima", "polyadenylation", "satellite47", "musk2"};
        String[] REGRESS = new String[]{"abalone", "boston", "add10", "comp", "california", "concrete", "puma8fh", "puma8nh", "puma8fm", "puma8nm"};
        //  String[] CLASSIFY = new String[]{ "breast"};

//        SDT sdt = new SDT("data_sdt\\breast\\breast-train-1-1.txt", "data_sdt\\breast\\breast-validation-1-1.txt", "data_sdt\\breast\\breast-test.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
//        sdt.learnTree();
//        System.out.println(sdt.getErrors());
//        System.out.println(sdt.toString());

//        SDT sdt = new SDT("data_sdt\\boston\\boston-train-1-1.txt", "data_sdt\\boston\\boston-validation-1-1.txt", "data_sdt\\boston\\boston-test.txt", false, LEARNING_RATE, EPOCH, MAX_STEP);
//        sdt.learnTree();
//        System.out.println(sdt.getErrors());
//        System.out.println(sdt.toString());

        SDT sdt3 = new SDT("data_sdt\\optdigits\\train.txt", "data_sdt\\optdigits\\validation.txt", "data_sdt\\optdigits\\test.txt");

        sdt3.splitTree();
        System.out.println("Size: " + sdt3.size() + "\t" + sdt3.effSize() + "\t" + sdt3.getErrors());

//        double[][] results = new double[5][10];
//        for (String s : CLASSIFY) {
////            System.out.println("CLASS " + s);
//            for (int i = 1; i <= 5; i++) {
//                for (int j = 1; j <= 2; j++) {
//                    sdt = null;
//                    sdt = new SDT( "data_sdt\\"+ s+ "\\"+s + "-train-" + i + "-" + j + ".txt", "data_sdt\\"+ s+ "\\"+s  + "-validation-" + i + "-" + j + ".txt",  "data_sdt\\"+ s+ "\\"+s +  "-test.txt");
//                    sdt.splitTree();
////                    System.out.println("Eff Size: " + sdt.effSize() + "\t " + "Size: " + sdt.size() + "\t" + sdt.getErrors());
////                    System.out.println(sdt.toString());
//                    results[0][i * 2 - 3 + j] = sdt.size();
//                    results[1][i * 2 - 3 + j] = sdt.effSize();
//                    results[2][i * 2 - 3 + j] = 1 - sdt.ErrorOfTree(sdt.X);
//                    results[3][i * 2 - 3 + j] = 1 - sdt.ErrorOfTree(sdt.V);
//                    results[4][i * 2 - 3 + j] = 1 - sdt.ErrorOfTree(sdt.T);
//                }
//            }
//            double[] statistics = findStatistcis(results);
//            System.out.println(statistics[0] + "\t" + statistics[1] + "\t" + statistics[2] + "\t" + statistics[3] + "\t" + statistics[4]);
//        }


//
//        isClassify = false;
//        for (String s : REGRESS) {
//            System.out.println("REGRESS " + s);
//            for (int i = 1; i <= 5; i++) {
//                for (int j = 1; j <= 2; j++) {
//                    SDT sdt = new SDT("data_sdt\\" + s + "\\" + s + "-train-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-validation-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-test.txt");
//                    sdt.splitTree();
//                    System.out.println("Size: " + sdt.size() + "\t" + sdt.getErrors());
//                    //   System.out.println(sdt.toString());
//                }
//            }
//        }
    }

    private static double[] findStatistcis(double[][] results) {
        double[] resultGeneral = new double[5];
        for(int i = 0; i < 10; i++){
            resultGeneral[0] += results[0][i];
            resultGeneral[1] += results[1][i];
            resultGeneral[2] += results[2][i];
            resultGeneral[3] += results[3][i];
            resultGeneral[4] += results[3][i];
        }
        for(int i = 0; i < 5; i++){
            resultGeneral[i] /= 10;
        }
        return resultGeneral;
    }
}