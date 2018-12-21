package sample.behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ServiceException;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.messaging.TopicManagementHelper;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import javafx.application.Platform;
import sample.*;
import sample.Task.*;
import sample.messageObjects.TransferS1MsgObj;

import java.lang.Enum;
import java.util.Iterator;

public class S1Bhvr extends BaseBhvr {

    ViaBot owner;
    MessageTemplate tpl;
ToolChanger toolChanger;
AgentState previousState;

    public S1Bhvr(ViaBot owner, int ms) {
        super(owner, ms);
        this.owner = owner;
        toolChanger = new ToolChanger();
        TopicManagementHelper topicHelper = null;
        try {
            topicHelper = (TopicManagementHelper) myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
            AID s1Topic = topicHelper.createTopic("S1");
            tpl = MessageTemplate.MatchTopic(s1Topic);

        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    int s1tickcount = 0;

    @Override
    protected void onTick() {
        super.onTick();
        //  receiveUImessage();
        ACLMessage msg = myAgent.receive(tpl);
        if (msg != null) {
            String s = msg.getContent();
/*
            if (s.equalsIgnoreCase("a")) {
                owner.assignTaskType(TaskType.A);
            }
            if (s.equalsIgnoreCase("b")) {
                owner.assignTaskType(TaskType.B);
            }
            if (s.equalsIgnoreCase("c")) {
                owner.assignTaskType(TaskType.C);
            }
*/
            //temporary
            //if (!owner.isWorking)
            //  owner.assignTaskType(Task.getRandomTask());

        }
        receiveTypeChangeMessage();

       boolean justFinishedChange= toolChanger.simStep();
       if(toolChanger.isChanging)
           owner.agentState=AgentState.RECONFIGURING;
       if(justFinishedChange) {
           owner.assignTaskType(toolChanger.nextTaskType);
       owner.agentState=previousState;
       }

        if (owner.agentState == AgentState.WORKING) {
            boolean isBatOk = owner.dischargeBattery(owner.assignedTaskType);
            if (!isBatOk) {
                owner.setToCharge();
            }
        } else if (owner.agentState == AgentState.CHARGING) {
            boolean isCharging = owner.battery.charge();
            if (!isCharging) {
                owner.agentState = AgentState.IDLE;

            }
        }

        if (owner != null) {
            if (owner.simulationRunning) {
                if (!owner.isWorking && owner.agentState == AgentState.IDLE) {
                    owner.assignTask();
                }

                if (owner.currentTask != null && owner.agentState == AgentState.WORKING) {
                    owner.currentTask.progress += owner.speed[owner.currentTask.taskType.ordinal()];
                    //pārbauda vai uzdevums ir pabeigts
                    if (owner.currentTask.progress >= Task.FULL_PROGRESS) {
                        owner.currentTask.progress = Task.FULL_PROGRESS;
                        owner.isWorking = false;

                        owner.agentState = AgentState.IDLE;
                        owner.finishedTasksCount[owner.currentTask.taskType.ordinal()]++;

                        owner.totalFinishedTasks++;

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                owner.simulation.moveToFinishedList(owner);
                            }
                        });

                    }
                }
                updateInfo();
            }
        } else
            System.out.println(getTickCount() + " agent null");


    }

    void updateInfo() {
        for (Iterator<AgentInfo> iterator = owner.agentsList.iterator(); iterator.hasNext(); ) {
            AgentInfo info = iterator.next();
            if (info.name.equals(owner.getName())) {
                info.currentTask = owner.currentTask;
                info.behaviours = owner.mBehaviours;
                info.finishedTasksCount = owner.finishedTasksCount;

/*if(info.getSpeedA()!=owner.speed[0])
info.setSpeedA(owner.speed[0]);
                if(info.getSpeedB()!=owner.speed[1])
                    info.setSpeedB(owner.speed[1]);
                if(info.getSpeedC()!=owner.speed[2])
                    info.setSpeedC(owner.speed[2]);*/
                owner.speed[0] = info.getSpeedA();
                owner.speed[1] = info.getSpeedB();
                owner.speed[2] = info.getSpeedC();
                owner.speed[3] = info.getSpeedS2();
                owner.speed[4] = info.getSpeedS3();
                owner.speed[5] = info.getSpeedS4();

//info.setFinishedTasksA(owner.finishedTasksCount[0]);
//owner.getLocalName()
                info.isS1 = true;
                info.asignedTaskType = owner.assignedTaskType;
                info.batteryCharge = owner.battery.energyLeft;
                info.agentState=owner.agentState;
            }
        }
    }


    void receiveTypeChangeMessage() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);


        ACLMessage msg = myAgent.receive(mt);
        if (msg != null) {
            ACLMessage msg2 = myAgent.receive(mt);
// apstrādā tikai pedējo ziņu
            if (msg2 == null) {
                //  System.out.println(" received ui broadcast");
                boolean[] data;
                try {
                    TransferS1MsgObj msgInfo = (TransferS1MsgObj) msg.getContentObject();
                    startToolChange(msgInfo.desiredType);
                    //owner.assignTaskType(msgInfo.desiredType);
                    //       System.out.println(owner.getName() + " changed task type to " + owner.assignedTaskType);
                } catch (UnreadableException e) {
                    e.printStackTrace();
                }
                //    System.out.println("s3 : Agentinfo size: "+owner.agentsList.size());
            } else receiveTypeChangeMessage(); //recu
        }
    }
void startToolChange(TaskType newTaskType){
    if(owner.agentState!=AgentState.RECONFIGURING) {
        toolChanger.startChange(newTaskType);
        previousState = owner.agentState;//saglabā stāvokil, lai varētu atgriezties
    }
    }
}
