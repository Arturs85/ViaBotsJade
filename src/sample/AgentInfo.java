package sample;

import jade.core.behaviours.Behaviour;
import jade.wrapper.AgentController;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.List;

public class AgentInfo {
    public String name;
    int timeCreated;
    AgentController ac;
    public List<Behaviour> behaviours;
    public Task currentTask;
    public boolean isS1 = false;
    public TaskType asignedTaskType = TaskType.A;
    public int[] finishedTasksCount = new int[]{0, 0, 0};

    SimpleIntegerProperty finishedTasksA = new SimpleIntegerProperty(this, "finishedTasksA");
    public int getFinishedTasksA() {
        return finishedTasksA.get();
    }
    public SimpleIntegerProperty finishedTasksAProperty() {
        return finishedTasksA;
    }
    public void setFinishedTasksA(int finishedTasksA) {

        this.finishedTasksA.set(finishedTasksA);
    }

    int finishedTasksB;
    int finishedTasksC;

    SimpleIntegerProperty speedA = new SimpleIntegerProperty(this, "speedA");
    public int getSpeedA() {
        return speedA.get();
    }
    public SimpleIntegerProperty speedAProperty() {
        return speedA;
    }
    public void setSpeedA(int speedA) {
        this.speedA.set(speedA);
    }

    SimpleIntegerProperty speedB = new SimpleIntegerProperty(this, "speedB");
    public int getSpeedB() {
        return speedB.get();
    }
    public SimpleIntegerProperty speedBProperty() {
        return speedB;
    }
    public void setSpeedB(int speedB) {
        this.speedB.set(speedB);
    }

    SimpleIntegerProperty speedC = new SimpleIntegerProperty(this, "speedC");
    public int getSpeedC() {
        return speedC.get();
    }
    public SimpleIntegerProperty speedCProperty() {
        return speedC;
    }
    public void setSpeedC(int speedC) {
        this.speedC.set(speedC);
    }


    AgentInfo(String name, int timeCreated, AgentController ac,int[] speed) {
        this.name = name;
        this.timeCreated = timeCreated;
        this.ac = ac;
    }

    String getShortName() {
        int length = name.indexOf("@");
        return name.substring(0, length);
    }

    @Override
    public String toString() {
        //    return super.toString();
        String behString = "";
        if (behaviours != null) {
            for (Behaviour b : behaviours) {
                String beh = (" " + b.getBehaviourName().substring(0, 2));
                if (beh.equalsIgnoreCase(" S1"))
                    beh += "." + asignedTaskType;
                behString += beh;
            }
        }

else
    behString+=" bhLst0";
        return behString;
    }
}