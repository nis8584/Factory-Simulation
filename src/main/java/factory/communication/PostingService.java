package factory.communication;

import com.google.inject.Inject;
import factory.communication.message.LogMessage;
import factory.communication.message.Message;
import org.greenrobot.eventbus.EventBus;

public class PostingService implements PostingServiceInterface{

    private final EventBus eventBus;

    @Inject
    public PostingService(EventBus eventBus) {
        this.eventBus = eventBus;
    }
    
    public void post(Message message){
        eventBus.post(message);
    }

    public static void log(String string){
        EventBus.getDefault().post(new LogMessage(string));
    }

}
