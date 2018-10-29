package sample.behaviours;

import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import javafx.application.Platform;
import sample.*;

import java.io.IOException;
import java.util.Arrays;

public abstract class S2Bhvr extends BaseBhvr {
    //ViaBot owner;
    int[] s1Distribution = new int[]{0, 0, 0};
    int[] taskDistribution = new int[]{0, 0, 0};
    AID s3Topic;
    AID s2Topic;
    double PREDICTION_WEIGHT = 0.9;
    double LOCAL_INFO_WEIGHT = 0.1;
    int[] predictedSpeeds = new int[]{0, 0, 0};//a,b,c
    TaskType taskType;
    double problemValue;
    double availableSpeed;
    MessageTemplate s3s2tpl;
ExecutiveBehaviourType behaviourType=ExecutiveBehaviourType.S2;
    public S2Bhvr(ViaBot owner, int ms) {
        super(owner, ms);
        //    this.owner = owner;
        initilizeTopics();
    }

    int s2tickcount = 0;

    @Override
    protected void onTick() {
        super.onTick();
        //System.out.println(taskType+" s2count "+s2tickcount++);

        // nolasa S1 sadalījumu pa uzdevumu veidiem
        updateS1Distribution();
//nolasa pieejamo uzdevumu sadalījumu
        updateTaskDistribution();
        //    System.out.println("s1: A: " + s1Distribution[0] + " B: " + s1Distribution[1] + " C: " + s1Distribution[2]);

        sendMessageTests3();
        receiveS2message();
        owner.addBehaviour(new S2ExchangerBhvr(owner, calculateProblemValues(), taskType));
        availableSpeed = calculateAvailableSpeed();
        problemValue = calculateProblemValue();

//battery discharge
        if (owner.agentState == AgentState.WORKING) {
            boolean isBatOk = owner.dischargeBattery(behaviourType);
            if (!isBatOk) {
                owner.setToCharge();
            }
        }

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

    double[] calculateProblemValues() {
        double[] vals = new double[TaskType.values().length];

        for (int i = 0; i < vals.length; i++) {
            vals[i] = PREDICTION_WEIGHT * predictedSpeeds[i] + LOCAL_INFO_WEIGHT * taskDistribution[i];

        }

        return vals;

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
        s2Topic = topicHelper.createTopic("S2");
        s3s2tpl = MessageTemplate.MatchTopic(s2Topic);

    }

    void updateS1Distribution() {
        owner.simulation.updateS1Distribution(s1Distribution);
    }

    void updateTaskDistribution() {
        Arrays.fill(taskDistribution, 0);
        for (Task task : owner.taskList) {
            taskDistribution[task.taskType.ordinal()]++;
        }
    }

    void receiveS2message() {

        ACLMessage msg = myAgent.receive(s3s2tpl);
        if (msg != null) {

                //  System.out.println(" received broadcast from s3");
                int[] data = new int[0];
                try {
                    data = (int[]) msg.getContentObject();

                } catch (UnreadableException e) {
                    e.printStackTrace();
                }

                // System.out.println("s2." + taskType + " recieved speed data: " + data[taskType.ordinal()]);

                //System.out.println("s3 s1: A: " + s1Distribution[0] + " B: " + s1Distribution[1] + " C: " + s1Distribution[2]);


        }
    }


}

