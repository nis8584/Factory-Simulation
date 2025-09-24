package factory.queueAndScheduler;

/**
 * enum of different scheduling strategies that the scheduler aims to fulfill
 */
public enum ScheduleStrategies {
    FirstInFirstOut,
    ShortestProcessingTime,
    EarliestDueDate,
    OperationalDueDate
}
