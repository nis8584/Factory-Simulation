package factory.communication.message;

public class LogMessage implements Message {
    private final String msg;

    public LogMessage(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
