package BuddingTreeMultiClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Fatih on 18-Mar-16.
 */
public class aa {
    public static void main(String[] args) {
        ArrayList<double[]> list = new ArrayList<>();
        list.add(new double[]{5,7,3,4,4});
        list.add(new double[]{2,0,5,2,5});
        list.add(new double[]{3,7,6,7,9});
        list.add(new double[]{7,4,2,5,2.1});
        list.add(new double[]{8,2,4,3,2.0});

        Collections.sort(list, ((o1, o2) ->  Double.compare(o2[4], o1[4])));

        for(double[] d : list)
            System.out.println(Arrays.toString(d));

    }
}
