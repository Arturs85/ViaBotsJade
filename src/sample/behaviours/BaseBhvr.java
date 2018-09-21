package sample.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import sample.ViaBot;

public abstract class BaseBhvr extends TickerBehaviour {
    TopicManagementHelper topicHelper = null;
    AID uiTopic; //ui ziņu temats(piem, stop/run)
    MessageTemplate uiMsgTpl;
boolean isRunning = false;

    public BaseBhvr(Agent a, long period) {
        super(a, period);

        try {
            topicHelper = (TopicManagementHelper)myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
            uiTopic = topicHelper.createTopic("uiTopic");

            topicHelper.register(uiTopic);// ?

            uiMsgTpl = MessageTemplate.MatchTopic(uiTopic);


        } catch (
                ServiceException e) {
            e.printStackTrace();
        }

    }


void receiveUImessage(){


    ACLMessage msg = myAgent.receive(uiMsgTpl);
    if (msg != null) {
        ACLMessage msg2 = myAgent.receive(uiMsgTpl);
// apstrādā tikai pedējo ziņu
        if(msg2==null){
          //  System.out.println(" received ui broadcast");
            boolean[] data;
            try {
                data = (boolean[]) msg.getContentObject();
                isRunning = data[0];
                ((ViaBot)myAgent).simulationRunning = isRunning;
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        //    System.out.println("s3 : Agentinfo size: "+owner.agentsList.size());

        }else receiveUImessage(); //recu
    }

}




}
