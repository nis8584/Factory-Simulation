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
import org.python.util.PythonInterpreter;
import java.io.StringWriter;

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

        // example for injection and Bus comms
        WorkStation ws = injector.getInstance(WorkStation.class);
        injector.getInstance(EventBus.class).post(new BusMessage(1));

        //example for python running in java
        try (PythonInterpreter pyInterp = new PythonInterpreter()){
            StringWriter output = new StringWriter();
            pyInterp.setOut(output);
            pyInterp.exec("for i in range(5,6):" + " print(i)");
            System.out.println(output.toString().trim());
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
