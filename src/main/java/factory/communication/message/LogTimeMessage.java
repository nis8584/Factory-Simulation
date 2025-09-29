package factory.communication.message;

/**
 * Message that contains the total runtime of an individual task
 */
public class LogTimeMessage implements Message{
    private final double timeToCompletion;

    public LogTimeMessage(double timeToCompletion) {
        this.timeToCompletion = timeToCompletion;
    }

    public double getTimeToCompletion() {
        return timeToCompletion;
    }
}
