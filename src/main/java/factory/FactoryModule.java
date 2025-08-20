package factory;

import com.google.inject.AbstractModule;
import org.greenrobot.eventbus.EventBus;

public class FactoryModule extends AbstractModule {
    final EventBus eventBus = EventBus.getDefault();

    @Override
    protected void configure(){
        bind(EventBus.class).toInstance(eventBus);
    }
}
