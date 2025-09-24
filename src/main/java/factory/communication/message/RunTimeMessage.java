package factory.communication.message;

public class RunTimeMessage implements Message{
    private final long runTime;

    public RunTimeMessage(long runTime) {
        this.runTime = runTime;
    }

    public long getRunTime() {
        return runTime;
    }
}
