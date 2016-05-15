package kNN;


import BuddingTreeMultiClass.Error2;
import BuddingTreeMultiClass.Instance;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static BuddingTreeMultiClass.SetReader.*;

public class Runner {
    public static int k = 20;
    public static boolean noTag = true;

    public static void main(String[] args) throws FileNotFoundException {
        if (noTag)
            System.out.printf("kNN  k=%d without tags\n", k);
        else
            System.out.printf("kNN  k=%d with tags\n", k);

        ArrayList<Instance>[] sets;
        System.out.println("Starting to read input files");
        if (noTag)
            sets = getGithubDatasetNoTag();
        else
            sets = getGithubDataset();
        System.out.println("Completed to read input files");
        kNN knn = new kNN(k, sets[0], sets[1]);
        knn.run();
        Error2 error2 = knn.MAP_error();
        System.out.println(error2.toString());

    }
}
