package BuddingTreeMultiClass3;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static BuddingTreeMultiClass3.SetReader.getDataset;
import static BuddingTreeMultiClass3.SetReader.getGithubDataset;
import static BuddingTreeMultiClass3.SetReader.getGithubDatasetNoTag;
//g is multiple
public class Runner {
    static String properties;
    static String currentDate;
    static boolean g_newversion = false;

    static boolean rho_newversion = false;

    public static void main(String[] args) throws IOException {
        ArrayList<Instance>[] sets = getGithubDataset();
        BTM btm = new BTM(sets[0], sets[1], 0.6, 100, 0.001);
        properties = "btm3 rho_" + rho_newversion + "_" + SetReader.tag_size + " " + sets[0].get(0).x.length + " " + btm.LEARNING_RATE + " " + btm.LAMBDA + " " + g_newversion;
        System.out.println(properties);
        currentDate = Long.toString((new Date()).getTime());
        btm.learnTree();
//        btm.printToFile("print.txt");

//        BTM btm2 = new BTM(sets[0], sets[1], "print50.txt", 1);
//        btm2.followInstance(sets[1].get(0));
//
//        btm2.followInstance(sets[1].get(1));
//
//        btm2.followInstance(sets[1].get(2));
//
//        btm2.followInstance(sets[1].get(3));

//        btm2.learnTree();
    }

}