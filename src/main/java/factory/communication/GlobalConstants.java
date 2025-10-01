package factory.communication;

/**
 * Class that contains constants that are used throughout the whole software.
 */
public class GlobalConstants {
    /**
     * parameter that sets the length of a system-wide timeunit in real life milliseconds.
     */
    public static final Integer TimeFactor = 1500;
    private static int taskId = 0;

    public static int getTaskId(){
        return taskId++;
    }
}
