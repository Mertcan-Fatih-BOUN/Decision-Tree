package Readers;


import java.util.ArrayList;
import java.util.Arrays;

public class FlickerDataSet extends DataSet {
    public String[] POTENTIAL_LABELS;
    public int tag_size;
    public int[] class_counts = new int[38];

    public void setPotentialLabels(String[] POTENTIAL_LABELS){
        this.POTENTIAL_LABELS = POTENTIAL_LABELS;
        CLASS_NAMES = new ArrayList<>();
        for(int i = 0; i < POTENTIAL_LABELS.length; i++)
            CLASS_NAMES.add(POTENTIAL_LABELS[i]);
    }
}
