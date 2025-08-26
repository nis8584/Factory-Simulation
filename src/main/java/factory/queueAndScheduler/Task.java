package factory.queueAndScheduler;

import factory.controlledSystem.WorkStation;

import java.util.Collection;

public abstract class Task {

    private Collection<WorkStation> requiredWorkStations;

    public Task(Collection<WorkStation> workStations){
        requiredWorkStations = workStations;
    }

    public Collection<WorkStation> getRequiredWorkStations() {
        return requiredWorkStations;
    }

    public boolean doWorkAt(WorkStation here){
        return requiredWorkStations.remove(here);
    }
    public boolean isTaskDone(){
        return requiredWorkStations.isEmpty();
    }

    @Override
    public String toString() {
        return "Task{" +
                "requiredWorkStations=" + requiredWorkStations +
                '}';
    }
}
