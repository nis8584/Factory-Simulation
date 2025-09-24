package factory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import factory.dependencyInjection.FXMLLoaderProvider;
import factory.dependencyInjection.FactoryModule;
import factory.queueAndScheduler.SchedulerInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FactoryApplication extends Application {


    SchedulerInterface scheduler;

    GUIControllerConnection controllerConnection;

    @Override
    public void start(Stage stage) throws Exception {

        Injector injector = Guice.createInjector(new FactoryModule());
        FXMLLoaderProvider loaderProvider = injector.getInstance(FXMLLoaderProvider.class);
        scheduler = injector.getInstance(SchedulerInterface.class);
        controllerConnection = (GUIControllerConnection) injector.getInstance(GUIControllerConnectionInterface.class);
        LoggingService loggingService = (LoggingService) injector.getInstance(LoggingServiceInterface.class);
        FXMLLoader loader = loaderProvider.get();
        loader.setLocation(getClass().getResource("/fxml/MainView.fxml"));
        Scene scene = new Scene(loader.load());
        GraphicsController graphicsController = loader.getController();
        Platform.runLater(() -> controllerConnection.setController(graphicsController));
        stage.setTitle("Factory Simulation");
        stage.setScene(scene);
        stage.show();

/*
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

 */
    }

    public void stop(){
        System.exit(0);
    }

    public static void main(String[] args){
        launch(args);
    }
}
