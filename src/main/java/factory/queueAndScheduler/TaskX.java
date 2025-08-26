package factory.queueAndScheduler;

import factory.controlledSystem.WorkStation;

import java.util.Collection;

public class TaskX extends Task{

    public TaskX(Collection<WorkStation> workStations) {
        super(workStations);
    }

    @Override
    public String toString() {
        return "TaskX{"+super.toString() +"}";
    }
}
