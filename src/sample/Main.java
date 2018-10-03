package sample;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Viabots sim");
        primaryStage.setScene(new Scene(root, 1100, 675));
        Controller controller = loader.getController();
        Simulation simulation = new Simulation(controller);
        controller.setSimulation(simulation);
        primaryStage.show();


//startJade();
        /*try {
            Process theProcess = Runtime.getRuntime().exec("java jade.Boot -gui  creator:sample.CreatorAgent");
            System.out.println((theProcess.getInputStream()));

        }catch (IOException e){
            e.printStackTrace();
        }*/
    }


    public static void main(String[] args) {
        launch(args);
    }

}

