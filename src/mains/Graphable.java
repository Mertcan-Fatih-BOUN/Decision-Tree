package mains;

import BuddingTree.Instance;

import java.util.ArrayList;

public interface Graphable {
    double predicted_class(Instance instance);

    int getClassCount();

    int getAttributeCount();

    ArrayList<Instance> getInstances();
}
