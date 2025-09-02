package factory;

import com.google.inject.Inject;
import factory.communication.LogMessage;
import factory.communication.PostingService;
import factory.communication.SetLayoutMessage;
import factory.communication.SetQueueMessage;
import factory.controlledSystem.Factory;
import factory.controlledSystem.FactoryNode;
import factory.queueAndScheduler.Queue;
import factory.queueAndScheduler.FileParser;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.net.URL;
import java.util.*;

public class GraphicsController implements Initializable {
    @FXML
    private ListView<String> logView;
    @FXML
    GridPane gridPane;
    @FXML
    AnchorPane anchorPane;

    @Inject
    protected PostingService postingService;

    @Inject
    protected EventBus eventBus;

    private Map<FactoryNode, Rectangle> stations = new HashMap<>();

    private ObservableList<String> logList = FXCollections.observableArrayList();

    private Factory factory = new Factory(); // durch dynamisches laden erestzen

    List<PathTransition> pathTransitions = new LinkedList<>();

    public void onButton(ActionEvent actionEvent){
        pathTransitions.forEach(p-> Platform.runLater(()->{
            p.getNode().setOpacity(100);
            p.play();
        }));
    }

    @SuppressWarnings("no usages")
    @Subscribe
    public void onLogMessage(LogMessage logMessage){
        logList.add(logMessage.getMsg());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        eventBus.register(this);
        // setup listview cells to wrap text
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
/*
        //todo move this to new class AnimationCreation or smth
        //make sure that the positions are written correctly!
        FactoryNode from = stations.keySet().stream().filter(s -> s.getPosition().equals("0,2")).findFirst().get();

        FactoryNode to  = stations.keySet().stream().filter(s -> s.getPosition().equals("4,2")).findFirst().get();
        FactoryNode to2 = stations.keySet().stream().filter(s -> s.getPosition().equals("0,4")).findFirst().get();
        FactoryNode to3 = stations.keySet().stream().filter(s -> s.getPosition().equals("2,2")).findFirst().get();
        FactoryNode to4 = stations.keySet().stream().filter(s -> s.getPosition().equals("2,4")).findFirst().get();
        FactoryNode to5 = stations.keySet().stream().filter(s -> s.getPosition().equals("0,0")).findFirst().get();

        HashMap<FactoryNode, Color> endPoints = new HashMap<>();
        endPoints.put(to , Color.GREENYELLOW);
        endPoints.put(to2, Color.PEACHPUFF);
        endPoints.put(to3, Color.RED );
        endPoints.put(to4, Color.ROYALBLUE );
        endPoints.put(to5, Color.BLACK );

        setUpCircleAnimations(from, endPoints);

 */

    }
    //todo move this to new class AnimationCreator or smth
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
            pathTransition.setDuration(Duration.millis(1000 * (path.getElements().size()-1)));
            pathTransition.setNode(circle);
            pathTransition.setPath(path);
            circle.setOpacity(0);
            pathTransition.setOnFinished(s -> circle.setOpacity(0));
            pathTransitions.add(pathTransition);
            anchorPane.getChildren().add(circle);

        }
    }

    public void onLoadQueuePressed(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open textfile with queue data");
        File selectedFile = fileChooser.showOpenDialog(((Node)actionEvent.getSource()).getScene().getWindow());
        Queue queue = FileParser.parseFileToQueue(selectedFile);
        postingService.post(new SetQueueMessage(queue));

    }

    public void onLoadLayoutPressed(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open textfile with layout data");
        File selectedFile = fileChooser.showOpenDialog(((Node)actionEvent.getSource()).getScene().getWindow());
        LinkedList<FactoryNode> layout = FileParser.parseFileToFactoryNodeSetup(selectedFile);
        postingService.post(new SetLayoutMessage(layout));
    }
    @Subscribe
    public void onSetLayout(SetLayoutMessage message){
        if(message.getFactoryNodes() == null) return;
        factory = new Factory();
        factory.setWorkStations(message.getFactoryNodes());
        for(Node node :gridPane.getChildren()){
            if(node instanceof Rectangle){
                Platform.runLater(()-> gridPane.getChildren().remove(node));
            }
        }
        for(FactoryNode node : factory.getWorkStations()){
            Rectangle rectangle = new Rectangle(25,25, Color.RED);
            int col = Integer.parseInt(String.valueOf(node.getPosition().charAt(0)));
            int row = Integer.parseInt(String.valueOf(node.getPosition().charAt(2)));
            stations.put(node, rectangle);
            Platform.runLater(()-> gridPane.add(rectangle,col,row));
        }
        gridPane.layout();
        anchorPane.layout();
    }
}