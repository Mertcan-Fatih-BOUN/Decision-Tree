package Readers;

import java.util.ArrayList;

public class DataSet {
    public enum TYPE {REGRESSION, BINARY_CLASSIFICATION, MULTI_CLASS_CLASSIFICATION, MULTI_LABEL_CLASSIFICATION}

    public ArrayList<Instance> TRAINING_INSTANCES;
    public ArrayList<Instance> VALIDATION_INSTANCES;
    public ArrayList<String> CLASS_NAMES;
    public TYPE type;

    public void setPotentialLabels(String[] POTENTIAL_LABELS){
        CLASS_NAMES = new ArrayList<>();
        for(int i = 0; i < POTENTIAL_LABELS.length; i++)
            CLASS_NAMES.add(POTENTIAL_LABELS[i]);
    }
}
