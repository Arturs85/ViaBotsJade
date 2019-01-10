package sample.messageObjects;

import java.io.Serializable;

public class GuiAgentMessageToOther implements Serializable {

public boolean isSimulationRunning;
public int agentSpeedFactor;
public boolean resetNow;
public GuiAgentMessageToOther(boolean isSimulationRunning,int agentSpeedFactor,boolean resetNow){

    this.isSimulationRunning = isSimulationRunning;
    this.agentSpeedFactor = agentSpeedFactor;
    this.resetNow =resetNow;

}

}
