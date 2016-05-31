package BuddingTreeMultiClass.readers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class DataSet {
    public String getName() {
        return name;
    }

    public enum TYPE {REGRESSION, BINARY_CLASSIFICATION, MULTI_CLASS_CLASSIFICATION, MULTI_LABEL_CLASSIFICATION}

    public String name = "";
    public ArrayList<Instance> TRAINING_INSTANCES;
    public ArrayList<Instance> VALIDATION_INSTANCES;
    public TYPE type;
    public double[] learning_rate_modifier;

    public DataSet(String name, ArrayList<Instance> X, ArrayList<Instance> V, TYPE type, double[] learning_rate_modifier) {
        this.name = name;
        this.TRAINING_INSTANCES = X;
        this.VALIDATION_INSTANCES = V;
        this.type = type;
        if (learning_rate_modifier == null) {
            this.learning_rate_modifier = new double[TRAINING_INSTANCES.get(0).x.length];
            Arrays.fill(this.learning_rate_modifier, 0);
        } else
            this.learning_rate_modifier = learning_rate_modifier;
    }

    public DataSet(String name, ArrayList<Instance> X, ArrayList<Instance> V, TYPE type) {
        this(name, X, V, type, null);
    }
}
