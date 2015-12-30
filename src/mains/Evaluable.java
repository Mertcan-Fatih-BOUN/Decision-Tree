package mains;

/**
 * Created by Fatih on 30-Dec-15.
 */

import BuddingTree.Instance;

import java.util.ArrayList;

public  interface Evaluable {
    double predicted_class(Instance instance);
    int getClassCount();
    int getAttributeCount();
    ArrayList<Instance> getInstances();
}
