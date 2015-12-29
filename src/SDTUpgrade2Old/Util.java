package SDTUpgrade2Old;

import java.util.Comparator;

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

    public static double[] softmax(double[] sigmoid) {
        double[] ratios = new double[sigmoid.length];
        double total = exp_total(sigmoid);
        for(int i = 0; i < sigmoid.length; i++)
            ratios[i] = Math.exp(sigmoid[i]) / total;
        return ratios;
    }

    public static int argMax(double[] d){
        double tmp = d[0];
        int tmpIndex = 0;
        for(int i = 1; i < d.length; i++){
            if(d[i] > tmp){
                tmp = d[i];
                tmpIndex = i;
            }
        }
        return tmpIndex;
    }

    public static double max_value(double[] d){
        double tmp = d[0];
        int tmpIndex = 0;
        for(int i = 1; i < d.length; i++){
            if(d[i] > tmp){
                tmp = d[i];
                tmpIndex = i;
            }
        }
        return tmp;
    }

    public static double exp_total(double[] t){
        double total = 0;
        for(int i = 0; i < t.length; i++)
            total += Math.exp(t[i]);
        return total;
    }

    public static class ArrayIndexComparator implements Comparator<Integer>
    {
        private final double[] array;

        public ArrayIndexComparator(double[] array)
        {
            this.array = array;
        }

        public Integer[] createIndexArray()
        {
            Integer[] indexes = new Integer[array.length];
            for (int i = 0; i < array.length; i++)
            {
                indexes[i] = i; // Autoboxing
            }
            return indexes;
        }

        @Override
        public int compare(Integer index1, Integer index2)
        {
            // Autounbox from Integer to int to use as array indexes
            return ((Double)array[index1]).compareTo(array[index2]);
        }
    }
}
