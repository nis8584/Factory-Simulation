package factory.controllingSystem;

import factory.communication.message.StatusMessage;
import factory.controlledSystem.WorkStation;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Class that implements a GUI element that shows information about a given WorkStation
 * @see WorkStation
 * @see StatusMessage
 */
public class WorkStationInfoPanel extends Pane {

    Label infoLabel = new Label("idle");

    String defaultText;

    private final char key;

    public WorkStationInfoPanel(WorkStation station) {
        this.key = station.getKey();
        EventBus.getDefault().register(this);
        defaultText = "Workstation " + station.getKey() + " status: ";
        HBox hBox = new HBox();
        hBox.getChildren().add(new Label(defaultText));
        hBox.getChildren().add(infoLabel);
        getChildren().add(hBox);
    }

    @Subscribe
    public void onStatusMessage(StatusMessage message){
        if(message.getKey() == key){
            Platform.runLater(()->infoLabel.setText(message.getStatus()));
        }
    }



    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }
}
