package eu.reusing.audiotacho.utils;

/**
 * Created by Tobi on 23.01.2016.
 */
public class FifoBuffer {

    private int size;
    private double[] data;
    private int idx;
    private double sum = 0;
    public FifoBuffer(int size) {
        if (size <= 0)
            throw new RuntimeException("Size out of range");
        this.size = size;
        idx = 0;
        data = new double[size];
    }

    public void addDataPoint(double value)
    {
        sum -= data[idx];
        data[idx++] = value;

        if (idx >= size) idx = 0;

        sum += value;
    }

    public double getAverage()
    {
        return sum / size;
    }


}
