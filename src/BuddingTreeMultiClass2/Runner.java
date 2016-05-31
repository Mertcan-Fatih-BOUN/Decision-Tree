package BuddingTreeMultiClass2;


import BuddingTreeMultiClass.*;
import BuddingTreeMultiClass.readers.*;
import Readers.*;
import Readers.DataSet;
import Readers.FlickerReader;
import Readers.MSDReader;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


public class Runner {
    static String properties;
    static String currentDate;

    static boolean g_newversion = false;
    public static double down_learning_rate = 1;//if 1, works normal
    public static double punish_gama = 0.00;

    public static void main(String[] args) throws IOException {

//        DataSet dataSet = FlickerReader.getGithubDatasetNoTag();
        DataSet dataSet = MSDReader.getSoundOnly();
        System.out.println("File read");
        double learning_rate = 1;
        int epoch = 100;
        double lambda = 0.0001;
        double learnin_rate_decay = 0.99;
        int firstmodalsize = 30;
        BTM btm = new BTM(dataSet, learning_rate, epoch, lambda);
        //btm.enableSaveFile("btm_notag_v2__.txt");
        //btm.enable_g_new_version();s
        properties = "btm2 " + firstmodalsize + " " + dataSet.TRAINING_INSTANCES.get(0).x.length + " " + btm.LEARNING_RATE + " " + btm.LAMBDA + " " + g_newversion;
        System.out.println(properties);
        currentDate = Long.toString((new Date()).getTime());
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
