package SDTMultiClass;



import BuddingTreeMultiClass.Instance;
import BuddingTreeMultiClass.SetReader;
import SDT.SDT;

import java.io.IOException;
import java.util.ArrayList;

import static BuddingTreeMultiClass.SetReader.getGithubDatasetNoTag;

public class Runner {

    static boolean g_newversion = false;

    public static void main(String[] args) throws IOException {
        ArrayList<Instance>[] sets = getGithubDatasetNoTag();
        SDTM sdtm = new SDTM(sets[0], sets[1], 0.3, 50, 8);
        sdtm.learnTree();
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
