package sample.behaviours;


import sample.TaskType;
import sample.ViaBot;

public class S2aBhvr extends S2Bhvr {
    int order = 0;

    public S2aBhvr(ViaBot owner, int ms) {
        super(owner, ms);
        taskType = TaskType.A;

    }



    @Override
    void updateUI() {
        owner.simulation.controller.textS2aValue.setText(Double.toString(problemValues[taskType.ordinal()]));
        owner.simulation.controller.textS1aSpeed.setText(Double.toString(availableSpeed));
    }



}
