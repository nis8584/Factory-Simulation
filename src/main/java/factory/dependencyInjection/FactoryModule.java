package factory.dependencyInjection;

import com.google.inject.AbstractModule;
import factory.*;
import factory.communication.PostingService;
import factory.communication.PostingServiceInterface;
import factory.controlledSystem.Factory;
import factory.controlledSystem.FactoryInterface;
import factory.queueAndScheduler.Scheduler;
import factory.queueAndScheduler.SchedulerInterface;
import javafx.fxml.FXMLLoader;
import org.greenrobot.eventbus.EventBus;

/**
 * Module that provides dependency injection
 */
public class FactoryModule extends AbstractModule {
    final EventBus eventBus = EventBus.getDefault();

    @Override
    protected void configure(){
        bind(FXMLLoader.class).toProvider(FXMLLoaderProvider.class);
        bind(EventBus.class).toInstance(eventBus);
        bind(FactoryInterface.class).to(Factory.class);
        bind(SchedulerInterface.class).to(Scheduler.class);
        bind(PostingServiceInterface.class).to(PostingService.class);
        bind(LoggingServiceInterface.class).to(LoggingService.class);
        bind(GUIControllerConnectionInterface.class).to(GUIControllerConnection.class);
        bind(TimeServiceInterface.class).to(TimeService.class);
    }
}
