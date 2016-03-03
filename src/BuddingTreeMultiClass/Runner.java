package BuddingTreeMultiClass;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import static BuddingTreeMultiClass.SetReader.getDataset;

public class Runner {
    public static void main(String[] args) throws IOException {
        ArrayList<Instance>[] sets = getDataset(true, true, true);
        BTM btm = new BTM(sets[0], sets[1], 1, 1, 0.001);
        btm.learnTree();
        System.out.println(btm.size());
        System.out.println(btm.getErrors());
    }


}
