package sample.behaviours;

import jade.core.AID;
import jade.core.ServiceException;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import sample.AgentState;
import sample.Statistics;
import sample.ViaBot;

import java.util.LinkedList;

public class S4Bhvr extends BaseBhvr {


    AID s4Topic;
    //  ViaBot owner;
    MessageTemplate tpl;
    Statistics statistics = new Statistics();












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

        //battery discharge
        if (owner.agentState == AgentState.WORKING) {
            boolean isBatOk = owner.dischargeBattery(behaviourType);
            if (!isBatOk) {//isBatOk - battery is not discharged
                owner.setToCharge();
            }
        }
//
receiveS3message();
    statistics.calcAverageDist();
     //   System.out.println("s4 avg: "+statistics.avg[0]+" "+statistics.avg[1]+" "+statistics.avg[2]);
    }

    void initilizeTopics() {
        TopicManagementHelper topicHelper = null;
        try {
            topicHelper = (TopicManagementHelper) myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
            s4Topic = topicHelper.createTopic("S4");

            if (s4Topic == null) System.out.println("s4topic == null; from s3bhv");

            tpl = MessageTemplate.MatchTopic(s4Topic);
            topicHelper.register(s4Topic);

        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    void receiveS3message() {

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



}
