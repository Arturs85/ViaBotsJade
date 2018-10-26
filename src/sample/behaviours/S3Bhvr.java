package sample.behaviours;

import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import sample.AgentState;
import sample.Task;
import sample.TaskType;
import sample.ViaBot;

import java.io.IOException;
import java.util.Arrays;

public class S3Bhvr extends BaseBhvr {
    AID s3Topic;
    AID s2Topic;
    //  ViaBot owner;
    MessageTemplate tpl;
    MessageTemplate s3s2tpl;
    int[] s1Distribution = new int[]{0, 0, 0};
    int[] taskDistribution = new int[]{0, 0, 0};
    ExecutiveBehaviourType behaviourType = ExecutiveBehaviourType.S3;


    public S3Bhvr(ViaBot owner, int ms) {
        super(owner, ms);
        //  this.owner = owner;
        initilizeTopics();//dublējas ar nākošo

        TopicManagementHelper topicHelper = null;
        try {
            topicHelper = (TopicManagementHelper) myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
            s3Topic = topicHelper.createTopic("S3");
            s2Topic = topicHelper.createTopic("S2");
            tpl = MessageTemplate.MatchTopic(s3Topic);
            s3s2tpl = MessageTemplate.MatchTopic(s2Topic);
            topicHelper.register(s3Topic);

        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    int s3tickcount = 0;

    @Override
    protected void onTick() {
        super.onTick();
        System.out.println(" s3count " + s3tickcount++);

        receiveS3message();
        calcPrefDist();
        // sendMessageToS1();
        sendMessageToS2();

        //battery discharge
        if (owner.agentState == AgentState.WORKING) {
            boolean isBatOk = owner.dischargeBattery(behaviourType);
            if (!isBatOk) {
                owner.setToCharge();
            }
        }

    }

    void calcPrefDist() {
        float taskDistrPct[] = new float[taskDistribution.length];
        float totalTasks = Arrays.stream(taskDistribution).sum();
        for (int i = 0; i < taskDistrPct.length; i++) {
            taskDistrPct[i] = taskDistribution[i] / totalTasks;
        }
        // s1 aģentu kopejais skaits
        float totalS1 = Arrays.stream(s1Distribution).sum();
//velamais aģentu sadalījums
        int s1DistrDesired[] = new int[taskDistribution.length];
        for (int i = 0; i < taskDistrPct.length; i++) {
            s1DistrDesired[i] = Math.round(taskDistrPct[i] * totalS1);
        }


//        System.out.println(" pref agent dist " + s1DistrDesired[0] + " " + s1DistrDesired[1] + " " + s1DistrDesired[2]);
// sadala uzdevumus proporcionali

    }

    void sendMessageToS1() {//for test
        // int[][] data = new int[][]{s1Distribution,taskDistribution};
        if (owner.taskList.size() < 1) return;
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        // msg.addReceiver(s1Topic);
        if (owner.taskList.get(owner.taskList.size() - 1).taskType.ordinal() == 0)
            msg.setContent("a");
        if (owner.taskList.get(owner.taskList.size() - 1).taskType.ordinal() == 1)
            msg.setContent("b");
        if (owner.taskList.get(owner.taskList.size() - 1).taskType.ordinal() == 2)
            msg.setContent("c");

        myAgent.send(msg);

    }

    void sendMessageToS2() {//for test
        // int[][] data = new int[][]{s1Distribution,taskDistribution};
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        msg.addReceiver(s2Topic);
        try {
            msg.setContentObject(new int[]{10, 15, 25});
        } catch (IOException e) {
            e.printStackTrace();
        }


        myAgent.send(msg);

    }

    void initilizeTopics() {
        TopicManagementHelper topicHelper = null;
        try {
            topicHelper = (TopicManagementHelper) myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        // s1Topic = topicHelper.createTopic("S1");

    }

    void receiveS3message() {

        //ACLMessage msg = myAgent.receive(tpl);
        // if (msg != null) {
        ACLMessage msg2 = myAgent.receive(tpl);
// apstrādā tikai pedējo ziņu
        if (msg2 == null) {
            System.out.println(" received s3 broadcast");
            int[][] data;
            try {
                data = (int[][]) msg2.getContentObject();
                s1Distribution = data[0];
                taskDistribution = data[1];
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
            System.out.println("s3 : Agentinfo size: " + owner.agentsList.size());

            System.out.println("s3 s1: A: " + s1Distribution[0] + " B: " + s1Distribution[1] + " C: " + s1Distribution[2]);

        } //else receiveS3message(); //recu
    }
    // }

}
