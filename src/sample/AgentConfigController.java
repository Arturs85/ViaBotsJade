package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

public class AgentConfigController {

    public Button cancelCreateAgentButton;
    public Button createAgentButton;

    public TextField textSpeedA;
    public TextField textSpeedB;
    public TextField textSpeedC;
    public TextField textSpeedS2;
    public TextField textSpeedS3;
    public TextField textSpeedS4;
    int speed[];
    Simulation simulation;
    @FXML
    public ChoiceBox agentBehaviourChoiseBox;



    void setSimulation(Simulation simulation) {
        this.simulation = simulation;


            agentBehaviourChoiseBox.setItems(Simulation.roleList);
   agentBehaviourChoiseBox.setValue(Simulation.roleList.get(0));
    speed=new int[6];
    }

    public void cancelCreateAgentButtonHandler(ActionEvent actionEvent) {

    }

    public void createAgentButtonHandler(ActionEvent actionEvent) {
        speed=new int[]{Integer.parseInt(textSpeedA.getText()),
                Integer.parseInt(textSpeedB.getText()),
                Integer.parseInt(textSpeedC.getText()),
                Integer.parseInt(textSpeedS2.getText()),
                Integer.parseInt(textSpeedS3.getText()),
                Integer.parseInt(textSpeedS4.getText())};

simulation.createAgent((String) (agentBehaviourChoiseBox.getValue()),speed);
    }
}
