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
    @FXML
    AnchorPane anchorPane;

    private Map<FactoryNode, Rectangle> stations = new HashMap<>();

    private ObservableList<String> logList = FXCollections.observableArrayList();

    private Factory factory = new Factory();

    List<PathTransition> pathTransitions = new LinkedList<>();

    public void onButton(ActionEvent actionEvent){
        pathTransitions.forEach(p->{
            p.getNode().setOpacity(100);
            p.play();
        });
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
        logView.setCellFactory(param -> new ListCell<>(){
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
            Rectangle rectangle = new Rectangle(25,25, Color.RED);
            int col = Integer.parseInt(String.valueOf(node.getPosition().charAt(0)));
            int row = Integer.parseInt(String.valueOf(node.getPosition().charAt(2)));
            stations.put(node, rectangle);
            gridPane.add(rectangle,col,row);
        }

        gridPane.layout();
        anchorPane.layout();

        //make sure that the positions are written correctly!
        FactoryNode from = stations.keySet().stream().filter(s -> s.getPosition().equals("0,2")).findFirst().get();
        FactoryNode to = stations.keySet().stream().filter(s -> s.getPosition().equals("4,2")).findFirst().get();
        FactoryNode to2 = stations.keySet().stream().filter(s -> s.getPosition().equals("0,0")).findFirst().get();

        HashMap<FactoryNode, Color> endPoints = new HashMap<>();
        endPoints.put(to,Color.GREENYELLOW);
        endPoints.put(to2, Color.PEACHPUFF);
        setUpCircleAnimations(from, endPoints);



    }

    public void setUpCircleAnimations(FactoryNode from, HashMap<FactoryNode, Color> endPoints){
        for(FactoryNode node : endPoints.keySet()){
            Point2D screenCoordinates = stations.get(from).localToScene(0,0);
            Circle circle = new Circle();
            circle.setCenterY(screenCoordinates.getY());
            circle.setCenterX(screenCoordinates.getX());
            circle.setRadius(20);
            circle.setFill(endPoints.get(node));

            Path path = new Path();
            MoveTo moveTo = new MoveTo(screenCoordinates.getX(),screenCoordinates.getY());
            LinkedList<FactoryNode> pathNodes = FactoryNode.findPath(from, node, 0);
            path.getElements().add(moveTo);
            assert pathNodes != null;
            for(FactoryNode n : pathNodes){
                Point2D tempCoordinates = stations.get(n).localToScene(0,0);
                path.getElements().add(new LineTo(tempCoordinates.getX(),tempCoordinates.getY()));
            }
            PathTransition pathTransition = new PathTransition();
            pathTransition.setAutoReverse(false);
            pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
            pathTransition.setCycleCount(1);
            pathTransition.setDuration(Duration.millis(1000 * path.getElements().size()));
            pathTransition.setNode(circle);
            pathTransition.setPath(path);
            circle.setOpacity(0);
            pathTransition.setOnFinished(s -> circle.setOpacity(0));
            pathTransitions.add(pathTransition);
            anchorPane.getChildren().add(circle);

        }
    }


}
