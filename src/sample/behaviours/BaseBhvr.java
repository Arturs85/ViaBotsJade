package sample.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import sample.AgentState;
import sample.ViaBot;
import sample.messageObjects.GuiAgentMessageToOther;

public abstract class BaseBhvr extends TickerBehaviour {
    TopicManagementHelper topicHelper = null;
    AID uiTopic; //ui ziņu temats(piem, stop/run)
    MessageTemplate uiMsgTpl;
    boolean isRunning = false;
    ViaBot owner;
    int defPauseMs = 1000;
    int speedFactor = 1;

    public BaseBhvr(ViaBot a, long period) {
        super(a, period);
        owner = a;
        try {
            topicHelper = (TopicManagementHelper) myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
            uiTopic = topicHelper.createTopic("uiTopic");
            topicHelper.register(uiTopic);// ?
            uiMsgTpl = MessageTemplate.MatchTopic(uiTopic);

        } catch (
                ServiceException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onTick() {
        owner.addBehaviour(new ManagingRoleChekerBhvr(owner));
    receiveUImessage();

    }

    void receiveUImessage() {


        ACLMessage msg = myAgent.receive(uiMsgTpl);
        if (msg != null) {
            ACLMessage msg2 = myAgent.receive(uiMsgTpl);
// apstrādā tikai pedējo ziņu
            if (msg2 == null) {
                //  System.out.println(" received ui broadcast");
                GuiAgentMessageToOther data;
                try {
                    data = (GuiAgentMessageToOther) msg.getContentObject();
                    isRunning = data.isSimulationRunning;
                    ((ViaBot) myAgent).simulationRunning = isRunning;
                    if (data.agentSpeedFactor >= 1) {
                        speedFactor = data.agentSpeedFactor;
                       reset(defPauseMs / speedFactor);
                        System.out.println("ticker behaviour period reset to "+getPeriod());
                    }
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                //    System.out.println("s3 : Agentinfo size: "+owner.agentsList.size());
            } else receiveUImessage(); //recu
        }
    }
}
