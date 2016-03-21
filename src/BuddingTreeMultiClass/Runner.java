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
        ArrayList<Instance>[] sets = getGithubDatasetNoTag();
//        BTM btm = new BTM(sets[0], sets[1], 1, 1, 0.0001);
//        btm.learnTree();
//        btm.printToFile("print.txt");

        BTM btm2 = new BTM(sets[0], sets[1], "print50.txt", 1);
        btm2.followInstance(sets[1].get(0));

        btm2.followInstance(sets[1].get(1));

        btm2.followInstance(sets[1].get(2));

        btm2.followInstance(sets[1].get(3));

//        btm2.learnTree();
    }


}
