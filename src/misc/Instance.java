package misc;

public class Instance {
    public double classValue;
    public double[] attributes;

    public Instance(double classValue, double[] attributes) {
        this.classValue = classValue;
        this.attributes = attributes;
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