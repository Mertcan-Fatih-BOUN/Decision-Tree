package BuddingTreeMultiClass3;

import java.util.Arrays;

public class Error {
    private double cross_entropy = 0;
    private int[] false_negative;
    private int[] false_positive;
    private int[] true_positive;
    private int[] true_negative;
    private int instance_count;

    double absolute_diff = 0;

    double classic_misclass = 0;

    public Error(int class_count, int instance_count) {
        false_negative = new int[class_count];
        false_positive = new int[class_count];
        true_positive = new int[class_count];
        true_negative = new int[class_count];
        Arrays.fill(false_negative, 0);
        Arrays.fill(false_positive, 0);
        Arrays.fill(true_positive, 0);
        Arrays.fill(true_negative, 0);
        this.instance_count = instance_count;
    }

    public void addClassification(int y, int r, int c) {
        absolute_diff += Math.abs(y - r);
        if (y == r) {
            if (y == 1)
                true_positive[c]++;
            else
                true_negative[c]++;
        } else {
            if (y == 1)
                false_positive[c]++;
            else
                false_negative[c]++;
        }
    }

    public double getMeanAbsDif() {
        return absolute_diff / instance_count;
    }

    public double getClassficationError() {
        return sum(true_positive) / (sum(true_positive) + sum(false_negative));
    }

    public double getTrueClass() {
        return (sum(true_positive) + sum(true_negative)) / (sum(true_positive) + sum(true_negative) + sum(false_negative) + sum(false_positive));
    }

    public void addClassicMissClass() {
        classic_misclass++;
    }

    public double getClassic_misclass() {
        return 1 - (classic_misclass / instance_count);
    }

    public String toString() {
        return "tp/(tp+fn) :\t" + getClassficationError() +
                "\nt/(t+f) :\t" + getTrueClass() +
                "\nclassic miss :\t" + getClassic_misclass() +
                "\nt mean abs diff :\t" + getMeanAbsDif() +
                "\n" + "True positive :\t" + Arrays.toString(true_positive) +
                "\n" + "True negative :\t" + Arrays.toString(true_negative) +
                "\n" + "False positive :\t" + Arrays.toString(false_positive) +
                "\n" + "False negative :\t" + Arrays.toString(false_negative);
    }

    public static double sum(int[] x) {
        double sum = 0;
        for (double a : x)
            sum += a;
        return sum;
    }
}
