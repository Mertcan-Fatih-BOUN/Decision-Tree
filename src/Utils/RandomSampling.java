package Utils;

import misc.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Created by mertcan on 18.2.2016.
 */
public class RandomSampling {
    public static Random r = new Random();
    public static void main(String[] args){
        Integer[] indices = generateRandomIndices(5000, 51000);
        try {
            readFile("data_multi" + File.separator + "millionsong_yearpred_clsfirst-test.txt", indices);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Integer[] generateRandomIndices(int total, int limit){
        Integer[] indices = new Integer[total];
        ArrayList<Integer> queue =
                new ArrayList<>();
        for(int i = 0; i < total; i++){
            int rnd = r.nextInt(limit);
            while(queue.contains(rnd))
                rnd = r.nextInt(limit);
            queue.add(rnd);
        }
        indices = queue.toArray(indices);
        Arrays.sort(indices);
        for(int i = 0; i < total; i++)
            System.out.println(indices[i]);
        return indices;
    }

    private static void readFile(String filename, Integer[] indices) throws IOException {
        String line;
        String toFile = "";


        Scanner scanner = new Scanner(new File(filename));
        int i = 0;
        while (i < indices.length) {
            int next = 0;
            if(i > 0)
                next = indices[i - 1];
            for(int j = next + 1; j < indices[i]; j++)
                scanner.nextLine();
            line = scanner.nextLine();
            toFile += line + "\n";
            i++;
        }

        File file2 = new File("million_song_rnd_sample_clsfirst_test.txt");
        BufferedWriter writer2 = new BufferedWriter(new FileWriter(file2, true));
        writer2.write(toFile);
        writer2.flush();
        writer2.close();
    }
}
