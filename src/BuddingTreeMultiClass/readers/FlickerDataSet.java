package BuddingTreeMultiClass.readers;


import java.util.ArrayList;

public class FlickerDataSet extends DataSet {
    public String[] POTENTIAL_LABELS;
    public int tag_size;
    public int[] class_counts = new int[38];


    public FlickerDataSet(String name, ArrayList<Instance> X, ArrayList<Instance> V, TYPE type, double[] learning_rate_modifier,
                          String[] POTENTIAL_LABELS, int tag_size, int[] class_counts) {
        super(name, X, V, type, learning_rate_modifier);
        this.tag_size = tag_size;
        this.POTENTIAL_LABELS = POTENTIAL_LABELS;
        this.class_counts = class_counts;
    }

    public FlickerDataSet(String name, ArrayList<Instance> X, ArrayList<Instance> V, TYPE type,
                          String[] POTENTIAL_LABELS, int tag_size, int[] class_counts) {
        this(name, X, V, type, null, POTENTIAL_LABELS, tag_size, class_counts);
    }
}
