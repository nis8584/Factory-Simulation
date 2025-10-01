package factory.controllingSystem;

import factory.communication.message.TaskStepStatusMessage;
import factory.queueAndScheduler.Task;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class QueueInfoPanelElement extends Pane {

    Map<String, Label> stepStatuses = new HashMap<>();

    private final int id;

    public QueueInfoPanelElement(Task task) {
        EventBus.getDefault().register(this);
        id = task.getId();
        HBox hBox1 = new HBox();
        hBox1.setAlignment(Pos.TOP_CENTER);
        Label nameLabel = new Label("Task " + task.getId() +": " + task.getName());
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        hBox1.getChildren().addAll(nameLabel, vBox);
        vBox.getChildren().add(new Label("Steps:"));
        for(LinkedList<String> list: task.getRequiredWorkStations()){
            for(String s: list){
                Label statuslabel = new Label("not started");
                stepStatuses.put(s,statuslabel);
                HBox hBox = new HBox(new Label(s + ": "), statuslabel);
                hBox.setAlignment(Pos.CENTER);
                vBox.getChildren().add(hBox);
            }
        }
        this.getChildren().add(hBox1);
    }

    @Subscribe
    public void onTaskStepStatusMessage(TaskStepStatusMessage message){
        if(message.getId() == id && stepStatuses.containsKey(message.getStep())){
            Platform.runLater(()->stepStatuses.get(message.getStep()).setText(message.getStatus()));
        }
    }

}
