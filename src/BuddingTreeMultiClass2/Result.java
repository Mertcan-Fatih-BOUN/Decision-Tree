package BuddingTreeMultiClass2;

/**
 * Created by mertcan on 27.4.2016.
 */
public class Result {
    public double trainingMap;
    public double trainingPrec;
    public double validationMap;
    public double valitadionPrec;
    public int size;
    public int effsize;
    public int epoch;
    public double[] trainingMapAll;
    public double[] traininPrecAll;
    public double[] validationMapAll;
    public double[] validationPrecAll;

    public double getTrainingMap() {
        return trainingMap;
    }

    public void setTrainingMap(double trainingMap) {
        this.trainingMap = trainingMap;
    }

    public double getTrainingPrec() {
        return trainingPrec;
    }

    public void setTrainingPrec(double trainingPrec) {
        this.trainingPrec = trainingPrec;
    }

    public double getValidationMap() {
        return validationMap;
    }

    public void setValidationMap(double validationMap) {
        this.validationMap = validationMap;
    }

    public double getValitadionPrec() {
        return valitadionPrec;
    }

    public void setValitadionPrec(double valitadionPrec) {
        this.valitadionPrec = valitadionPrec;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getEffsize() {
        return effsize;
    }

    public void setEffsize(int effsize) {
        this.effsize = effsize;
    }

    public int getEpoch() {
        return epoch;
    }

    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }

    public double[] getTrainingMapAll() {
        return trainingMapAll;
    }

    public void setTrainingMapAll(double[] trainingMapAll) {
        this.trainingMapAll = trainingMapAll;
    }

    public double[] getTraininPrecAll() {
        return traininPrecAll;
    }

    public void setTraininPrecAll(double[] traininPrecAll) {
        this.traininPrecAll = traininPrecAll;
    }

    public double[] getValidationMapAll() {
        return validationMapAll;
    }

    public void setValidationMapAll(double[] validationMapAll) {
        this.validationMapAll = validationMapAll;
    }

    public double[] getValidationPrecAll() {
        return validationPrecAll;
    }

    public void setValidationPrecAll(double[] validationPrecAll) {
        this.validationPrecAll = validationPrecAll;
    }
}
