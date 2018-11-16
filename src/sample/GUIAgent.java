package sample;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javafx.collections.ObservableList;
import sample.Controller;
import sample.behaviours.S1Bhvr;
import sample.behaviours.S2Bhvr;
import sample.behaviours.S3Bhvr;
import sample.messageObjects.GuiAgentMessageToOther;

import java.io.IOException;

public class GUIAgent extends Agent {
    Controller controller;
    TopicManagementHelper topicHelper = null;
    AID uiTopic; //ui ziņu temats(piem, stop/run)
    MessageTemplate uiMsgTpl;
int simSpeedFactor=1;
    protected void setup() {

        Object[] args = getArguments();

        this.controller = (Controller) args[1];
        controller.setGUIAgent(this);
        try {
            topicHelper = (TopicManagementHelper) getHelper(TopicManagementHelper.SERVICE_NAME);
            uiTopic = topicHelper.createTopic("uiTopic");

            //topicHelper.register(uiTopic);// ?

            // uiMsgTpl = MessageTemplate.MatchTopic(uiTopic);


        } catch (
                ServiceException e) {
            e.printStackTrace();
        }
    }

    void sendMessageUI(boolean isRunning) {
    //    System.out.println("sending ui is running: " + isRunning);
        GuiAgentMessageToOther data = new GuiAgentMessageToOther(isRunning,simSpeedFactor );
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        try {
            msg.setContentObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        msg.addReceiver(uiTopic);
        // msg.setContent("b");
        send(msg);

    }

}
