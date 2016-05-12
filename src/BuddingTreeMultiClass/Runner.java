package BuddingTreeMultiClass;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static BuddingTreeMultiClass.SetReader.getDataset;
import static BuddingTreeMultiClass.SetReader.getGithubDataset;
import static BuddingTreeMultiClass.SetReader.getGithubDatasetNoTag;

public class Runner {

    static boolean g_newversion = false;

    public static void main(String[] args) throws IOException {
        ArrayList<Instance>[] sets = getGithubDataset();
        BTM btm = new BTM(sets[0], sets[1], 1, 100, 0.00001,7);
        System.out.println(SetReader.tag_size + " " + sets[0].get(0).x.length + " " + btm.LEARNING_RATE + " " + btm.LAMBDA + " " + g_newversion);
        System.out.printf("%2s %3s %5s %5s %5s %5s", "e", "Sz", "TrMap", "TrPre", "VaMap", "VaPre");
        for (int  i= 0; i < 38; i++)
            System.out.printf(" %5s","C" + i + "TM" );
        for (int  i= 0; i < 38; i++)
            System.out.printf(" %5s","C" + i + "TP" );
        for (int  i= 0; i < 38; i++)
            System.out.printf(" %5s","C" + i + "VM" );
        for (int  i= 0; i < 38; i++)
            System.out.printf(" %5s","C" + i + "TP" );
        System.out.println("");
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
