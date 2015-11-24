package SDT;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Queue;


public class SDTMain {
    public static double LEARNING_RATE = 10;
    public static int MAX_STEP = 10;
    public static int EPOCH = 25;
    public static Queue<Node> split_q = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);
       // String[] CLASSIFY = new String[]{"breast", "spambase", "twonorm", "ringnorm", "german", "magic", "pima", "polyadenylation", "satellite47", "musk2"};
        String[] REGRESS = new String[]{"abalone", "boston", "add10", "comp", "california", "concrete", "puma8fh", "puma8nh", "puma8fm", "puma8nm"};
        String[] CLASSIFY = new String[]{ "spambase"};

//        SDT sdt = new SDT("data_sdt\\breast\\breast-train-1-1.txt", "data_sdt\\breast\\breast-validation-1-1.txt", "data_sdt\\breast\\breast-test.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
//        sdt.learnTree();
//        System.out.println(sdt.getErrors());
//        System.out.println(sdt.toString());

//        SDT sdt = new SDT("data_sdt\\boston\\boston-train-1-1.txt", "data_sdt\\boston\\boston-validation-1-1.txt", "data_sdt\\boston\\boston-test.txt", false, LEARNING_RATE, EPOCH, MAX_STEP);
//        sdt.learnTree();
//        System.out.println(sdt.getErrors());
//        System.out.println(sdt.toString());

        boolean classify = true;
//        classify = false;

        if(classify) {
            Node.hardInit = false;
            for (String s : CLASSIFY) {
                System.out.println("CLASS " + s);
                for (int i = 1; i <= 5; i++) {
                    for (int j = 1; j <= 2; j++) {
                        SDT sdt = new SDT("data_sdt\\" + s + "\\" + s + "-train-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-validation-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-test.txt", true, LEARNING_RATE, EPOCH, MAX_STEP);
                        sdt.learnTree();
                        System.out.println(sdt.getErrors());
                        System.out.println(sdt.toString());
                    }
                }
            }
        }else {
            Node.hardInit = true;
            for (String s : REGRESS) {
                System.out.println("REGRESS " + s);
                for (int i = 1; i <= 5; i++) {
                    for (int j = 1; j <= 2; j++) {
                        SDT sdt = new SDT("data_sdt\\" + s + "\\" + s + "-train-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-validation-" + i + "-" + j + ".txt", "data_sdt\\" + s + "\\" + s + "-test.txt", false, LEARNING_RATE, EPOCH, MAX_STEP);
                        sdt.learnTree();
                        System.out.println(sdt.getErrors());
                        System.out.println(sdt.toString());
                    }
                }
            }
        }
    }
}
