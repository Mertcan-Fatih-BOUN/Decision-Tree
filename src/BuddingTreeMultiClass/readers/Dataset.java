package BuddingTreeMultiClass.readers;

import java.util.ArrayList;

public class DataSet {
    public enum TYPE {REGRESSION, BINARY_CLASSIFICATION, MULTI_CLASS_CLASSIFICATION, MULTI_LABEL_CLASSIFICATION}

    public ArrayList<Instance> TRAINING_INSTANCES;
    public ArrayList<Instance> VALIDATION_INSTANCES;
    public TYPE type;
}
