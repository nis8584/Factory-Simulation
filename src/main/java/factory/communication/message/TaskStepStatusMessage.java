package factory.communication.message;

public class TaskStepStatusMessage implements Message{

    private final int id;

    private final String step;

    private final String status;

    public TaskStepStatusMessage(int id, String step, String status) {
        this.id = id;
        this.step = step;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getStep() {
        return step;
    }

    public String getStatus() {
        return status;
    }
}
