package factory;

import factory.controlledSystem.Factory;
import factory.controlledSystem.FactoryNode;
import javafx.animation.PathTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class GraphicsController implements Initializable {
    @FXML
    private ListView<String> logView;
    @FXML
    GridPane gridPane;

    private Map<FactoryNode, Rectangle> stations = new HashMap<>();

    private ObservableList<String> logList = FXCollections.observableArrayList();

    private Factory factory = new Factory();
    PathTransition pathTransition;
    PathTransition pathTransition2;

    public void onButton(ActionEvent actionEvent){
        pathTransition.play();
        pathTransition2.play();
    }

    /**
     * method to add text to the log window
     * @param s string to be added
     */
    public void logInGUI(String s){
        logList.add(s);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        // Source: https://stackoverflow.com/questions/53493111/javafx-wrapping-text-in-listview
        logView.setCellFactory(param -> new ListCell<String>(){
            @Override
            protected void updateItem(String item, boolean empty){
                super.updateItem(item,empty);
                if(empty || item == null) {
                    setGraphic(null);
                    setText(null);
                }else{
                    setMinWidth(param.getWidth());
                    setMaxWidth(param.getWidth());
                    setPrefWidth(param.getWidth());
                    setWrapText(true);
                    setText(item);
                }
            }
        });


        // same as constructor:
        logView.setItems(logList);
        List<FactoryNode> list = factory.getFactoryNodes();
        for(FactoryNode node : list){
            Rectangle rectangle = new Rectangle(20,20, Color.RED);
            int col = Integer.parseInt(String.valueOf(node.getPosition().charAt(0)));
            int row = Integer.parseInt(String.valueOf(node.getPosition().charAt(2)));
            stations.put(node, rectangle);
            gridPane.add(rectangle,col,row);
        }
        AnchorPane aP = (AnchorPane) gridPane.getParent();

        gridPane.layout();
        aP.layout();


        FactoryNode from = stations.keySet().stream().filter(s -> s.getPosition().equals("0,2")).findFirst().get();
        FactoryNode to = stations.keySet().stream().filter(s -> s.getPosition().equals("4,2")).findFirst().get();
        FactoryNode to2 = stations.keySet().stream().filter(s -> s.getPosition().equals("0,0")).findFirst().get();
        //todo make this into a loop with x possible ending points
        Point2D screenCoordinates = stations.get(from).localToScene(0,0);
        Circle circle1 = new Circle();
        Circle circle2 = new Circle();
        circle1.setCenterX(screenCoordinates.getX());
        circle1.setCenterY(screenCoordinates.getY());
        circle1.setRadius(20);
        circle1.setFill(Color.GREENYELLOW);
        circle2.setCenterX(screenCoordinates.getX());
        circle2.setCenterY(screenCoordinates.getY());
        circle2.setRadius(20);
        circle2.setFill(Color.PEACHPUFF);

        Path path1 = new Path();
        MoveTo moveTo = new MoveTo(screenCoordinates.getX(),screenCoordinates.getY());
        List<FactoryNode> pathNodes = FactoryNode.findPath(from,to,0);
        path1.getElements().add(moveTo);
        for(FactoryNode n : pathNodes){
            Point2D tempCoordinates = stations.get(n).localToScene(0,0);
            path1.getElements().add(new LineTo(tempCoordinates.getX(),tempCoordinates.getY()));
        }
        Path path2 = new Path();
        MoveTo moveTwo = new MoveTo(screenCoordinates.getX(),screenCoordinates.getY());
        List<FactoryNode> pathNodes2 = FactoryNode.findPath(from,to2,0);
        path2.getElements().add(moveTwo);
        for(FactoryNode n : pathNodes2){
            Point2D tempCoordinates = stations.get(n).localToScene(0,0);
            path2.getElements().add(new LineTo(tempCoordinates.getX(),tempCoordinates.getY()));
        }

        pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(1000 * path1.getElements().size()));
        pathTransition.setNode(circle1);
        pathTransition.setPath(path1);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(100);
        pathTransition.setAutoReverse(false);
        aP.getChildren().add(circle1);
        pathTransition2 = new PathTransition();
        pathTransition2.setDuration(Duration.millis(1000 * path2.getElements().size()));
        pathTransition2.setNode(circle2);
        pathTransition2.setPath(path2);
        pathTransition2.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition2.setCycleCount(100);
        pathTransition2.setAutoReverse(false);
        aP.getChildren().add(circle2);
    }


}
