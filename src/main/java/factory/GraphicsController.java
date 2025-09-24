package factory;

import com.google.inject.Inject;
import factory.communication.GlobalConstants;
import factory.communication.message.*;
import factory.communication.PostingService;
import factory.controlledSystem.*;
import factory.controllingSystem.WorkStationInfoPanel;
import factory.queueAndScheduler.Queue;
import factory.queueAndScheduler.FileParser;
import factory.queueAndScheduler.Task;
import factory.queueAndScheduler.TaskX;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;

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
    @FXML
    VBox controlPanel;
    @FXML
    Slider concurrencySlider;

    @Inject
    protected PostingService postingService;

    private final Map<FactoryNode, Rectangle> stations = new HashMap<>();

    private final ObservableList<String> logList = FXCollections.observableArrayList();

    private Queue queue;

    private final LinkedList<Line> drawnLines = new LinkedList<>();

    private Map<String, LinkedList<String>> tasksAndSteps;

    public void onButton(ActionEvent actionEvent){
        postingService.post(new DoSchedulingMessage(null));
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

    public void onLoadFactoryAndQueuePressed(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open textfile with factory and queue data");
        File selectedFile = fileChooser.showOpenDialog(((Node)actionEvent.getSource()).getScene().getWindow());
        if(selectedFile == null) return;
        FileParser.parseFactoryAndQueue(selectedFile);
    }

    public void onPrintStats(ActionEvent actionEvent) {
        postingService.post(new PrintResultsMessage());
    }

    public void onConcurrencySliderSet(MouseEvent mouseEvent) {
        postingService.post(new SetConcurrencyMessage((int) concurrencySlider.getValue()));
    }

    private void createTaskButtons(){
        for(String s: tasksAndSteps.keySet()){
            Button button = new Button("Add Task: " + s );
            Task t = new TaskX(tasksAndSteps.get(s));
            button.setOnAction(actionEvent-> postingService.post(new AddTaskToQueueMessage(t)));
            button.setPadding(new Insets(8));
            controlPanel.getChildren().add(button);
        }
    }

    public void setFactoryInfo(List<FactoryNode> nodes, Queue queue, Map<String,LinkedList<String>> tasksAndSteps){
        if(nodes == null || queue == null || tasksAndSteps == null) return;
        controlPanel.getChildren().clear();
        this.queue = queue;
        this.tasksAndSteps = tasksAndSteps;
        Node a = gridPane.getChildren().getFirst();
        Platform.runLater(()->{
            gridPane.getChildren().clear();
            gridPane.getChildren().add(a);
            for(Line p :drawnLines){
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
        createTaskButtons();
        createInfoPanels();
    }
    private void createInfoPanels(){
        for(FactoryNode node: stations.keySet()){
            if(node instanceof WorkStation){
                controlPanel.getChildren().add(new WorkStationInfoPanel((WorkStation) node));
            }
        }
    }

    private void drawAllLines(List<FactoryNode> list){
        drawnLines.clear();
        for(FactoryNode node: list){
            for(FactoryNode neighbors: node.getNeighbors().keySet()){
                drawLineBetweenNodes(node,neighbors);
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
        double nodeWidthOffSet = stations.get(from).getWidth()/2;
        double nodeHeightOffSet = stations.get(from).getHeight()/2;

        Point2D fromCords = stations.get(from).localToScene(0,0);
        Point2D toCords = stations.get(to).localToScene(0,0);

        Line line1 = new Line(fromCords.getX()+nodeWidthOffSet,fromCords.getY()+nodeHeightOffSet,toCords.getX()+nodeWidthOffSet,toCords.getY()+nodeHeightOffSet);
        line1.setStroke(Color.SANDYBROWN);
        line1.setStrokeWidth(30);

        Line line2 = new Line(fromCords.getX()+nodeWidthOffSet,fromCords.getY()+nodeHeightOffSet,toCords.getX()+nodeWidthOffSet,toCords.getY()+nodeHeightOffSet);
        line2.setStroke(Color.BLACK);
        line2.setStrokeWidth(5);

        anchorPane.getChildren().addFirst(line2);
        anchorPane.getChildren().addFirst(line1);
        drawnLines.add(line1);
        drawnLines.add(line2);
    }

}