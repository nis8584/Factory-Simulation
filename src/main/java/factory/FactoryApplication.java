package factory;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FactoryApplication extends Application {

    FXMLLoader loader = new FXMLLoader();

    @Override
    public void start(Stage stage) throws Exception {
        loader.setLocation(getClass().getResource("/fxml/MainView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
