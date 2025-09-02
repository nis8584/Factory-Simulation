package factory.communication;

import org.greenrobot.eventbus.EventBus;

public class PostingService {

    private final EventBus eventBus;

    public PostingService(EventBus eventBus) {
        this.eventBus = eventBus;
    }
    
    public void post(Message message){
        eventBus.post(message);
    }
}
