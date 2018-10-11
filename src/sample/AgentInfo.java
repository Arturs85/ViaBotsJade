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

    public void setBehaviours(List<Behaviour> behaviours) {
            this.behaviours = behaviours;
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

    public int getSpeed(TaskType taskType){
        switch (taskType.ordinal()){
            case 0:
                return getSpeedA();
            case 1:
                return getSpeedB();
            case 2:
                return getSpeedC();
        }
     return 0;
    }
    SimpleIntegerProperty speedS2 = new SimpleIntegerProperty(this,"speedS2");
    public int getSpeedS2(){return speedS2.get();}
    public SimpleIntegerProperty speedS2Property() {
        return speedS2;
    }
    public void setSpeedS2(int speedS2) {
        this.speedS2.set(speedS2);
    }

    SimpleIntegerProperty speedS3 = new SimpleIntegerProperty(this,"speedS3");
    public int getSpeedS3(){return speedS3.get();}
    public SimpleIntegerProperty speedS3Property() {
        return speedS3;
    }
    public void setSpeedS3(int speedS3) {
        this.speedS3.set(speedS3);
    }

    SimpleIntegerProperty speedS4 = new SimpleIntegerProperty(this,"speedS4");
    public int getSpeedS4(){return speedS4.get();}
    public SimpleIntegerProperty speedS4Property() {
        return speedS4;
    }
    public void setSpeedS4(int speedS4) {
        this.speedS4.set(speedS4);
    }


    AgentInfo(String name, int timeCreated, AgentController ac,int[] speed) {
        this.name = name;
        this.timeCreated = timeCreated;
        this.ac = ac;
        setSpeedA(speed[0]);
        setSpeedB(speed[1]);
        setSpeedC(speed[2]);
        setSpeedS2(speed[3]);
        setSpeedS3(speed[4]);
        setSpeedS4(speed[5]);
    }

    String getShortName() {
        int length = name.indexOf("@");
        return name.substring(0, length);
    }
String getCurrentTaskId(){
 if(currentTask!=null)
        return String.valueOf(currentTask.id);
else
    return null;
    }

    @Override
    public String toString() {
        //    return super.toString();
        String behString = "";
        if (behaviours != null) {
            for (Behaviour b : behaviours) {
                String beh = (" " + b.getBehaviourName().substring(0, 2));
                if(beh.equalsIgnoreCase(" S2"))
                    beh+="." + b.getBehaviourName().substring(2, 3);
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