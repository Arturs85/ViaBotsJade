package sample;

import jade.core.AID;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.event.PlatformEvent;
import jade.core.event.PlatformListener;
import jade.core.messaging.MessagingService;
import jade.core.messaging.TopicManagementHelper;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import jade.wrapper.PlatformController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Simulation {
    Controller controller;
    ObservableList<Task> tasks = FXCollections.observableArrayList(
            new Task(0), new Task(0));
    volatile ObservableList<AgentInfo> agents = FXCollections.observableArrayList();
    LinkedList<Task> queue = new LinkedList();

    ObservableList<Task> finishedTasks = FXCollections.observableList(queue);

    static int agentNumber = 0;
    Timeline timeline;
    int simTime = 0;
    TaskGenerator taskGenerator;
    final int TASKS_LENGTH = 4;
    final int FINISHED_TASKS_SIZE = 5;
    int beltStopedTime = 0;
    double beltStopedFactor  =0;
    boolean isRunning = false;
    ContainerController cc;
    AID[] topics = new AID[5];

    //kārtas skaits ievietošanai jaunā aģenta vārdā
    int getAgentNumber() {
        return ++agentNumber;
    }

    Simulation(Controller controller) {
        this.controller = controller;
        controller.workingAgentsListView.setItems(agents);
        controller.newTasksListView.setItems(tasks);
        controller.finishedTasksListView.setItems(finishedTasks);
        timeline = new Timeline(new KeyFrame(Duration.millis(1000), ae -> simulationStep()));
        timeline.setCycleCount(Animation.INDEFINITE);
        taskGenerator = new TaskGenerator();
        // agents.add(new Agent(tasks, agents, finishedTasks));
        cc = startJade();
        registerListener();
        createGUIAgent();
    }


    void simulationStep() {
        simTime++;
        controller.update(simTime, ViaBot.totalFinishedTasks);
        if (tasks.size() < TASKS_LENGTH) {
            Task task = taskGenerator.simulationStep(simTime);
            if (task != null)
                tasks.add(task);
        } else
            beltStopedTime++;
        //lai nepārpildītos rinda- patur tikai pedējos pievienotos elementus
        if(finishedTasks.size()>FINISHED_TASKS_SIZE){
            queue.removeFirst();
        }
        beltStopedFactor=100d*beltStopedTime/simTime;
        // plānotājs ir pirmais aģents sarakstā

    }

    // void addNewAgentToList() {
    //     agents.add(new Agent(tasks, agents, finishedTasks));
    // }


    ContainerController startJade() {
        // Get a hold on JADE runtime
        Runtime rt = Runtime.instance();

        // Create a default profile
        Profile p = new ProfileImpl();
        p.setParameter(Profile.GUI, "true");
        p.setParameter(Profile.SERVICES, "jade.core.messaging.TopicManagementService");

        //p.setParameter(Profile.SERVICES,"TopicManagement");
        // Create a new non-main container, connecting to the default
// main container (i.e. on this host, port 1099)
        ContainerController cc = rt.createMainContainer(p);
        return cc;
    }

    void createAgent() {
        if (cc != null) {
            // Create a new agent, a DummyAgent
// and pass it a reference to an Object
            Object reference = new Object();
            Object args[] = new Object[]{reference, tasks, agents, finishedTasks};

            try {
                AgentController dummy = cc.createNewAgent("viaBot_" + getAgentNumber(),
                        "sample.ViaBot", args);
// Fire up the agent
                dummy.start();

                agents.add(new AgentInfo(dummy.getName(), simTime, dummy,new int[]{10,20,3}));
            } catch (Exception e) {

            }
        }
    controller.guiAgent.sendMessageUI(isRunning);
    }
    void createGUIAgent() {
        if (cc != null) {
            // Create a new agent, a DummyAgent
// and pass it a reference to an Object
            Object reference = new Object();
            Object args[] = new Object[]{reference,controller};

            try {
                AgentController dummy = cc.createNewAgent("guiAgent",
                        "sample.GUIAgent", args);
// Fire up the agent
                dummy.start();

                //agents.add(new AgentInfo(dummy.getName(), simTime, dummy,new int[]{10,20,3}));
            } catch (Exception e) {

            }
        }
    }


    void removeAgentInfoByName(String AgentName) {

        for (int i = 0; i < agents.size(); i++) {
            AgentInfo agentInfo = agents.get(i);
            if (agentInfo.name.equals(AgentName)) {
                final int it = i;
                Platform.runLater(() -> {
                    agents.remove(it);
                    System.out.println("simulation : Agentinfo size: " + agents.size());
                });
            }
        }

    }

    void registerListener() {
        if (cc != null) {
            try {
                cc.getPlatformController().addPlatformListener(pcl);
            } catch (ControllerException e) {
                e.printStackTrace();
            }
        }
    }

    PlatformController.Listener pcl = new PlatformController.Listener() {
        @Override
        public void bornAgent(jade.wrapper.PlatformEvent platformEvent) {
            System.out.println("name ID: " +
                    platformEvent.getAgentGUID());
        }

        @Override
        public void deadAgent(jade.wrapper.PlatformEvent platformEvent) {
            removeAgentInfoByName(platformEvent.getAgentGUID());
            //System.out.println("agent removed, agents left: "+agents.size());
        }

        @Override
        public void startedPlatform(jade.wrapper.PlatformEvent platformEvent) {

        }

        @Override
        public void suspendedPlatform(jade.wrapper.PlatformEvent platformEvent) {

        }

        @Override
        public void resumedPlatform(jade.wrapper.PlatformEvent platformEvent) {

        }

        @Override
        public void killedPlatform(jade.wrapper.PlatformEvent platformEvent) {

        }
    };


    PlatformListener platformListener = new PlatformListener() {
        @Override
        public void addedContainer(PlatformEvent platformEvent) {

        }

        @Override
        public void removedContainer(PlatformEvent platformEvent) {

        }

        @Override
        public void bornAgent(PlatformEvent platformEvent) {
            System.out.println("name: " +
                    platformEvent.getAgent().getName());
        }

        @Override
        public void deadAgent(PlatformEvent platformEvent) {

        }

        @Override
        public void movedAgent(PlatformEvent platformEvent) {

        }

        @Override
        public void suspendedAgent(PlatformEvent platformEvent) {

        }

        @Override
        public void resumedAgent(PlatformEvent platformEvent) {

        }

        @Override
        public void frozenAgent(PlatformEvent platformEvent) {

        }

        @Override
        public void thawedAgent(PlatformEvent platformEvent) {

        }
    };

}
