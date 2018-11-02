package sample.behaviours;

import sample.TaskType;
import sample.ViaBot;

public class S2bBhvr extends S2Bhvr {
int order=1;
    public S2bBhvr(ViaBot owner, int ms) {
        super(owner, ms);
        taskType = TaskType.B;

    }



    @Override
    void updateUI() {
        owner.simulation.controller.textS2bValue.setText(Double.toString(problemValues[taskType.ordinal()]));
        owner.simulation.controller.textS1bSpeed.setText(Double.toString(availableSpeed));
    }

}
