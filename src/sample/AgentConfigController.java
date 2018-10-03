package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

public class AgentConfigController {

    public Button cancelCreateAgentButton;
    public Button createAgentButton;
    Simulation simulation;
    @FXML
    public ChoiceBox agentBehaviourChoiseBox;



    void setSimulation(Simulation simulation) {
        this.simulation = simulation;


            agentBehaviourChoiseBox.setItems(Simulation.roleList);
   agentBehaviourChoiseBox.setValue(Simulation.roleList.get(0));
    }

    public void cancelCreateAgentButtonHandler(ActionEvent actionEvent) {

    }

    public void createAgentButtonHandler(ActionEvent actionEvent) {
simulation.createAgent((String) (agentBehaviourChoiseBox.getValue()));
    }
}
