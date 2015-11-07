import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by mertcan on 11.10.2015.
 */
public class CreateLinearlySeperableDataSet {
    int numberOfClasses = 8;
    int numberOfInstances = 50;
    static Random r = new Random();

    public static void main(String[] args){
        String dataSet = "";
        String draw = "";
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 50; j++){
                int a1 = r.nextInt(50) + i * 100;
                int a2 = r.nextInt(50) + i * 100;
                dataSet += a1 + "," + a2 + "," + a1 + "," + a2 + "," + (char)('a' + i) + "\n";
                draw += "(" + a1 + "," + a2 + "),";
            }
        }
        writeToFile(dataSet, "data_set_1_4.data.txt");
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
