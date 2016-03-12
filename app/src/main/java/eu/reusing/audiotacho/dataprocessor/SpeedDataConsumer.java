package eu.reusing.audiotacho.dataprocessor;


public interface SpeedDataConsumer {
    void updateSpeedData(double speed);
    void updateDistance(double distance);
    void measuringStarted();
    void measuringStopped();
}
