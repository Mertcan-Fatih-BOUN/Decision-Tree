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
        BTM btm = new BTM(sets[0], sets[1], 1, 1000, 0.0001);
        btm.learnTree();
        System.out.println(btm.size());
        System.out.println(btm.getErrors());
    }


}
