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

    @Subscribe
    public void onLogTimeMessage(LogTimeMessage message){
        timesToCompletion.add(message.getTimeToCompletion());
        PostingService.log("task finished with runtime: " + message.getTimeToCompletion());
    }

    @Subscribe
    public void onRunTimeMessage(RunTimeMessage message){
        this.runTime = (double) message.getRunTime();
    }

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
