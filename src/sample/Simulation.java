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

import java.util.*;

public class Simulation {
    public Controller controller;
    private ObservableList<Task> tasks = FXCollections.observableArrayList(
            new Task(0), new Task(0));
    volatile ObservableList<AgentInfo> agents = FXCollections.observableArrayList();
    LinkedList<Task> queue = new LinkedList();

    ObservableList<Task> finishedTasks = FXCollections.observableList(queue);
    static ObservableList<String> roleList = FXCollections.observableArrayList("S1", "S2", "S3", "S4");
    static int agentNumber = 0;
    static int noOfRetoolings = 0;
    int simStepDefDuration = 1000;
    int simTimeFactor = 1;
    Timeline timeline;
    int simTime = 0;
    TaskGenerator taskGenerator;
    final int TASKS_LENGTH = 8;
    final int FINISHED_TASKS_SIZE = 20;
    int beltStopedTime = 0;
    double beltStopedFactor = 0;
    boolean isRunning = false;
    ContainerController cc;
    AID[] topics = new AID[5];
    public static boolean[] managingRolesFilled = new boolean[]{false, false, false, false, false};//s2.a, s2.b, s2.c,s3,s4
    public static String[] managingRoles = new String[]{"S2a", "S2b", "S2c", "S3", "S4"};
    public static double avgPartArriveTime = 1.5; //for s3 calculations
    public int[] incomingPartDist = new int[]{0, 0, 0};
    public int lastIncPartDistCaptureTime = 0;

    //kārtas skaits ievietošanai jaunā aģenta vārdā
    int getAgentNumber() {
        return ++agentNumber;
    }

    public synchronized int[] getIncomingPartDist() {
        if (lastIncPartDistCaptureTime == simTime) {
            return null;
        }
        lastIncPartDistCaptureTime = simTime;
        return incomingPartDist;
    }

    static synchronized void retoolingIncrement() {
        noOfRetoolings++;
    }

    static synchronized int getNoOfRetoolings() {
        return noOfRetoolings;
    }

    Simulation(Controller controller) {
        this.controller = controller;
        controller.workingAgentsListView.setItems(agents);
        controller.newTasksListView.setItems(tasks);
        controller.finishedTasksListView.setItems(finishedTasks);
        timeline = new Timeline(new KeyFrame(Duration.millis(simStepDefDuration), ae -> simulationStep()));
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
            Arrays.fill(incomingPartDist, 0);
            if (task != null) {
                tasks.add(task);
                incomingPartDist[task.taskType.ordinal()]++;// counts incoming part distribution(currently no more than one part per iteration)
            }
        } else
            beltStopedTime++;
        //lai nepārpildītos rinda- patur tikai pedējos pievienotos elementus
        if (finishedTasks.size() > FINISHED_TASKS_SIZE) {
            queue.removeFirst();
        }
        beltStopedFactor = 100d * beltStopedTime / simTime;
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
        p.setParameter(Profile.SERVICES, "jade.core.messaging.TopicManagementService;jade.core.event.NotificationService");

        //p.setParameter(Profile.SERVICES,"TopicManagement");
        // Create a new non-main container, connecting to the default
// main container (i.e. on this host, port 1099)
        ContainerController cc = rt.createMainContainer(p);
        return cc;
    }

    void createAgent(String initialBehaviour, int[] speed,int[] energyCons) {
        if (cc != null) {
            // Create a new agent, a DummyAgent
// and pass it a reference to an Object
            Object reference = new Object();
            Object args[] = new Object[]{reference, tasks, agents, finishedTasks, initialBehaviour, this,speed,energyCons};

            try {
                AgentController dummy = cc.createNewAgent("ViaBot " + getAgentNumber(),
                        "sample.ViaBot", args);
// Fire up the agent
                dummy.start();

                agents.add(new AgentInfo(dummy.getName(), simTime, dummy, speed));//[]a,b,c,s2,s3,s4
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
            Object args[] = new Object[]{reference, controller};

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

    public synchronized void moveToFinishedList(ViaBot viaBot) {
        int finishedTaskIndex = tasks.indexOf(viaBot.currentTask);
        if (finishedTaskIndex >= 0) {
            viaBot.currentTask.timeFinished = simTime;

            finishedTasks.add(tasks.get(finishedTaskIndex));
            tasks.remove(finishedTaskIndex);
            viaBot.currentTask = null;

        } else
            System.out.println("finished task " + viaBot.currentTask.id + " not found in task list ");
    }

    public synchronized void assignTask(ViaBot viaBot) {

        //  for (int i = 0; i < taskList.size(); i++) {
        if (tasks.size() > 0) {
            //Task task = (Task) taskList.get(taskList.size() - 1);// pārbauda tikai vecāko uzdevumu

            for (Task task : tasks) {//pārbauda visus uzdevumus sarakstā
                // Task task = (Task) taskList.get(taskList.size() - 1);


                if (task.taskType.equals(viaBot.assignedTaskType) && !task.isStarted) {
                    viaBot.currentTask = task;
                    viaBot.currentTask.isStarted = true;
                    viaBot.currentTask.timeStarted = simTime;
                    viaBot.isWorking = true;
                    viaBot.agentState = AgentState.WORKING;
                    break;
                }
            }
        }
    }

    public synchronized void updateS1Distribution(int[] s1Distribution) {
        Arrays.fill(s1Distribution, 0);
        for (AgentInfo info : agents) {
            if (info.isS1) {
                s1Distribution[info.asignedTaskType.ordinal()]++;
            }
        }
    }

    // counts, how many tasks of each type currently is available
    public synchronized int[] getCurTaskDistribution(int[] dist) {
        Arrays.fill(dist, 0);
        for (Task task : tasks) {
            dist[task.taskType.ordinal()]++;
        }
        return dist;
    }


    public synchronized String nameOfLeastValuedS1(TaskType taskType) {
        int index = -1;
        double minSpeedSoFar = Double.MAX_VALUE;

        for (int i = 0; i < agents.size(); i++) {
            if (agents.get(i).isS1 && agents.get(i).asignedTaskType.equals(taskType) && agents.get(i).getSpeed(taskType) < minSpeedSoFar)
                index = i;
        }
        if (index > -1)
            return agents.get(index).name;
        else
            return null;
    }

    public synchronized int[] speedsOfLeastValuedS1(TaskType taskType, int[] buf) {
        int index = -1;
        double minSpeedSoFar = Double.MAX_VALUE;

        for (int i = 0; i < agents.size(); i++) {
            if (agents.get(i).isS1 && agents.get(i).asignedTaskType.equals(taskType) && agents.get(i).getSpeed(taskType) <= minSpeedSoFar)
                index = i;
        }
        if (index > -1) {
            buf[0] = agents.get(index).getSpeedA();
            buf[1] = agents.get(index).getSpeedB();
            buf[2] = agents.get(index).getSpeedC();

            return buf;
        } else
            return null;//no agents
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
