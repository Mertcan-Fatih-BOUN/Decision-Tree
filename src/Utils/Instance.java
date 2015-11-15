package Utils;

import java.util.ArrayList;

/**
 * Created by mertcan on 15.11.2015.
 */
public class Instance {
    public int classNumber;
    public double[] attributes;
    public int classCode = -1;

    public String className;

    public Instance(String name) {
        className = name;
    }

    public Instance(int classNumber, double[] attributes) {
        this.classNumber = classNumber;
        this.attributes = attributes;
    }

    public Instance(double[] attributes) {
        this.attributes = attributes;
    }

    public Instance(String className, int classNumber, double[] attributes) {
        this.classNumber = classNumber;
        this.attributes = attributes;
        this.className = className;
    }

    public String toString() {
        String s = "";
        for (double d : attributes) {
            s += "\t" + d;
        }
        s += "\t" + classNumber;
        return s;
    }
}
