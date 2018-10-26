package sample.behaviours;

import jade.core.AID;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import sample.Task;
import sample.TaskType;
import sample.ViaBot;
import sample.messageObjects.TransferS1MsgObj;

import java.io.IOException;
import java.util.Arrays;

public class S2ExchangerBhvr extends Behaviour {
    AID s2ExchangeTopic;
    MessageTemplate s2eTemplate;
    ViaBot owner;
    double[] pValues;
    TaskType ownerTaskType;
    int[] s1distr = new int[]{0, 0, 0};

    public S2ExchangerBhvr(ViaBot owner, double[] pValues, TaskType taskType) {
        super(owner);
        //    System.out.println("s2ExchangeBhvr. constr");
        this.owner = owner;
        this.pValues = pValues;
        ownerTaskType = taskType;
        initilizeTopics();
    }


    @Override
    public void action() {
        //  System.out.println("s2ExchangeBhvr.action "+ownerTaskType);

        queryFirstProto();

    }


    void initilizeTopics() {
        TopicManagementHelper topicHelper = null;
        try {
            topicHelper = (TopicManagementHelper) myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        s2ExchangeTopic = topicHelper.createTopic("S2E");
        try {
            topicHelper.register(s2ExchangeTopic);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
        s2eTemplate = MessageTemplate.MatchTopic(s2ExchangeTopic);

    }

    void queryFirstProto() {

        if (ownerTaskType.equals(indexOfMax(pValues)))
            callForProposal(ownerTaskType);

        listenForCfp();

    }

    void proposalFirstProto() {

        listenForProposals();

        //check if owner of this behavioue coresponds to the type with lowest pValue
        if (ownerTaskType.equals(indexOfMin(pValues))) {

            //propose some s1 agent to s2 with the highest pValue
            String name = owner.nameOfLeastValuedS1(ownerTaskType);
            if (name != null)
                proposeS1(name, indexOfMax(pValues));


        }
    }

    void proposeS1(String name, TaskType proposedTaskType) {
        //sends s1 proposal to all other s2
      //  System.out.println("Sending proposals");
        ACLMessage proposal = new ACLMessage(ACLMessage.PROPOSE);
        try {
            proposal.setContentObject(new TransferS1MsgObj(name, proposedTaskType, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
        proposal.addReceiver(s2ExchangeTopic);
        myAgent.send(proposal);

        //waits for replay (y/n)
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
// Message received. Process it
  //          System.out.println("received confirmation");
            try {
                TransferS1MsgObj msgInfo = (TransferS1MsgObj) msg.getContentObject();
                if (msgInfo.isNeeded) {
//send particular s1 message to change task type
  //                  System.out.println("sending order to s1 named " + name);
                    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                    try {
                        request.setContentObject(new TransferS1MsgObj(name, proposedTaskType, false));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    request.addReceiver(new AID(name));
                    myAgent.send(request);

                }
            } catch (UnreadableException e) {
                e.printStackTrace();
            }


        } else {
            block(100);
        }
    }


    void callForProposal(TaskType proposedTaskType) {
        //sends s1 proposal to all other s2
      //  System.out.println("Sending proposals");
        ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
        try {
            cfp.setContentObject(new TransferS1MsgObj(null, proposedTaskType, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        cfp.addReceiver(s2ExchangeTopic);
        myAgent.send(cfp);

        //waits for replay (y/n)
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
// Message received. Process it
      //      System.out.println("received confirmation");

        } else {
            block(100);
        }


    }


    void listenForCfp() {
      //  System.out.println("Listening for cfp " + ownerTaskType);
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
       //     System.out.println(" received cfp");
            owner.simulation.updateS1Distribution(s1distr);
            if (indexOfMax(s1distr).equals(ownerTaskType)) { // šeit jātaisa lai pieņem pieprasījumu tas, kuram ir s1 aģenti
            //    System.out.println("accepting cfp, ");
                String name = owner.nameOfLeastValuedS1(ownerTaskType);
                if (name != null) {

                    ACLMessage replay = new ACLMessage(ACLMessage.CONFIRM);

                    replay.addReceiver(msg.getSender());
                    owner.send(replay);


                  //  System.out.println("sending order to s1 named " + name);
                    ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                    try {
                        TaskType proposedTaskType = ((TransferS1MsgObj) msg.getContentObject()).desiredType;
                        request.setContentObject(new TransferS1MsgObj(name, proposedTaskType, false));
                    } catch (IOException | UnreadableException e) {
                        e.printStackTrace();
                    }
                    request.addReceiver(new AID(name));
                    myAgent.send(request);

                }
            }
        }


    }


    void listenForProposals() {
  //      System.out.println("Listening for proposals " + ownerTaskType);
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
       //     System.out.println(" received proposal");

            if (indexOfMax(pValues).equals(ownerTaskType)) {
          //      System.out.println("accepting proposal");
                ACLMessage replay = new ACLMessage(ACLMessage.CONFIRM);
                try {
                    TransferS1MsgObj obj = (TransferS1MsgObj) msg.getContentObject();
                    obj.isNeeded = true;
                    replay.setContentObject(obj);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                replay.addReceiver(msg.getSender());
                owner.send(replay);

            }
        }


    }


    @Override
    public boolean done() {
        return true;
    }

    TaskType indexOfMin(double[] data) {
        double min = Double.MAX_VALUE;
        int index = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] < min) {
                index = i;
                min = data[i];
            }
        }
        return TaskType.values()[index];
    }

    TaskType indexOfMax(double[] data) {
        double max = Double.MIN_VALUE;
        int index = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > max) {
                index = i;
                max = data[i];
            }
        }
        return TaskType.values()[index];
    }


    TaskType indexOfMax(int[] data) {
        int min = Integer.MIN_VALUE;
        int index = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > min) {
                index = i;
                min = data[i];
            }
        }
        return TaskType.values()[index];
    }

}
