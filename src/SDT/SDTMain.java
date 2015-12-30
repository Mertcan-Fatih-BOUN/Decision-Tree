package SDT;

import java.io.IOException;
import java.util.Locale;


public class SDTMain {
    //0.35, 2, 10
    public static double LEARNING_RATE = 0.85;
    public static int MAX_STEP = 2;
    public static int EPOCH = 5;
    public static boolean isMnist = false;

    //PEN VE SEGMENT 0.35
    //Diğerleri 0.55
    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);
        String[] CLASSIFY_MULTI = new String[]{"pendigits", "segment","balance-scale", "cmc", "dermatology", "ecoli", "glass", "optdigits", "page-blocks", "pendigits", "segment", "yeast"};
        String[] CLASSIFY = new String[]{"breast", "spambase", "twonorm", "ringnorm", "german", "magic", "pima", "polyadenylation", "satellite47", "musk2"};
        String[] REGRESS = new String[]{"abalone", "boston", "add10", "comp", "california", "concrete", "puma8fh", "puma8nh", "puma8fm", "puma8nm"};
        System.out.println("Learning rate: " + LEARNING_RATE + " Epoch: " + EPOCH + " Max Step: " + MAX_STEP);
        //  String[] CLASSIFY = new String[]{ "breast"};

//        SDT sdt = new SDT("data_sdt\\breast\\breast-train-1-1.txt", "data_sdt\\breast\\breast-validation-1-1.txt", "data_sdt\\breast\\breast-test.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
//        sdt.learnTree();
//        System.out.println(sdt.getErrors());
//        System.out.println(sdt.toString());

//        SDT sdt = new SDT("data_sdt\\boston\\boston-train-1-1.txt", "data_sdt\\boston\\boston-validation-1-1.txt", "data_sdt\\boston\\boston-test.txt", false, LEARNING_RATE, EPOCH, MAX_STEP);
//        sdt.learnTree();
//        System.out.println(sdt.getErrors());
//        System.out.println(sdt.toString());
//

//        SDT sdt3 = new SDT( "iris.data.txt", "iris.data.txt", "iris.data.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
//        sdt4.learnTree();
//        System.out.println("Size: " + sdt4.size() + "\t" + sdt4.getErrors());

//        isMnist = true;
//       SDT sdt3 = new SDT("data_sdt\\mnist\\mnist_train.txt", "data_sdt\\mnist\\mnist_validation.txt", "data_sdt\\mnist\\mnist_test.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
////        SDT sdt3 = new SDT("data_sdt\\mnist\\mnist_ordered_03.txt", "data_sdt\\mnist\\mnist_ordered_03.txt", "data_sdt\\mnist\\mnist_ordered_03.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
////        SDT sdt3 = new SDT("data_sdt\\optdigits\\train.txt", "data_sdt\\optdigits\\validation.txt", "data_sdt\\optdigits\\test.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
//
//        sdt3.learnTree();
//        System.out.println("Size: " + sdt3.size() + "\t" + sdt3.getErrors());
//
//        SDT sdt2 = new SDT( "data_set_nonlinear_1.data.txt", "data_set_nonlinear_1.data.txt", "data_set_nonlinear_1.data.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
//        sdt2.learnTree();
//        while(sdt2.ErrorOfTree(sdt2.T) > 0.2){
//            sdt2 = new SDT( "data_set_nonlinear_1.data.txt", "data_set_nonlinear_1.data.txt", "data_set_nonlinear_1.data.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
//            sdt2.learnTree();
//        }
//        System.out.println("Size: " + sdt2.size() + "\t" + sdt2.getErrors());
        for (String s : CLASSIFY_MULTI) {
//            System.out.println("CLASS " + s);
//            for (int i = 1; i <= 5; i++) {
//                for (int j = 1; j <= 2; j++) {
                    SDT sdt = new SDT( "data_multi\\"+ s + ".data-train.txt",  "data_multi\\"+ s + ".data-validation.txt", "data_multi\\"+ s + ".data-test.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
                    sdt.learnTree();
//                    System.out.println("Size: " + sdt.size() + "\t" + sdt.getErrors());
                     System.out.println(sdt.size() + "\t" + (1 - sdt.ErrorOfTree(sdt.X)) + "\t" + (1 - sdt.ErrorOfTree(sdt.V)) + "\t" +(1 - sdt.ErrorOfTree(sdt.T)));
                     //System.out.println(sdt.toString());

//                }
//            }
        }

//        for (String s : CLASSIFY) {
//            System.out.println("CLASS " + s);
//            for (int i = 1; i <= 5; i++) {
//                for (int j = 1; j <= 2; j++) {
//                    SDT sdt = new SDT( "data_sdt\\"+ s+ "\\"+s + "-train-" + i + "-" + j + ".txt", "data_sdt\\"+ s+ "\\"+s  + "-validation-" + i + "-" + j + ".txt",  "data_sdt\\"+ s+ "\\"+s +  "-test.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
//                    sdt.learnTree();
//                    System.out.println("Size: " + sdt.size() + "\t" + sdt.getErrors());
//                    //System.out.println(sdt.toString());
//
//                }
//            }
//        }
////
//        for (String s : REGRESS) {
//            System.out.println("REGRESS " + s);
//            for (int i = 1; i <= 5; i++) {
//                for (int j = 1; j <= 2; j++) {
//                    SDT sdt = new SDT("data_sdt\\" + s + "\\" + s + "-train-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-validation-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-test.txt", false, LEARNING_RATE, EPOCH, MAX_STEP);
//                    sdt.learnTree();
//                    System.out.println("Size: " + sdt.size() + "\t" + sdt.getErrors());
//                    //   System.out.println(sdt.toString());
//                }
//            }
//        }
    }
}
