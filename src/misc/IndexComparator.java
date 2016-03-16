package misc;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by mertcan on 15.3.2016.
 */
public class IndexComparator implements Comparator<Integer>
{
    private final ArrayList<Double> array;

    public IndexComparator(ArrayList<Double> array)
    {
        this.array = array;
    }

    public Integer[] createIndexArray()
    {
        Integer[] indexes = new Integer[array.size()];
        for (int i = 0; i < array.size(); i++)
        {
            indexes[i] = i; // Autoboxing
        }
        return indexes;
    }

    @Override
    public int compare(Integer index1, Integer index2)
    {
        // Autounbox from Integer to int to use as array indexes
        return compare(array.get((index1)),array.get((index2)));
    }

    private static int compare(double a, double b) {
        return a < b ? 1
                : a > b ? -1
                : 0;
    }
}