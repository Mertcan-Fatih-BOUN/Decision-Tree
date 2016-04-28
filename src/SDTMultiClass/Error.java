package SDTMultiClass;


public class Error {
    double[] MAP;
    double[] precision;

    public Error(int class_count, int instance_count) {
        MAP = new double[class_count];
        precision = new double[class_count];
    }

    public double getAverageMAP() {
        double r = 0;
        for (double d : MAP)
            r += d;

        return r / MAP.length;
    }

    public String toString() {
        String s = "MAP: \n";
        double sumaMap = 0;
        for (double aMAP : MAP) {
            sumaMap += aMAP;
            s += String.format("%.3f\n", aMAP);
        }
        s += String.format("\nMAP Average : %.3f\n", sumaMap / MAP.length);

        s += "Precission: \n";
        double sumprecision = 0;
        for (double aprecission : precision) {
            sumprecision += aprecission;
            s += String.format("%.3f\n", aprecission);
        }
        s += String.format("\nPrecission Average : %.3f", sumprecision / precision.length);
        return s;
    }
}
