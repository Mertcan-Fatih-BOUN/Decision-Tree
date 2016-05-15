package kNN;

import BuddingTreeMultiClass.Error2;
import BuddingTreeMultiClass.Instance;

import java.util.*;

public class kNN {
    public final int k;
    public final ArrayList<Instance> X;
    public final ArrayList<Instance> V;
    public final int CLASS_COUNT;
    public final int TOTAL_SIZE;
    private double count = 0;

    public kNN(int k, ArrayList<Instance> X, ArrayList<Instance> V) {
        this.k = k;
        this.X = X;
        this.V = V;
        this.CLASS_COUNT = X.get(0).r.length;
        this.TOTAL_SIZE = X.size() * V.size();
    }

    public void run() {
        for (Instance instanceA : V) {
            ArrayList<Distance> distances = new ArrayList<>();
            for (Instance instanceB : X) {
                distances.add(new Distance(instanceA, instanceB));
            }
            Collections.sort(distances);
            instanceA.y = new double[instanceA.r.length];
            Arrays.fill(instanceA.y, 0);
            for (int i = 0; i < k; i++) {
                for (int j = 0; j < instanceA.y.length; j++) {
                    instanceA.y[j] += distances.get(i).instanceB.r[j];
                }
            }
            for (int j = 0; j < instanceA.y.length; j++) {
                instanceA.y[j] = instanceA.y[j] / k;
            }
            System.out.printf("Completed: %.2f\n", count++ / TOTAL_SIZE);
        }
    }

    public Error2 MAP_error() {
        Error2 error2 = new Error2(CLASS_COUNT, V.size());
        for (int i = 0; i < CLASS_COUNT; i++) {
            double error = 0;
            double positive_count = 0;
            double pre_count = 0;
            final int finalI = i;
            Collections.sort(V, (o1, o2) -> Double.compare(o2.y[finalI], o1.y[finalI]));

            for (int j = 0; j < V.size(); j++) {
                if (V.get(j).r[i] == 1) {
                    if (j < 50)
                        pre_count++;
                    positive_count++;
                    error += (positive_count * 1.0) / (j + 1);
                }
            }

            error /= positive_count;

            error2.MAP[i] = error;
            error2.precision[i] = pre_count / 50.0f;
        }

        return error2;
    }


    class Distance implements Comparable<Distance> {
        Instance instanceA;
        Instance instanceB;
        double distance;

        public Distance(Instance instanceA, Instance instanceB) {
            this.instanceA = instanceA;
            this.instanceB = instanceB;
            this.distance = calculateDistance();
        }

        private double calculateDistance() {
            double r = 0;
            for (int i = 0; i < instanceA.x.length; i++) {
                r += Math.pow(instanceA.x[i] - instanceB.x[i], 2);
            }
            return Math.sqrt(r);
        }

        public int compareTo(Distance distance) {
            return Double.compare(distance.distance, this.distance);
        }
    }
}
