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
        BTM btm = new BTM(sets[0], sets[1], 1, 1, 0.0001);
        btm.learnTree();
        btm.printToFile("print.txt");

        BTM btm2 = new BTM(sets[0], sets[1], "print.txt", 1);
        btm2.learnTree();
    }


}
