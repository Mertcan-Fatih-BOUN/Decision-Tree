package SDT;

public class Util {
    public static double rand(double s, double e) {
        if (e < s) {
            double t = e;
            e = s;
            s = t;
}

        return (e - s) * Math.random() + s;
    }

    public static double sigmoid(double x) {
        return 1.0 / (1 + Math.exp(-x));
    }

    public static double dotProduct(double[] x, double[] y) {
        double result = 0;
        for (int i = 0; i < x.length; i++)
            result += x[i] * y[i];
        return result;
    }
}
