package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;


public class Controller {

    @FXML
     Label textBeltStoppedPercentage;
    @FXML
    ListView finishedTasksListView;
    @FXML
    Label textBeltStoppedTime;
    @FXML
    Label textFinishedTasks;
    @FXML
    Label textSimTime;

    @FXML
    ListView newTasksListView;
    @FXML
    AnchorPane workingAgentsScrollPane;
    @FXML
    ListView<AgentInfo> workingAgentsListView;
    @FXML
    Button stopSimulationButton;
    @FXML
    Button startSimulationButton;
    @FXML
    Button newAgentButton;

    @FXML
    Text textField;
    private Simulation simulation;
     GUIAgent guiAgent;

    void setGUIAgent(GUIAgent guiagent) {
        this.guiAgent = guiagent;

    }

    void setSimulation(Simulation simulation) {
        this.simulation = simulation;

        workingAgentsListView.setCellFactory(new Callback<ListView<AgentInfo>, ListCell<AgentInfo>>() {
            @Override
            public ListCell<AgentInfo> call(ListView<AgentInfo> param) {

                return new AgentInfoListCell();
            }
        });

    }
// ObservableList<Agent> agents = FXCollections.observableArrayList(
    //       new Agent(),new Agent());


    @FXML
    void newAgentButtonHandler(ActionEvent actionEvent) {
        //  textField.setText(actionEvent.getSource().toString());
        //  simulation.addNewAgentToList();
        //   System.out.println("container: " + jade.core.AgentContainer.MAIN_CONTAINER_NAME);
        simulation.createAgent();
    }

    @FXML
    void startSimulationButtonHandler(ActionEvent actionEvent) {
        //    textField.setText(actionEvent.getSource().toString());
        simulation.timeline.play();
        simulation.isRunning=true;
        if(guiAgent!=null){
            guiAgent.sendMessageUI(true);

        }

        //jade.core.Agent agent = new SampleAgent();
        //      System.out.println( agent.getDefaultDF());
    }

    @FXML
    void StopSimulationbuttonHandler(ActionEvent actionEvent) {
        //  textField.setText(actionEvent.getSource().toString());
        simulation.timeline.stop();
        simulation.isRunning = false;
        if(guiAgent!=null){
            guiAgent.sendMessageUI(false);

        }
    }

    void update(int time, int finishedTasks) {
        workingAgentsListView.refresh();
        newTasksListView.refresh();
        finishedTasksListView.refresh();
        textSimTime.setText(time + " s");
        textBeltStoppedTime.setText(simulation.beltStopedTime + " s");
        textFinishedTasks.setText(Integer.toString(finishedTasks));
textBeltStoppedPercentage.setText(String.format("%.2f", simulation.beltStopedFactor)+ " %");
    }

    // @Override
    //public void initialize(URL location, ResourceBundle resources) {
    // }

}