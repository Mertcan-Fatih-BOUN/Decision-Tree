package misc;

import java.util.ArrayList;

public class Instance {
    public double classValue;
    public double[] attributes;
    public ArrayList<Integer> classNumbers;

    public Instance(double classValue, double[] attributes) {
        this.classValue = classValue;
        this.attributes = attributes;
    }

    public Instance(int classNumber, ArrayList<Integer> classNumbers, double[] attributes) {
        this.classValue = classNumber;
        this.attributes = attributes;
        this.classNumbers = new ArrayList<>(classNumbers);
    }

    public String toString() {
        String s = "";
        for (double d : attributes) {
            s += "\t" + d;
        }
        s += "\t" + classValue;
        return s;
    }
}