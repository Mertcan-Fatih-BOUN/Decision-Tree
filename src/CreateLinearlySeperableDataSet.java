import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by mertcan on 11.10.2015.
 */
public class CreateLinearlySeperableDataSet {
    int numberOfClasses = 4;
    int numberOfInstances = 50;
    static Random r = new Random();

    public static void main(String[] args){
        String dataSet = "";
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 50; j++){
                int a1 = r.nextInt(50) + i * 50;
                int a2 = r.nextInt(50) + i * 50;
                dataSet += a1 + "," + a2 + "," + (char)('a' + i) + "\n";
            }
        }
        writeToFile(dataSet, "data_set_1.data.txt");
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
