package BuddingTreeMultiClass;

import java.util.Arrays;

public class Error {
    private double cross_entropy = 0;
    private int[] false_negative;
    private int[] false_positive;
    private int[] true_positive;
    private int[] true_negative;
    private int instance_count;

    private int total_true = 0;
    private int total_fale = 0;

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
        if (y == r) {
            if (y == 1)
                true_positive[c]++;
            else
                true_negative[c]++;
            total_true++;
        } else {
            if (y == 1)
                false_positive[c]++;
            else
                false_negative[c]++;
            total_fale++;
        }
    }

    public void addCrossEntropy(double d) {
        cross_entropy += d;
    }

    public double getCross_entropy() {
        return cross_entropy / instance_count;
    }

    public double getClassficationError() {
        return (total_fale * 1.0) / (total_fale + total_true);
    }

    public String toString() {
        return "Cross entropy " + getCross_entropy() +
                "\n" + "True positive :" + Arrays.toString(true_positive) + "\n" +
                "\n" + "True negative :" + Arrays.toString(true_negative) + "\n" +
                "\n" + "False positive :" + Arrays.toString(false_positive) + "\n" +
                "\n" + "False negative :" + Arrays.toString(false_negative) + "\n";
    }
}
