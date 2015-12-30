package graph;

import misc.Instance;

import java.util.ArrayList;

/**
 * Defines the functions which are needed by Graph class
 */
public interface Graphable {
    /**
     * Returns the predicted result of the given instance
     *
     * @param instance The instance (use the class of misc package)
     * @return The class value
     */
    double predicted_class(Instance instance);

    /**
     * The number of classes of dataset
     *
     * @return The number of classes of dataset
     */
    int getClassCount();

    /**
     * The number of attributes of the dataset
     *
     * @return The number of attributes of the dataset
     */
    int getAttributeCount();

    /**
     * Training dataset, it will be used when scatter plot is drawn
     *
     * @return Training dataset
     */
    ArrayList<Instance> getInstances();
}
