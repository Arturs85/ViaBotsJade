package sample.behaviours;

import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import sample.*;

import java.io.IOException;
import java.util.Arrays;

public class S3Bhvr extends BaseBhvr {
    AID s3Topic;
    AID s2Topic;
    AID s4Topic;

    //  ViaBot owner;
    MessageTemplate tpl;
    MessageTemplate s3s2tpl;
    int[] s1Distribution = new int[]{0, 0, 0};
    int[] taskDistribution = new int[]{0, 0, 0};
    int[] taskDistributionTest = new int[]{1, 1, 6};

    ExecutiveBehaviourType behaviourType = ExecutiveBehaviourType.S3;
    double[] speeds = new double[]{0, 0, 0};
    ACLMessage lastDistrMsg;

    public S3Bhvr(ViaBot owner, int ms) {
        super(owner, ms);
        //  this.owner = owner;
        initilizeTopics();


    }

    int s3tickcount = 0;

    @Override
    protected void onTick() {
        super.onTick();
        //      System.out.println(" s3count " + s3tickcount++);

        receiveS3message();
        calcPrefDist();

//getCurTaskDistribution();

        calcSpeedsforS2();
        sendMessageToS2();
        sendMessageToS4();
        //battery discharge
        if (owner.agentState == AgentState.WORKING) {
            boolean isBatOk = owner.dischargeBattery(behaviourType);
            if (!isBatOk) {
                owner.setToCharge();
            }
        }
      //  System.out.println("S3 ownr msgQ size" + owner.getCurQueueSize());

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

    //direct access to information about environment
    void getCurTaskDistribution() {
        taskDistribution = owner.simulation.getCurTaskDistribution(taskDistribution);

    }

    void calcSpeedsforS2() {
        for (int i = 0; i < speeds.length; i++) {
            speeds[i] = taskDistribution[i] * Simulation.avgPartArriveTime;
        }

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
            msg.setContentObject(speeds);
        } catch (IOException e) {
            e.printStackTrace();
        }


        myAgent.send(msg);

    }

    void sendMessageToS4() {// if s3 recieves multiple identical messages from s2`s, still it sends only one to s4
        if (lastDistrMsg != null) {
            lastDistrMsg.clearAllReceiver();
            lastDistrMsg.addReceiver(s4Topic);
            owner.send(lastDistrMsg);
        }

    }

    void initilizeTopics() {
        TopicManagementHelper topicHelper = null;
        try {
            topicHelper = (TopicManagementHelper) myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
            s3Topic = topicHelper.createTopic("S3");
            s2Topic = topicHelper.createTopic("S2");
            s4Topic = topicHelper.createTopic("S4");

            if (s3Topic == null) System.out.println("s3topic == null; from s3bhv");

            tpl = MessageTemplate.MatchTopic(s3Topic);
            s3s2tpl = MessageTemplate.MatchTopic(s2Topic);
            topicHelper.register(s3Topic);

        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    void receiveS3message() {

        ACLMessage msg = myAgent.receive(tpl);
        if (msg != null) {
            ACLMessage msg2 = myAgent.receive(tpl);
// apstrādā tikai pedējo ziņu
            if (msg2 == null) {
                lastDistrMsg = msg;

                //   System.out.println(" received s3 broadcast");
                int[][] data;
                try {
                    Object o = (int[][]) msg.getContentObject();
                    if (o != null) {
                        data = (int[][]) o;
                        s1Distribution = data[0];
                        taskDistribution = data[1];
                    } else
                        System.out.println("s3 received msg with null data");
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                //   System.out.println("s3 : Agentinfo size: " + owner.agentsList.size());

                //   System.out.println("s3 s1: A: " + s1Distribution[0] + " B: " + s1Distribution[1] + " C: " + s1Distribution[2]);

            } else receiveS3message(); //recu
        }
    }
}
