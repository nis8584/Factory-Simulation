package factory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import factory.communication.GlobalConstants;
import factory.communication.message.*;
import factory.communication.PostingService;
import factory.controlledSystem.*;
import factory.controllingSystem.WorkStationController;
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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class GraphicsController implements Initializable {
    @FXML
    private ListView<String> logView;
    @FXML
    GridPane gridPane;
    @FXML
    AnchorPane anchorPane;
    @FXML
    VBox controlPanel;

    @Inject
    protected PostingService postingService;

    private final Map<FactoryNode, Rectangle> stations = new HashMap<>();

    private final ObservableList<String> logList = FXCollections.observableArrayList();

    private boolean queueSet = false;

    private boolean layoutSet = false;

    private Queue queue;

    private final LinkedList<Polygon> drawnLines = new LinkedList<>();

    public void onButton(ActionEvent actionEvent){
        postingService.post(new StartWorkSimulation());
    }

    public void log(String s){
        logList.add(s);
        Platform.runLater(()->logView.scrollTo(logList.lastIndexOf(s)));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
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
                    Platform.runLater(()->setText(item));
                }
            }
        });

        // same as constructor:
        logView.setItems(logList);
    }

    public void onLoadQueuePressed(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open textfile with queue data");
        File selectedFile = fileChooser.showOpenDialog(((Node)actionEvent.getSource()).getScene().getWindow());
        if(selectedFile == null) return;
        Queue queue = FileParser.parseFileToQueue(selectedFile);
        postingService.post(new SetQueueMessage(queue));

    }

    public void onLoadLayoutPressed(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open textfile with layout data");
        File selectedFile = fileChooser.showOpenDialog(((Node)actionEvent.getSource()).getScene().getWindow());
        if(selectedFile == null) return;
        LinkedList<FactoryNode> layout = FileParser.parseFileToFactoryNodeSetup(selectedFile);
        ObjectMapper mapper = new ObjectMapper();
        try {
            LinkedList<FactoryNode> list;
            list = mapper.readValue(new File("jsonoutput.json"), new TypeReference<LinkedList<FactoryNode>>() {});
            //mapper.writeValue(new File("jsonoutput.json"), layout);
            System.out.println(list.get(1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        postingService.post(new SetLayoutMessage(layout));
    }


    public void onSetLayout(List<FactoryNode> nodes){
        if(nodes == null) return;
        Node a = gridPane.getChildren().getFirst();
        Platform.runLater(()->{
            gridPane.getChildren().clear();
            gridPane.getChildren().add(a);
            for(Polygon p :drawnLines){
                anchorPane.getChildren().remove(p);
            }
        });
        stations.clear();
        for(FactoryNode node : nodes){
            String labelText = "Node ";
            Color color = Color.RED;
            if(node instanceof WorkStation){
                labelText = "Work ";
                color = Color.BLUE;
            } else if (node instanceof DispenserStation){
                labelText = "Dispense ";
                color = Color.ORANGE;
            } else if (node instanceof DropOffStation){
                labelText = "DropOff ";
                color = Color.BROWN;
            }
            StackPane stack = new StackPane();
            Rectangle rectangle = new Rectangle(40,40, color);
            Label label = new Label(labelText + node.getKey());
            label.setBackground(Background.fill(Color.WHITE));
            stack.getChildren().addAll(rectangle,label);
            int col = Integer.parseInt(String.valueOf(node.getPosition().charAt(0)));
            int row = Integer.parseInt(String.valueOf(node.getPosition().charAt(2)));
            stations.put(node, rectangle);
            Platform.runLater(()-> gridPane.add(stack,col,row));
        }
        gridPane.layout();
        anchorPane.layout();
        Platform.runLater(()->drawAllLines(nodes));
        layoutSet = true;
        checkIfTimeForControlPanel();
    }

    private void drawAllLines(List<FactoryNode> list){
        drawnLines.clear();
        for(FactoryNode node: list){
            for(FactoryNode neighbors: node.getNeighbors().keySet()){
                drawLineBetweenNodes(node,neighbors);
            }

        }
    }

    public void setQueue(Queue queue){
        if(queue == null) return;
        this.queue = queue;
        queueSet = true;
        checkIfTimeForControlPanel();
    }
    private void checkIfTimeForControlPanel(){
        if(queueSet && layoutSet){
            Platform.runLater(this::setControlPanel);
        }
    }

    private void setControlPanel(){
        controlPanel.getChildren().clear();
        List<String> typeOfSteps = new LinkedList<>();
        if(queue.getTasks().isEmpty()) return;
        queue.getTasks().forEach(task -> typeOfSteps.addAll(task.getRequiredWorkStations()));
        for(FactoryNode node : stations.keySet()){
            if(node instanceof WorkStation){
            Platform.runLater(()-> {
                try {
                    controlPanel.getChildren().add(new WorkStationController((WorkStation) node, typeOfSteps.stream().distinct().toList()));
                } catch (NoSuchFieldException e) {
                    PostingService.log("Error when creating WorkStationControllers");
                }
            });
            controlPanel.setSpacing(10);
            controlPanel.setAlignment(Pos.CENTER);
            controlPanel.layout();
            }
        }

    }


    public void animateMovement(Color color, List<FactoryNode> pathNodes){
        if(pathNodes == null) return;
        Point2D screenCoordinates = stations.get(pathNodes.getFirst()).localToScene(0,0);
        Circle circle = new Circle();
        circle.setCenterY(screenCoordinates.getY());
        circle.setCenterX(screenCoordinates.getX());
        circle.setRadius(20);
        circle.setFill(color);

        Path path = new Path();
        MoveTo moveTo = new MoveTo(screenCoordinates.getX(),screenCoordinates.getY());
        path.getElements().add(moveTo);
        for(FactoryNode n : pathNodes){
            Point2D tempCoordinates = stations.get(n).localToScene(0,0);
            path.getElements().add(new LineTo(tempCoordinates.getX(),tempCoordinates.getY()));
        }
        PathTransition pathTransition = new PathTransition();
        pathTransition.setAutoReverse(false);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(1);
        pathTransition.setDuration(Duration.millis(FactoryNode.costPerPath((LinkedList<FactoryNode>) pathNodes)* GlobalConstants.TimeFactor));
        pathTransition.setNode(circle);
        pathTransition.setPath(path);
        circle.setOpacity(100);
        pathTransition.setOnFinished(s -> circle.setOpacity(0));
        Platform.runLater(()->{
            anchorPane.getChildren().add(circle);
            pathTransition.play();
        });
    }

    private void drawLineBetweenNodes(FactoryNode from, FactoryNode to){
        anchorPane.layout();
        gridPane.layout();
        double fromWidth = stations.get(from).getWidth();
        double toWidth = stations.get(to).getWidth();
        Point2D fromCords = stations.get(from).localToScene(0,0);
        Point2D toCords = stations.get(to).localToScene(0,0);
        Point2D fromP1 = new Point2D(fromCords.getX()+fromWidth/2-2, fromCords.getY() +fromWidth/2-2);
        Point2D fromP2 = new Point2D(fromCords.getX()+fromWidth/2+2, fromCords.getY() +fromWidth/2+2);
        Point2D toP2 = new Point2D(toCords.getX()+toWidth/2-2, toCords.getY() + toWidth/2-2);
        Point2D toP1 = new Point2D(toCords.getX()+toWidth/2+2, toCords.getY() + toWidth/2+2);


        double[] points = new double[]{
                fromCords.getX(), fromCords.getY(),
                fromCords.getX()+fromWidth, fromCords.getY()+fromWidth,
                toCords.getX() + toWidth, toCords.getY()+fromWidth,
                toCords.getX(), toCords.getY()

        };
        Polygon line1 = new Polygon(points);

        line1.setFill(Color.SANDYBROWN);
        line1.setOpacity(60);
        Polygon line2 = new Polygon();
        line2.getPoints().addAll(fromP1.getX(), fromP1.getY(),fromP2.getX(),fromP2.getY(), toP1.getX(), toP1.getY(), toP2.getX(), toP2.getY());
        line2.setFill(Color.BLACK);
        line2.setOpacity(60);
        anchorPane.getChildren().addFirst(line2);
        anchorPane.getChildren().addFirst(line1);
        drawnLines.add(line1);
        drawnLines.add(line2);
    }

}