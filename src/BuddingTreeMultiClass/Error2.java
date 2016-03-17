package BuddingTreeMultiClass;

import java.lang.reflect.Array;
import java.util.Arrays;


public class Error2 {
    double[] MAP;


    public Error2(int class_count, int instance_count) {
        MAP = new double[class_count];
        Arrays.fill(MAP, 0);
    }

    public String toString() {
        return Arrays.toString(MAP);
    }
}
