package sample.behaviours;

import jade.core.behaviours.Behaviour;
import sample.AgentInfo;
import sample.Task;
import sample.TaskType;
import sample.ViaBot;

public class S2cBhvr extends S2Bhvr {

    public S2cBhvr(ViaBot owner, int ms) {
        super(owner, ms);
        taskType = TaskType.C;

    }



    @Override
    void updateUI() {
        owner.simulation.controller.textS2cValue.setText(Double.toString(problemValue));
        owner.simulation.controller.textS1cSpeed.setText(Double.toString(availableSpeed));

    }


}

