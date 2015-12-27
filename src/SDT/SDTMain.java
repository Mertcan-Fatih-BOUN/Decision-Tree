package SDT;

import java.io.IOException;
import java.util.Locale;


public class SDTMain {
    public static double LEARNING_RATE = 10;
    public static int MAX_STEP = 10;
    public static int EPOCH = 20;

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);
        String[] CLASSIFY = new String[]{"ringnorm", "breast", "spambase", "twonorm", "ringnorm", "german", "magic", "pima", "polyadenylation", "satellite47", "musk2"};
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
//

//        SDT sdt4 = new SDT( "iris.data.txt", "iris.data.txt", "iris.data.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
//        sdt4.learnTree();
//        System.out.println("Size: " + sdt4.size() + "\t" + sdt4.getErrors());

       // SDT sdt3 = new SDT("data_sdt\\mnist\\mnist.txt", "data_sdt\\mnist\\mnist.txt", "data_sdt\\mnist\\mnist.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
        SDT sdt3 = new SDT("data_sdt\\mnist\\mnist_ordered_01.txt", "data_sdt\\mnist\\mnist_ordered_01.txt", "data_sdt\\mnist\\mnist_ordered_01.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
        sdt3.learnTree();
        System.out.println("Size: " + sdt3.size() + "\t" + sdt3.getErrors());
//
//        SDT sdt2 = new SDT( "data_set_nonlinear_1.data.txt", "data_set_nonlinear_1.data.txt", "data_set_nonlinear_1.data.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
//        sdt2.learnTree();
//        while(sdt2.ErrorOfTree(sdt2.T) > 0.2){
//            sdt2 = new SDT( "data_set_nonlinear_1.data.txt", "data_set_nonlinear_1.data.txt", "data_set_nonlinear_1.data.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
//            sdt2.learnTree();
//        }
//        System.out.println("Size: " + sdt2.size() + "\t" + sdt2.getErrors());

        for (String s : CLASSIFY) {
            System.out.println("CLASS " + s);
            for (int i = 1; i <= 5; i++) {
                for (int j = 1; j <= 2; j++) {
                    SDT sdt = new SDT( "data_sdt\\"+ s+ "\\"+s + "-train-" + i + "-" + j + ".txt", "data_sdt\\"+ s+ "\\"+s  + "-validation-" + i + "-" + j + ".txt",  "data_sdt\\"+ s+ "\\"+s +  "-test.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
                    sdt.learnTree();
                    System.out.println("Size: " + sdt.size() + "\t" + sdt.getErrors());
                    //System.out.println(sdt.toString());

                }
            }
        }
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
