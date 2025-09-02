package factory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import factory.controlledSystem.Factory;
import factory.dependencyInjection.FXMLLoaderProvider;
import factory.dependencyInjection.FactoryModule;
import factory.queueAndScheduler.Scheduler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.BusMessage;
import org.greenrobot.eventbus.EventBus;

public class FactoryApplication extends Application {

    FXMLLoader loader;

    Factory factory;
    Scheduler scheduler;

    @Override
    public void start(Stage stage) throws Exception {

        Injector injector = Guice.createInjector(new FactoryModule());
        FXMLLoaderProvider loaderProvider = injector.getInstance(FXMLLoaderProvider.class);
        factory = injector.getInstance(Factory.class);
        scheduler = injector.getInstance(Scheduler.class);
        loader = loaderProvider.get();
        loader.setLocation(getClass().getResource("/fxml/MainView.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("Factory Simulation");
        stage.setScene(scene);
        stage.show();
        injector.getInstance(EventBus.class).post(new BusMessage(1));
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

    public static void main(String[] args){
        launch(args);
    }
}
