package sample.behaviours;

import jade.core.AID;
import jade.core.ServiceException;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import sample.*;

import java.io.IOException;
import java.util.LinkedList;

public class S4Bhvr extends BaseBhvr {


    AID s4Topic;
    AID s4s3Topic;

    //  ViaBot owner;
    MessageTemplate tpl;
    Statistics statistics = new Statistics();
    TaskGenerator taskGenerator = new TaskGenerator();
    int[] predictedSpeeds = new int[]{1, 1, 1};
    double[] speeds = new double[]{0, 0, 0};
    int[] taskDistribution = new int[]{0, 0, 0};


    public S4Bhvr(ViaBot a, long period) {
        super(a, period);
        initilizeTopics();
    }


    int s4count = 0;
    ExecutiveBehaviourType behaviourType = ExecutiveBehaviourType.S4;


    @Override
    protected void onTick() {
        super.onTick();
        //  System.out.println(" s4c "+s4count++);
        taskGenerator.simulationStep(s4count);
        //battery discharge
        if (owner.agentState == AgentState.WORKING) {
            boolean isBatOk = owner.dischargeBattery(behaviourType);
            if (!isBatOk) {//isBatOk - battery is not discharged
                owner.setToCharge();
            }
        }
//        receiveS4message();
        statistics.calcAverageDist();
        //   System.out.println("s4 avg: "+statistics.avg[0]+" "+statistics.avg[1]+" "+statistics.avg[2]);
        calcSpeedsforS3();
        sendMessageToS3();
chekReset();

        s4count++;
    }

    void calcSpeedsforS3() {
        for (int i = 0; i < speeds.length; i++) {
            //   speeds[i] = taskDistribution[i] * Simulation.avgPartArriveTime;
            //temp

            speeds[i] = (taskGenerator.curentPeriodTaskDistribution[i] / 10);//+ taskDistribution[i] * Simulation.avgPartArriveTime) / 2;
        }

    }


    void initilizeTopics() {
        TopicManagementHelper topicHelper = null;
        try {
            topicHelper = (TopicManagementHelper) myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
            s4Topic = topicHelper.createTopic("S4");
            s4s3Topic = topicHelper.createTopic("S4S3");

            if (s4Topic == null) System.out.println("s4topic == null; from s3bhv");

            tpl = MessageTemplate.MatchTopic(s4Topic);
            topicHelper.register(s4Topic);

        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    void receiveS4message() {

        //ACLMessage msg = myAgent.receive(tpl);
        // if (msg != null) {
        ACLMessage msg2 = myAgent.receive(tpl);
// apstrādā tikai pedējo ziņu
        if (msg2 != null) {
            //System.out.println(" received s3s4 broadcast");
            int[][] data;
            try {
                Object o = (int[][]) msg2.getContentObject();
                if (o != null) {
                    data = (int[][]) o;
                    // s1Distribution = data[0];
                    // taskDistribution = data[1];
                    statistics.addNewFrame(data[2]);
                } else
                    System.out.println("s4 received msg with null data");
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
            //System.out.println("s4 : Agentinfo size: " + owner.agentsList.size());

            //  System.out.println("s4 s1: A: " + s1Distribution[0] + " B: " + s1Distribution[1] + " C: " + s1Distribution[2]);

        } //else receiveS3message(); //recu
    }

    void sendMessageToS3() {//for test
        // int[][] data = new int[][]{s1Distribution,taskDistribution};
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);

        msg.addReceiver(s4s3Topic);
        try {
            msg.setContentObject(speeds);
        } catch (IOException e) {
            e.printStackTrace();
        }


        myAgent.send(msg);

    }
    void chekReset(){
       // System.out.println("chek reset- called: "+resetCalled+" "+super.resetCalled);
        if(owner.resetCalled){
            taskGenerator.reset();
             System.out.println("s4 task generator resetting");
owner.resetCalled=false;
        }
    }

}
