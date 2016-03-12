package eu.reusing.audiotacho.dataprocessor;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class DataProcessor implements Runnable {

    private AudioRecord audioRecord;
    private static final String TAG = "DataProcessor";

    // these settings (like sample rate and audio format are "guaranteed" to work on all devices
    // other settings might not be supported
    public final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    public final int SAMPLE_RATE_IN_HZ = 44100;
    public final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    public final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private int bufferSizeInBytes = (SAMPLE_RATE_IN_HZ / 4) + 1; // needs to be even (2byte per capture)

    private double circumference = 0.314159;

    private boolean properlyInitialized = false;

    private SpeedDataConsumer consumer;
    private double distance;

    public DataProcessor(SpeedDataConsumer consumer) {
        this.consumer = consumer;
    }

    /*
         * Minimum number of samples between two capture events:
         * Based on caluclation: 100km/h with a 24inch tire gives 6080 samples per revolution (at 44.1kHz).
         * Cut that in halve for the possibility of two pickups per revolution and substrakt a safety margin
         */
    // CHANGED, does not reflect calculation
    @SuppressWarnings({"PointlessArithmeticExpression", "FieldCanBeLocal"})
    private final int MIN_SAMPLES_BETWEEN_CAPTURE = 5000 * (SAMPLE_RATE_IN_HZ / 44100);

    @SuppressWarnings("FieldCanBeLocal")
    private int threshold = 5000;

    private boolean stopRequested = false;

    private void startTacho() {
        int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (minBufferSize > bufferSizeInBytes) {
            Log.w(TAG, "BufferSize (" + bufferSizeInBytes + " Byte) too small. Using " + minBufferSize + " Byte");
            bufferSizeInBytes = minBufferSize;
        }
        Log.i(TAG, "BufferSize: " + bufferSizeInBytes);
        audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE_IN_HZ, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSizeInBytes);
        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e(TAG, "Error initializing AudioRecord object. Abort audio input processing.");
            properlyInitialized = false;
        } else {
            properlyInitialized = true;
            distance = 0;
            consumer.updateDistance(0);
            consumer.updateSpeedData(0);
            processingLoop();
        }
    }

    private void processingLoop() {
        if (!properlyInitialized) return;

        consumer.measuringStarted();
        audioRecord.startRecording();
        int chunkSize = SAMPLE_RATE_IN_HZ / 16;
        short[] chunk = new short[chunkSize];

        int interval = 0;

        while (!stopRequested) {

            int result = audioRecord.read(chunk, 0, chunkSize);
            if (result >= 0) {
                for (int i = 0; i < result; i++) {
                    int value = Math.abs(chunk[i]);
                    if (value > threshold && interval > MIN_SAMPLES_BETWEEN_CAPTURE) {
//                        System.out.println(interval);

                        updateInterval(interval);
//                        System.out.println(revTime);
                        interval = 0;
                    } else {
                        interval++;
                    }
                }


            }
        }
        audioRecord.stop();
        audioRecord.release();
        stopRequested = false;
        consumer.measuringStopped();

    }

    public void stopTacho() {
        stopRequested = true;
    }

    @Override
    public void run() {
        this.startTacho();
    }

    private void updateInterval(int interval) {
        double revTime = (double) interval / SAMPLE_RATE_IN_HZ;
        double speedMS = circumference / revTime;
        double speedKMH = speedMS * 3.6;
        consumer.updateSpeedData(speedKMH);
        distance += circumference / 1000;
        consumer.updateDistance(distance);
    }

    public double getCircumference() {
        return circumference;
    }

    public void setCircumference(double circumference) {
        this.circumference = circumference;
    }

    public void setCircumference(String mm_str)
    {
        System.out.println(mm_str);
//        double circ_in_mm = Double.parseDouble(mm_str);
//        this.circumference = circ_in_mm / 1000;
    }
}
