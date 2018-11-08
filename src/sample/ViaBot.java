package sample;

import jade.core.AID;
import jade.core.Agent;
import jade.core.BehaviourID;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.core.messaging.TopicManagementHelper;
import javafx.application.Platform;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sample.behaviours.*;

public class ViaBot extends Agent {

    public static int count = 1;
    public static int totalFinishedTasks = 0;

    public static int[] defSpeed = {23, 14, 19, 1, 1, 1};//A,B,C
    public static int[] defEnergyCons = {2, 3, 4, 1, 1, 1};

    public int[] speed = {23, 14, 19, 1, 1, 1};//A,B,C
    public int[] finishedTasksCount = {0, 0, 0};//A,B,C
    public int[] energyCons = {2, 3, 4, 1, 1, 1};
    int costPlaning = 10;

    public TaskType assignedTaskType = TaskType.A;

    public Task currentTask;
    public boolean isWorking = false;
    public List<Task> taskList;
    public List<AgentInfo> agentsList;
    public List<Task> finishedTasksList;
    boolean isPlanner = false;
    public List<Behaviour> mBehaviours = new ArrayList<>(5);
    public boolean simulationRunning = false;
    public Simulation simulation;
    public AgentState agentState = AgentState.IDLE;
    public Battery battery;
    public int speedFactor = 1;

    protected void setup() {
        battery = new Battery();
        Object[] args = getArguments();

        this.taskList = (ObservableList) args[1];
        this.agentsList = (ObservableList) args[2];
        this.finishedTasksList = (ObservableList) args[3];
        mAddBehaviour((String) args[4]);
        this.simulation = (Simulation) args[5];
        this.speed = (int[]) args[6];
        this.energyCons = (int[]) args[7];

    }

    public void mAddBehaviour(String behaviourString) {
        Behaviour behaviour;
        if (behaviourString.contains("S1"))
            behaviour = new S1Bhvr(this, 1000);

        else if (behaviourString.contains("S2a")) {
            behaviour = new S2aBhvr(this, 1000);
            Simulation.managingRolesFilled[0] = true;
        } else if (behaviourString.contains("S2b")) {
            behaviour = new S2bBhvr(this, 1000);
            Simulation.managingRolesFilled[1] = true;
        } else if (behaviourString.contains("S2c")) {
            behaviour = new S2cBhvr(this, 1000);
            Simulation.managingRolesFilled[2] = true;
        } else if (behaviourString.contains("S3")) {
            behaviour = new S3Bhvr(this, 1000);
            Simulation.managingRolesFilled[3] = true;
        } else if (behaviourString.contains("S4")) {
            behaviour = new S4Bhvr(this, 1000);
            Simulation.managingRolesFilled[4] = true;
        } else {
            System.out.println("Unable to recognize string: " + behaviourString + ",to create bahaviour");
            return;
        }

        mAddBehaviour(behaviour);


    }

    // veido vietēju lomu sarakstu un pierakstās uz ziņojumiem
    void mAddBehaviour(Behaviour behaviour) {
        TopicManagementHelper topicHelper = null;
        //reģistrējas saņemt lomai atbilstošos ziņojumus
        try {
            topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            topicHelper.register(topicHelper.createTopic(getName2(behaviour)));

        } catch (ServiceException e) {
            e.printStackTrace();
        }

        addBehaviour(behaviour);
        mBehaviours.add(behaviour);
        agentsList.get(indexOfInfoEntry()).setBehaviours(mBehaviours);
        //  printBehaviours();
    }

    /**
     * Removes managing role from list, so that other agents can see, that this role is unfilled.
     *
     * @param behaviour The behaviour (role) to be removed.
     */
    void mRemoveBehaviour(Behaviour behaviour) {

        if (behaviour.getBehaviourName().contains("S2a")) {
            Simulation.managingRolesFilled[0] = false;
        } else if (behaviour.getBehaviourName().contains("S2b")) {
            Simulation.managingRolesFilled[1] = false;
        } else if (behaviour.getBehaviourName().contains("S2c")) {
            Simulation.managingRolesFilled[2] = false;
        } else if (behaviour.getBehaviourName().contains("S3")) {
            Simulation.managingRolesFilled[3] = false;
        } else if (behaviour.getBehaviourName().contains("S4")) {
            Simulation.managingRolesFilled[4] = false;
        }
        removeBehaviour(behaviour);

    }

    public void printBehaviours() {
        int count = 0;
        for (Behaviour b : mBehaviours) {
            System.out.println(++count + ". behaviour: " + b.getBehaviourName() + ", isS1: " + isS1());
        }
    }

    String getName2(Behaviour behaviour) {
        if (behaviour.getBehaviourName().contains("S1"))
            return "S1";
        if (behaviour.getBehaviourName().contains("S2"))
            return "S2";
        if (behaviour.getBehaviourName().contains("S3"))
            return "S3";
        if (behaviour.getBehaviourName().contains("S4"))
            return "S4";
        return null;
    }

    public boolean isS1() {
        for (Behaviour b : mBehaviours) {
            if (b.getBehaviourName().contains("S1")) {
                return true;
            }
        }
        return false;
    }

    public void assignTaskType(TaskType taskType) {

        assignedTaskType = taskType;
        //     System.out.println(getName() + " new taskType: " + taskType);
        Simulation.retoolingIncrement();
    }

    public void assignTask() {
        simulation.assignTask(this);

    }

    int indexOfInfoEntry() {
        for (AgentInfo entry : agentsList) {
            if (entry.name.contains(getName()))
                return agentsList.indexOf(entry);
        }
        return -1;
    }


    @Override
    protected void takeDown() {
        super.takeDown();
        for (int i = 0; i < mBehaviours.size(); i++) {
            mRemoveBehaviour(mBehaviours.get(i));

        }
    }

    public String nameOfLeastValuedS1(TaskType taskType) {
        int index = -1;
        double minSpeedSoFar = Double.MAX_VALUE;

        for (int i = 0; i < agentsList.size(); i++) {
            if (agentsList.get(i).isS1 && agentsList.get(i).asignedTaskType.equals(taskType) && agentsList.get(i).getSpeed(taskType) < minSpeedSoFar)
                index = i;
        }
        if (index > -1)
            return agentsList.get(index).name;
        else
            return null;
    }

    public boolean dischargeBattery(TaskType taskType) {
        return battery.discharge(energyCons[taskType.ordinal()]);

    }

    public boolean dischargeBattery(ExecutiveBehaviourType type) {
        return battery.discharge(energyCons[type.ordinal()]);

    }

    public void setToCharge() {
        agentState = AgentState.CHARGING;

        currentTask.abandonTask();
        currentTask = null;
        isWorking = false;// should be removed in favor of agentState
    }


}
