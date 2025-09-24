package factory.communication.message;

public class StatusMessage implements Message{

    private final String status;

    private final char key;

    public StatusMessage(String status, char key) {
        this.status = status;
        this.key = key;
    }

    public String getStatus() {
        return status;
    }

    public char getKey() {
        return key;
    }
}
