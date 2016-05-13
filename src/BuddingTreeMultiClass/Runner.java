package BuddingTreeMultiClass;


import java.io.IOException;
import java.util.ArrayList;

import static BuddingTreeMultiClass.SetReader.getGithubDatasetNoTag;

public class Runner {

    public static int similar_count = 5;
    static boolean g_newversion = true;

    public static void main(String[] args) throws IOException {
//        ArrayList<Instance>[] sets = getGithubDataset();
        ArrayList<Instance>[] sets = getGithubDatasetNoTag();
//        BTM btm = new BTM(sets[0], sets[1], 0.3, 10, 0.0001);
//        System.out.println(SetReader.tag_size + " " + sets[0].get(0).x.length + " " + btm.LEARNING_RATE + " " + btm.LAMBDA + " " + g_newversion);
//        btm.learnTree();
//        btm.printToFile("btm_notag.txt");

        BTM btm2 = new BTM(sets[0], sets[1], "btm_notag.txt", 0);
        btm2.treeNodeRoot = new TreeNode();
//        btm2.find_ymeans(sets[1]);
//        System.out.println("Size: " + btm2.size() + " " + btm2.eff_size() + "\n" + btm2.getErrors() + "\n-----------------------\n");
//        btm2.write_ymeans();
//        System.out.println(BTM.ROOT.toStringWeights() + "\n" + BTM.ROOT.leftNode.toStringWeights() + "\n" + BTM.ROOT.rightNode.toStringWeights());
//        System.out.println("\n" + sets[0].get(0).toStringX() + "\n" + sets[0].get(1).toStringX() + "\n" + sets[0].get(2).toStringX());

        btm2.findAllMinDifferences(sets[0]);
        btm2.findScaledRhos();
        btm2.findCumulativeG(sets[0]);

        btm2.treeNodeRoot.printToFile("tree.png");
        System.out.println(BTM.ROOT.toStringIndexesAndRhos(0, sets[0]));

//        System.out.println(BTM.ROOT.minDifferences(sets[0]));
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
