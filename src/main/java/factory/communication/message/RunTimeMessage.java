package factory.communication.message;

/**
 * Message that sends the total runtime of the simulation to the LoggingService.
 * @see factory.LoggingService
 */
public class RunTimeMessage implements Message{
    private final long runTime;

    public RunTimeMessage(long runTime) {
        this.runTime = runTime;
    }

    public long getRunTime() {
        return runTime;
    }
}
