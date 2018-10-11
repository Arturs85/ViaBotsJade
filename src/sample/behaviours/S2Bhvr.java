package sample.behaviours;

import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import javafx.application.Platform;
import sample.AgentInfo;
import sample.Task;
import sample.TaskType;
import sample.ViaBot;

import java.io.IOException;
import java.util.Arrays;

public abstract class S2Bhvr extends BaseBhvr {
    //ViaBot owner;
    int[] s1Distribution = new int[]{0, 0, 0};
    int[] taskDistribution = new int[]{0, 0, 0};
    AID s3Topic;
    double PREDICTION_WEIGHT = 0.9;
    double LOCAL_INFO_WEIGHT = 0.1;
    int[] predictedSpeeds = new int[]{0, 0, 0};//a,b,c
    TaskType taskType;
    double problemValue;
    double availableSpeed;

    public S2Bhvr(ViaBot owner, int ms) {
        super(owner, ms);
        //    this.owner = owner;
        initilizeTopics();
    }

    @Override
    protected void onTick() {
        super.onTick();

        // nolasa S1 sadalījumu pa uzdevumu veidiem
        updateS1Distribution();
//nolasa pieejamo uzdevumu sadalījumu
        updateTaskDistribution();
        System.out.println("s1: A: " + s1Distribution[0] + " B: " + s1Distribution[1] + " C: " + s1Distribution[2]);

        sendMessageTests3();

        availableSpeed = calculateAvailableSpeed();
        problemValue = calculateProblemValue();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                updateUI();
            }
        });

    }

    abstract void updateUI();

    double calculateProblemValue() {
        double val = PREDICTION_WEIGHT * predictedSpeeds[taskType.ordinal()] + LOCAL_INFO_WEIGHT * taskDistribution[taskType.ordinal()];

        return val;

    }

    double calculateAvailableSpeed() {
        double speed = 0;
        for (AgentInfo ai : owner.agentsList) {
            for (Behaviour be :
                    ai.behaviours) {
                if (be.getBehaviourName().contains("S1") && ((ViaBot) be.getAgent()).assignedTaskType.equals(taskType))
                    speed += ai.getSpeed(taskType);
            }
        }
        return speed;
    }


    void sendMessageTests3() {
        int[][] data = new int[][]{s1Distribution, taskDistribution};
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        try {
            msg.setContentObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        msg.addReceiver(s3Topic);
        // msg.setContent("b");
        myAgent.send(msg);

    }

    void initilizeTopics() {
        TopicManagementHelper topicHelper = null;
        try {
            topicHelper = (TopicManagementHelper) myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        s3Topic = topicHelper.createTopic("S3");

    }

    void updateS1Distribution() {
        Arrays.fill(s1Distribution, 0);
        for (AgentInfo info : owner.agentsList) {
            if (info.isS1) {
                s1Distribution[info.asignedTaskType.ordinal()]++;
            }
        }
    }

    void updateTaskDistribution() {
        Arrays.fill(taskDistribution, 0);
        for (Task task : owner.taskList) {
            taskDistribution[task.taskType.ordinal()]++;
        }
    }

}

