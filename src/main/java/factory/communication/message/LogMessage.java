package factory.communication.message;

/**
 * Message that sends text logs to the GUI log output.
 *
 * @see factory.GraphicsController
 */
public class LogMessage implements Message {
    private final String msg;

    public LogMessage(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
