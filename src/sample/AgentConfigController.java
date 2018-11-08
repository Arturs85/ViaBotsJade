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
    public TextField textPwrA;
    public TextField textPwrB;
    public TextField textPwrC;
    public TextField textPwrS2;
    public TextField textPwrS3;
    public TextField textPwrS4;
    int speed[];
    Simulation simulation;
    @FXML
    public ChoiceBox agentBehaviourChoiseBox;


    public void initialize(){

        textSpeedA.setText(Integer.toString(ViaBot.defSpeed[0]));
        textSpeedB.setText(Integer.toString(ViaBot.defSpeed[1]));
        textSpeedC.setText(Integer.toString(ViaBot.defSpeed[2]));
        textSpeedS2.setText(Integer.toString(ViaBot.defSpeed[3]));
        textSpeedS3.setText(Integer.toString(ViaBot.defSpeed[4]));
        textSpeedS4.setText(Integer.toString(ViaBot.defSpeed[5]));

        textPwrA.setText(Integer.toString(ViaBot.defEnergyCons[0]));
        textPwrB.setText(Integer.toString(ViaBot.defEnergyCons[1]));
        textPwrC.setText(Integer.toString(ViaBot.defEnergyCons[2]));
        textPwrS2.setText(Integer.toString(ViaBot.defEnergyCons[3]));
        textPwrS3.setText(Integer.toString(ViaBot.defEnergyCons[4]));
        textPwrS4.setText(Integer.toString(ViaBot.defEnergyCons[5]));

    }

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

        int [] powerCons = new int[]{Integer.parseInt(textPwrA.getText()),
                Integer.parseInt(textPwrB.getText()),
                Integer.parseInt(textPwrC.getText()),
                Integer.parseInt(textPwrS2.getText()),
                Integer.parseInt(textPwrS3.getText()),
                Integer.parseInt(textPwrS4.getText())};


                simulation.createAgent((String) (agentBehaviourChoiseBox.getValue()),speed,powerCons);
    }
}
