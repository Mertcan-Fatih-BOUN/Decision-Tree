package BuddingTree;

import java.io.IOException;
import java.util.Locale;


public class BTMain {
    public static double LEARNING_RATE = 0.35;
    //    public static int MAX_STEP = 10;
    public static int EPOCH =10;
    public static boolean isMnist = false;

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);
        String[] CLASSIFY_MULTI = new String[]{"balance-scale", "cmc", "dermatology", "ecoli", "glass", "optdigits", "page-blocks", "pendigits", "segment", "yeast"};
        String[] CLASSIFY = new String[]{"breast", "spambase", "twonorm", "ringnorm", "german", "magic", "pima", "polyadenylation", "satellite47", "musk2"};
        String[] REGRESS = new String[]{"abalone", "boston", "add10", "comp", "california", "concrete", "puma8fh", "puma8nh", "puma8fm", "puma8nm"};
        System.out.println("Learning rate: " + LEARNING_RATE);
        //  String[] CLASSIFY = new String[]{ "breast"};
//        REGRESS = new String[]{"abalone"};
//        SDT sdt = new SDT("data_sdt\\breast\\breast-train-1-1.txt", "data_sdt\\breast\\breast-validation-1-1.txt", "data_sdt\\breast\\breast-test.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
//        sdt.learnTree();
//        System.out.println(sdt.getErrors());
//        System.out.println(sdt.toString());

//        SDT sdt = new SDT("data_sdt\\boston\\boston-train-1-1.txt", "data_sdt\\boston\\boston-validation-1-1.txt", "data_sdt\\boston\\boston-test.txt", false, LEARNING_RATE, EPOCH, MAX_STEP);
//        sdt.learnTree();
//        System.out.println(sdt.getErrors());
//        System.out.println(sdt.toString());


        for (String s : CLASSIFY_MULTI) {
//            System.out.println("CLASS " + s);
//            for (int i = 1; i <= 5; i++) {
//                for (int j = 1; j <= 2; j++) {
            BT sdt = new BT( "data_multi\\"+ s + ".data-train.txt",  "data_multi\\"+ s + ".data-validation.txt", "data_multi\\"+ s + ".data-test.txt", true, LEARNING_RATE, EPOCH);
            sdt.learnTree();
//            System.out.println("Size: " + sdt.size() + "\t" + sdt.effSize() + "\t" + sdt.getErrors());
            System.out.println(sdt.size() + "\t" + sdt.effSize() + "\t" + (1 - sdt.ErrorOfTree(sdt.X)) + "\t" + (1 - sdt.ErrorOfTree(sdt.V)) + "\t" +(1 - sdt.ErrorOfTree(sdt.T)));
            //System.out.println(sdt.toString());

//                }
//            }
        }

//        for (String s : CLASSIFY) {
//            System.out.println("CLASS " + s);
//            for (int i = 1; i <= 5; i++) {
//                for (int j = 1; j <= 2; j++) {
//                    BT sdt = new BT( "data_sdt\\"+ s+ "\\"+s + "-train-" + i + "-" + j + ".txt", "data_sdt\\"+ s+ "\\"+s  + "-validation-" + i + "-" + j + ".txt",  "data_sdt\\"+ s+ "\\"+s +  "-test.txt", true, LEARNING_RATE, EPOCH);
//                    sdt.learnTree();
//                    System.out.println(sdt.size() + "\t" + sdt.effSize() + "\t" + sdt.getErrors());
//                    //System.out.println(sdt.toString());
//                }
//            }
//        }
//        isMnist = true;
//        BT bt = new BT( "iris.data.txt", "iris.data.txt", "iris.data.txt", true, LEARNING_RATE, EPOCH);
//        bt.learnTree();
//        System.out.println("Size: " + bt.size() + "\t" + bt.getErrors());
//        BT bt = new BT("data_set_nonlinear_2.data.txt", "data_set_nonlinear_2_val.data.txt", "data_set_nonlinear_2_test.data.txt", true, LEARNING_RATE, EPOCH);
//        BT bt = new BT("data_set_nonlinear_1.data.txt", "data_set_nonlinear_1.data.txt", "data_set_nonlinear_1.data.txt", true, LEARNING_RATE, EPOCH);
//        BT bt = new BT("data_sdt\\mnist\\mnist_train.txt", "data_sdt\\mnist\\mnist_validation.txt", "data_sdt\\mnist\\mnist_test.txt", true, LEARNING_RATE, EPOCH);
////        BT bt = new BT("data_sdt\\mnist\\mnist_ordered_03.txt", "data_sdt\\mnist\\mnist_ordered_03.txt", "data_sdt\\mnist\\mnist_ordered_03.txt", true, LEARNING_RATE, EPOCH);
////        BT bt = new BT("data_sdt\\optdigits\\train.txt", "data_sdt\\optdigits\\validation.txt", "data_sdt\\optdigits\\test.txt", true, LEARNING_RATE, EPOCH);
//        bt.learnTree();
//        System.out.println("Size: " + bt.size() + "\t" + bt.getErrors());
//
//        System.out.println(bt.size() + "\t" + bt.effSize() + "\t" + bt.getErrors());

////
//        for (String s : REGRESS) {
//            System.out.println("REGRESS " + s);
//            for (int i = 1; i <= 5; i++) {
//                for (int j = 1; j <= 2; j++) {
//                    BT sdt = new BT("data_sdt\\" + s + "\\" + s + "-train-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-validation-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-test.txt", false, LEARNING_RATE, EPOCH);
//                    sdt.learnTree();
//                    System.out.println(sdt.size() + "\t" + sdt.effSize() + "\t" + sdt.getErrors());
////                    break;
//                    //   System.out.println(sdt.toString());
//                }
////                break;
//            }
        //   }
    }


}
