package sample.messageObjects;

import java.io.Serializable;

public class GuiAgentMessageToOther implements Serializable {

public boolean isSimulationRunning;
public int agentSpeedFactor;

public GuiAgentMessageToOther(boolean isSimulationRunning,int agentSpeedFactor){

    this.isSimulationRunning = isSimulationRunning;
    this.agentSpeedFactor = agentSpeedFactor;


}

}
