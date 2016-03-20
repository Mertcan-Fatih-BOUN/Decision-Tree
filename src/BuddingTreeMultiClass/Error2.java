package BuddingTreeMultiClass;

import java.lang.reflect.Array;
import java.util.Arrays;


public class Error2 {
    double[] MAP;
    double[] precision;

    public Error2(int class_count, int instance_count) {
        MAP = new double[class_count];
        precision = new double[class_count];
    }

    public String toString() {
        String s = "MAP: \n";
        double sumaMap = 0;
        for (double aMAP : MAP) {
            sumaMap += aMAP;
            s += String.format("%.2f ", aMAP);
        }
        s += String.format("\nMAP Average : %.2f\n", sumaMap / MAP.length);

        s += "Precission: \n";
        double sumprecision = 0;
        for (double aprecission : precision) {
            sumprecision += aprecission;
            s += String.format("%.2f ", aprecission);
        }
        s += String.format("\nPrecission Average : %.2f", sumprecision / precision.length);
        return s;
    }
}
