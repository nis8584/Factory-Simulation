package factory.controllingSystem;

import factory.controlledSystem.WorkStation;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class WorkStationController extends Pane {

    ChoiceBox<String> choiceBox = new ChoiceBox<>();

    public WorkStationController(WorkStation station, List<String> options) throws NoSuchFieldException {
        if(options.isEmpty()){
            throw new NoSuchFieldException("Specify a queue before attempting to create WorkStationCont");
        }

        Label label = new Label();
        label.setText("WorkStation " + station.getKey() + ": ");
        Rectangle rectangle = new Rectangle(150, 80, Color.AQUA);
        StackPane stackPane = new StackPane();
        HBox hBox1 = new HBox();
        HBox hBox2 = new HBox();
        VBox vBox = new VBox();
        stackPane.getChildren().addAll(rectangle, vBox);
        hBox1.getChildren().add(label);
        hBox1.setAlignment(Pos.CENTER);
        hBox2.setAlignment(Pos.CENTER);
        choiceBox.setItems(FXCollections.observableList(options));
        choiceBox.setOnAction( event -> station.setTypeOfWork(choiceBox.getValue()));
        choiceBox.setValue(options.getFirst());
        hBox1.getChildren().add(choiceBox);
        Slider slider = new Slider(1,10,1);
        slider.setBlockIncrement(1);
        slider.setOnMouseReleased( a -> station.setProcessingCost((int) slider.getValue()));
        slider.setValue(1);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.showTickLabelsProperty().set(true);
        slider.showTickMarksProperty().set(true);
        slider.snapToTicksProperty().set(true);
        getChildren().add(stackPane);
        hBox2.getChildren().add(slider);
        vBox.getChildren().addAll(hBox1,hBox2);
    }


    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }
}
