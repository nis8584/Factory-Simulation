package factory.communication.message;

/**
 * Message that is sent by WorkStations that contain their current status. Is displayed by WorkStationInfoPanel
 *
 * @see factory.controlledSystem.WorkStation
 * @see factory.controllingSystem.WorkStationInfoPanel
 */
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
