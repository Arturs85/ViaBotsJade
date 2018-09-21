package sample.behaviours;

import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import sample.AgentInfo;
import sample.Task;
import sample.ViaBot;

import java.io.IOException;
import java.util.Arrays;

public class S2Bhvr extends BaseBhvr {
    ViaBot owner;
    int[] s1Distribution = new int[]{0, 0, 0};
    int[] taskDistribution = new int[]{0, 0, 0};
    AID s3Topic;

    public S2Bhvr(ViaBot owner, int ms) {
        super(owner, ms);
        this.owner = owner;
    initilizeTopics();
    }

    @Override
    protected void onTick() {
        // nolasa S1 sadalījumu pa uzdevumu veidiem
        updateS1Distribution();
//nolasa pieejamo uzdevumu sadalījumu
        updateTaskDistribution();
        System.out.println("s1: A: "+s1Distribution[0]+" B: "+s1Distribution[1]+" C: "+s1Distribution[2]);

        sendMessageTests3();



    }

void sendMessageTests3(){
        int[][] data = new int[][]{s1Distribution,taskDistribution};
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
void initilizeTopics(){
    TopicManagementHelper topicHelper = null;
    try {
        topicHelper = (TopicManagementHelper)myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
    } catch (ServiceException e) {
        e.printStackTrace();
    }
    s3Topic = topicHelper.createTopic("S3");

}

    void updateS1Distribution() {
        Arrays.fill(s1Distribution,0);
        for (AgentInfo info : owner.agentsList) {
            if (info.isS1) {
                s1Distribution[info.asignedTaskType.ordinal()]++;
            }
        }
    }

    void updateTaskDistribution() {
        Arrays.fill(taskDistribution,0);
        for (Task task : owner.taskList) {
            taskDistribution[task.taskType.ordinal()]++;
        }
    }

}

