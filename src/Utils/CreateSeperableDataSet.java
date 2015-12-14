package Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by mertcan on 11.10.2015.
 */
public class CreateSeperableDataSet {
    static int numberOfClasses = 2;
    static int numberOfInstances = 100;
    static Random r = new Random();
    static String fileName = "data_set_nonlinear_2.data.txt";

    public static void main(String[] args){
//        createLinearlySeperable();
        createNonLinearlySeperable();
    }
    private static void createLinearlySeperable(){
        String dataSet = "";
        String draw = "";
        for(int i = 0; i < numberOfClasses; i++){
            for(int j = 0; j < numberOfInstances; j++){
                int a1 = r.nextInt(50) + i * 100;
                int a2 = r.nextInt(50) + i * 100;
//                dataSet += a1 + " " + a2 + "," + (char)('a' + i) + "\n";
                dataSet += a1 + " " + a2 + " " + i + "\n";
                draw += "(" + a1 + "," + a2 + "),";
            }
        }
        writeToFile(dataSet, fileName);
        System.out.println(draw);
    }

    private static void createNonLinearlySeperable(){
        String dataSet = "";
        String draw = "";
        for(int i = 0; i < numberOfClasses; i++){
            for(int j = 0; j < numberOfInstances; j++){
                double a1 = 0, a2 = 0;
                if(i == 0) {
                    a1 = r.nextInt(20);
                    a2 = r.nextInt((int)Math.sqrt(400 - a1 * a1));
                }else if(i == 1){
                    a1 = r.nextInt(40);
                    int t = 0;
                    if(a1 <= 25) t = (int) Math.sqrt(625 - a1 * a1);
                    a2 = t + r.nextInt(40 - t);
                }else if(i == 2){
                    a1 = 30 + r.nextInt(20);
                    a2 = 30 + r.nextInt(20);
                }
                int rr = r.nextInt(2);
                if(rr == 0 && i != 2) {
                    a1 = -a1;
                }
                rr = r.nextInt(2);
                if(rr == 0 && i != 2) {
                    a2 = -a2;
                }
                a1 += 30;
                a2 += 40;
                a1 /= 100;
                a2 /= 100;
//                dataSet += a1 + " " + a2 + "," + (char)('a' + i) + "\n";
                dataSet += a1 + " " + a2 + " " + i + "\n";
                draw += "(" + a1 + "," + a2 + "),";
            }
        }
        writeToFile(dataSet, fileName);
        System.out.println(draw);
    }


    private static void writeToFile(String dataSet, String s) {
        BufferedWriter output = null;
        try {
            File file = new File(s);
            output = new BufferedWriter(new FileWriter(file));
            output.write(dataSet);
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            if ( output != null ) try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
