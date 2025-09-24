package factory.communication.message;

public class LogTimeMessage implements Message{
    private final double timeToCompletion;

    public LogTimeMessage(double timeToCompletion) {
        this.timeToCompletion = timeToCompletion;
    }

    public double getTimeToCompletion() {
        return timeToCompletion;
    }
}
