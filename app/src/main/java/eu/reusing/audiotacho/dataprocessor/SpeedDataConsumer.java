package eu.reusing.audiotacho.dataprocessor;


public interface SpeedDataConsumer {
    void updateSpeedData(double speed);
    void measuringStarted();
    void measuringStopped();
}
