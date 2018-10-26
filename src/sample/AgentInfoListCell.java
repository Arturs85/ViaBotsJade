package sample;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;

public class AgentInfoListCell extends ListCell<AgentInfo> {

    @FXML
    public Label agentName;
    @FXML
    public Label agentRoles;
    @FXML
    public ProgressBar agentWorkProgress;
    @FXML
    public HBox hBox;
    @FXML
    public Label textFinishedA;
    @FXML
    public Label textFinishedB;
    @FXML
    public Label textFinishedC;
    @FXML
    public TextField textSpeedA;
    @FXML
    public TextField textSpeedB;
    @FXML
    public TextField textSpeedC;
    @FXML
    public Label textCurTaskIdString;
    @FXML
    public VBox vBoxCurrTask;
@FXML
        public ProgressIndicator progressIndBatterry;
@FXML
   Label labelAgentState;

    FXMLLoader mLLoader;


    @Override
    protected void updateItem(AgentInfo item, boolean empty) {
        // super.updateItem(item, empty);
        if (getItem() != null) { // get old item
            //remove all bidirectional bindings and listeners
            textSpeedA.textProperty().unbindBidirectional(getItem().speedAProperty());
            textSpeedB.textProperty().unbindBidirectional(getItem().speedBProperty());
            textSpeedC.textProperty().unbindBidirectional(getItem().speedCProperty());

            // getItem().speedAProperty().removeListener(textListener);
        }
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);

        } else {

            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource("AgentInfoListCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            textFinishedA.setText(String.valueOf(item.finishedTasksCount[0]));
            textFinishedB.setText(String.valueOf(item.finishedTasksCount[1]));
            textFinishedC.setText(String.valueOf(item.finishedTasksCount[2]));

            textSpeedA.textProperty().bindBidirectional(item.speedAProperty(), speedConverter);//.bindBidirectional(item.finishedTasksA.asString());
            textSpeedB.textProperty().bindBidirectional(item.speedBProperty(), speedConverter);
            textSpeedC.textProperty().bindBidirectional(item.speedCProperty(), speedConverter);

            agentName.setText(item.getShortName());
            agentRoles.setText(item.toString());
            if (item.isS1) {
                vBoxCurrTask.setVisible(true);

                if (item.getCurrentTaskId() != null)
                    textCurTaskIdString.setText("Part: " + item.getCurrentTaskId());
                else
                    textCurTaskIdString.setText("Part: ");
            }
                else
                vBoxCurrTask.setVisible(false);
                //textCurTaskIdString.setText("");
            double p = 0;
            if (item.currentTask != null) {
                p = item.currentTask.progress / 100d;
            }
            agentWorkProgress.setProgress(p);

            progressIndBatterry.setProgress(item.batteryCharge/100d);
            labelAgentState.setText(item.agentState.toString());
            // setText(null);
            setGraphic(hBox);

        }

    }

    StringConverter speedConverter = new StringConverter<Number>() {
        @Override
        public String toString(Number object) {
            return object.toString();
        }

        @Override
        public Number fromString(String string) {
            int res = Integer.parseInt(string);

            return res;
        }
    };

    public void updateSpeedA(ActionEvent actionEvent) {
        System.out.println(actionEvent.getSource());
        System.out.println(((TextField) actionEvent.getSource()).getText());
        System.out.println();
    }
}
