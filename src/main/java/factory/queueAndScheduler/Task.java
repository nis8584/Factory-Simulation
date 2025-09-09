package factory.queueAndScheduler;

import java.util.LinkedList;
import java.util.Objects;

public abstract class Task {

    private final LinkedList<String> requiredTasks;

    private  long startTime;

    protected Task(LinkedList<String> requiredTasks) {
        this.requiredTasks = requiredTasks;
    }

    public void setStartTime(){
        startTime = System.currentTimeMillis();
    }

    public double getRunningTime(){
        long endTime = System.currentTimeMillis();
        return (double) (endTime - startTime) / 1000;
    }

    public LinkedList<String> getRequiredWorkStations() {
        return requiredTasks;
    }

    public boolean doWorkAt(String workDone){
        return requiredTasks.remove(workDone);
    }
    public boolean isTaskDone(){
        return requiredTasks.isEmpty();
    }

    @Override
    public String toString() {
        return "Task{" +
                "requiredWorkStations=" + requiredTasks +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(requiredTasks, task.requiredTasks);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(requiredTasks);
    }
}
