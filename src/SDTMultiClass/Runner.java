package SDTMultiClass;

import BuddingTreeMultiClass.Instance;

import java.io.IOException;
import java.util.ArrayList;

import static BuddingTreeMultiClass.SetReader.getGithubDatasetNoTag;

public class Runner {
    public static void main(String[] args) throws IOException {
        ArrayList<Instance>[] sets = getGithubDatasetNoTag();
        SDTM sdtm = new SDTM(sets[0], sets[1], 0.3, 50, 7);
        sdtm.learnTree();
    }
}