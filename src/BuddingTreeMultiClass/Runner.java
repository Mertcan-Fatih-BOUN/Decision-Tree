package BuddingTreeMultiClass;


import BuddingTreeMultiClass.readers.DataSet;
import BuddingTreeMultiClass.readers.MSDReader;

import java.io.IOException;

public class Runner {

    public static int similar_count = 5;

    public static void main(String[] args) throws IOException {
        DataSet dataSet = MSDReader.getSoundOnly();
        System.out.println("File read");
        double learning_rate = 0.5;
        int epoch = 100;
        double lambda = 0.0001;
        double learning_rate_decay = 0.99;
        System.out.println("Dataset: " + dataSet.name);
        System.out.println("learning_rate: " + learning_rate + " lambda: " + lambda + " learning_rate_decay :" + learning_rate_decay);
        System.out.println("Special note: ");
        BTM btm = new BTM(dataSet);
        //btm.enableSaveFile("btm_notag_v2__.txt");
        //btm.enable_g_new_version();s
        btm.learnTree(learning_rate, epoch, lambda, learning_rate_decay);


//System.out.println(FlickerReader.tag_size + " " + sets[0].get(0).x.length + " " + sets[0].size() + " " + sets[1].size() + " " + btm.LEARNING_RATE + " " + btm.LAMBDA + " " + g_newversion + " " + LEARNING_RATE_DECAY);


//        btm.printToFile(toFile);
//
//        BTM btm2 = new BTM(sets[0], sets[1], "btm_tag.txt", 0);
////        BTM btm2 = new BTM(sets[0], sets[1], "btm_notag.txt", 0);
//        btm2.treeNodeRoot = new TreeNode();
////        btm2.find_ymeans(sets[1]);
////        System.out.println("Size: " + btm2.size() + " " + btm2.eff_size() + "\n" + btm2.getErrors() + "\n-----------------------\n");
////        btm2.write_ymeans();
////        System.out.println(BTM.ROOT.toStringWeights() + "\n" + BTM.ROOT.leftNode.toStringWeights() + "\n" + BTM.ROOT.rightNode.toStringWeights());
////        System.out.println("\n" + sets[0].get(0).toStringX() + "\n" + sets[0].get(1).toStringX() + "\n" + sets[0].get(2).toStringX());
//
//        class_counts = FlickerReader.class_counts(sets[0]);
//        for(int i = 0; i< 38; i++)
//            System.out.println(class_counts[i]);
//        btm2.findAllMinDifferences(sets[0]);
//        btm2.findScaledRhos();
//        btm2.findCumulativeG(sets[0]);
//
//        btm2.treeNodeRoot.printToFile("tree_tag.png");
////        btm2.treeNodeRoot.printToFile("tree_notag.png");
//        System.out.println(BTM.ROOT.toStringIndexesAndRhos(0, sets[0]));

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
