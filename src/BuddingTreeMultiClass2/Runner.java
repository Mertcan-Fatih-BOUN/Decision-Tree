package BuddingTreeMultiClass2;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static BuddingTreeMultiClass2.SetReader.getDataset;
import static BuddingTreeMultiClass2.SetReader.getGithubDataset;
import static BuddingTreeMultiClass2.SetReader.getGithubDatasetNoTag;

public class Runner {

    static boolean g_newversion = true;

    public static void main(String[] args) throws IOException {
        ArrayList<Instance>[] sets = getGithubDataset();
        System.out.println(SetReader.tag_size);
        BTM btm = new BTM(sets[0], sets[1], 0.8, 100, 0.001);
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
