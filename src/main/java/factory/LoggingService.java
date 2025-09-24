package factory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import factory.communication.PostingService;
import factory.communication.message.LogTimeMessage;
import factory.communication.message.PrintResultsMessage;
import factory.communication.message.RunTimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

/**
 * Class that collects runtimes of individual tasks and prints them out after the simulation finishes
 */
@Singleton
public class LoggingService implements LoggingServiceInterface{

    private static final Logger LOG = LogManager.getLogger(LoggingService.class);

    private EventBus eventBus;

    private double runTime = -1;

    private final LinkedList<Double> timesToCompletion = new LinkedList<>();

    @Inject
    public LoggingService(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    /**
     * Method reacting to a LogTimeMessage being sent by a DropOffStation after a task is completed. Adds the completion time the log.
     * @param message incoming message
     */
    @Subscribe
    public void onLogTimeMessage(LogTimeMessage message){
        timesToCompletion.add(message.getTimeToCompletion());
        PostingService.log("task finished with runtime: " + message.getTimeToCompletion());
    }

    /**
     * Method reacting to a RunTimeMessage coming from the Scheduler when the last Task is removed from the queue
     * @param message incoming message
     */
    @Subscribe
    public void onRunTimeMessage(RunTimeMessage message){
        this.runTime = (double) message.getRunTime();
    }

    /**
     * Method reacting to a PrintResultMessage coming from the GUI to print the task completion times, total runtime and avg task time
     * @param message incoming message
     */
    @Subscribe
    public void onPrintResultsMessage(PrintResultsMessage message){
        StringBuilder result = new StringBuilder("Total runtime: " + runTime + '\n');
        int i = 0;
        for(Double time: timesToCompletion){
            i++;
            result.append("Task ").append(i).append(": ").append(time).append('\n');
        }
        double sumOfTasks = timesToCompletion.stream().reduce(0.0, Double::sum);
        result.append("Average time per task: ").append(sumOfTasks / i);
        try(PrintWriter out = new PrintWriter("results.txt")){
            out.print(result);
        } catch (IOException e){
            LOG.error("Something went wrong with writing to results.txt");
            throw new RuntimeException(e);
        }
        timesToCompletion.clear();
    }
}
