package BuddingTreeMultiClass;


public class Instance {
    public int[] r;
    public double[] x;
    public double[] y;
    int id = 0;
    public double[] d;

    public int mirflicker_id = 0;

    public void setY(double[] y) {
        this.y = y;
        d = new double[y.length];
        for (int i = 0; i < y.length; i++) {
            d[i] = y[i] - r[i];
        }
    }

    public String toStringX() {
        String s = x[0] + "";
        for (int i = 1; i < x.length; i++) {
            s += " " + x[i];
        }
        return s;
    }

}
