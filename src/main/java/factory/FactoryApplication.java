package factory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import factory.controlledSystem.WorkStation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.BusMessage;
import org.greenrobot.eventbus.EventBus;

public class FactoryApplication extends Application {

    FXMLLoader loader = new FXMLLoader();

    @Override
    public void start(Stage stage) throws Exception {
        Injector injector = Guice.createInjector(new FactoryModule());

        loader.setLocation(getClass().getResource("/fxml/MainView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("My JavaFX Application");
        stage.setScene(scene);
        stage.show();

        WorkStation ws = injector.getInstance(WorkStation.class);
        injector.getInstance(EventBus.class).post(new BusMessage(1));
    }

    public static void main(String[] args){
        launch(args);
    }
}
