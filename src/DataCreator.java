import BuddingTree.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataCreator {

    public static void main(String[] args) throws IOException {
        final String FILE_NAME = "sinx_validation.txt";

        File regress_out = new File(FILE_NAME);
        BufferedWriter regressWriter = new BufferedWriter(new FileWriter(regress_out, true));


        for (int i = 0; i < 2000; i++) {
            double x = Util.rand(0, 2* Math.PI);
            regressWriter.write(x + " " + Math.sin(x) + "\n");
        }

        regressWriter.flush();

    }
}
