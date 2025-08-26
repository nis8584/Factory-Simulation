package factory.queueAndScheduler;

import factory.controlledSystem.WorkStation;

import java.util.Collection;

public class TaskY extends Task{

    public TaskY(Collection<WorkStation> workStations) {
        super(workStations);
    }

    @Override
    public String toString() {
        return "TaskY{" + super.toString() + "}";
    }
}
