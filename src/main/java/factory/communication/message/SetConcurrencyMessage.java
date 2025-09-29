package factory.communication.message;

/**
 * Message that indicates a new input of the concurrency slider in the GUI.
 * Changes the limit of current running tasks in the scheduler.
 *
 * @see factory.GraphicsController
 * @see factory.queueAndScheduler.Scheduler
 */
public class SetConcurrencyMessage implements Message{
    private final int count;

    public SetConcurrencyMessage(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
