package factory.dependencyInjection;

import com.google.inject.AbstractModule;
import factory.communication.PostingService;
import factory.controlledSystem.Factory;
import factory.queueAndScheduler.Scheduler;
import javafx.fxml.FXMLLoader;
import org.greenrobot.eventbus.EventBus;

public class FactoryModule extends AbstractModule {
    final EventBus eventBus = EventBus.getDefault();
    final Factory factory = new Factory();
    final Scheduler scheduler = new Scheduler(eventBus);
    final PostingService postingService = new PostingService(eventBus);

    @Override
    protected void configure(){
        bind(FXMLLoader.class).toProvider(FXMLLoaderProvider.class);
        bind(EventBus.class).toInstance(eventBus);
        bind(Factory.class).toInstance(factory);
        bind(Scheduler.class).toInstance(scheduler);
        bind(PostingService.class).toInstance(postingService);
    }
}
