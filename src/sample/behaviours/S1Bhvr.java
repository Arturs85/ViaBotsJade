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
import sample.AgentInfo;
import sample.Task;
import sample.Task.*;
import sample.TaskType;
import sample.ViaBot;

import java.lang.Enum;
import java.util.Iterator;

public class S1Bhvr extends BaseBhvr {

    ViaBot owner;
    MessageTemplate tpl;

    public S1Bhvr(ViaBot owner, int ms) {
        super(owner, ms);
        this.owner = owner;

        TopicManagementHelper topicHelper = null;
        try {
            topicHelper = (TopicManagementHelper) myAgent.getHelper(TopicManagementHelper.SERVICE_NAME);
            AID s1Topic = topicHelper.createTopic("S1");
            tpl = MessageTemplate.MatchTopic(s1Topic);

        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onTick() {
        receiveUImessage();

        ACLMessage msg = myAgent.receive(tpl);
        if (msg != null) {
            String s = msg.getContent();
            if (s.equalsIgnoreCase("a")) {
                owner.assignTaskType(TaskType.A);
            }
            if (s.equalsIgnoreCase("b")) {
                owner.assignTaskType(TaskType.B);
            }
            if (s.equalsIgnoreCase("c")) {
                owner.assignTaskType(TaskType.C);
            }
        }

        if (owner != null) {
            if (owner.simulationRunning) {
                if (!owner.isWorking) {
                    owner.assignTask();
                }

                if (owner.currentTask != null) {
                    owner.currentTask.progress += owner.speed[owner.currentTask.taskType.ordinal()];
                    //pārbauda vai ir uzdevums ir pabeigts
                    if (owner.currentTask.progress >= Task.FULL_PROGRESS) {
                        owner.currentTask.progress = Task.FULL_PROGRESS;
                        owner.isWorking = false;
                        owner.finishedTasksCount[owner.currentTask.taskType.ordinal()]++;

                        owner.totalFinishedTasks++;
                        int finishedTaskIndex = owner.taskList.indexOf(owner.currentTask);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                owner.finishedTasksList.add(owner.taskList.get(finishedTaskIndex));
                                owner.taskList.remove(finishedTaskIndex);
                            }
                        });

                        owner.currentTask = null;

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

//info.setFinishedTasksA(owner.finishedTasksCount[0]);
//owner.getLocalName()
                info.isS1 = true;
                info.asignedTaskType = owner.assignedTaskType;
            }
        }
    }


}
