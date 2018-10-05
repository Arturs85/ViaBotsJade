package sample;

import java.util.Arrays;
import java.util.Random;

public class Task {
    static int count = 1;


    public TaskType taskType;
    final int id;

    private static final TaskType[] VALUES = TaskType.values();
    private static final int SIZE = VALUES.length;
    private static final Random RANDOM = new Random();
    private static final int[] taskDistribution = new int[]{10, 10, 80}; //sum of values should be 100


    public static TaskType getRandomTask() {
        return VALUES[RANDOM.nextInt(SIZE)];
    }

    public static TaskType getRandomTaskFromDistribution() {
        int uniformRandValue = RANDOM.nextInt(Arrays.stream(taskDistribution).sum());
       // System.out.println("rndVal= "+uniformRandValue);
        if (uniformRandValue <= taskDistribution[0])
            return VALUES[0];
        else if (uniformRandValue > taskDistribution[0] && uniformRandValue <= taskDistribution[0]+taskDistribution[1])
            return VALUES[1];
//  default else if (uniformRandValue>taskDistribution[1])
        return VALUES[2];

    }


    public static final int FULL_PROGRESS = 100;
    int timeCreated;
    boolean isFinished = false;
    boolean isStarted = false;
    public int progress = 0;

    Task(int timeStep) {
        id = count++;
        //taskType = getRandomTask();
        taskType=getRandomTaskFromDistribution();
        this.timeCreated = timeStep;
    }


    @Override
    public String toString() {
        //    return super.toString();
        return "Part " + id + "  Type: " + taskType + " created: " + timeCreated + " Progress: " + progress;
    }

}
