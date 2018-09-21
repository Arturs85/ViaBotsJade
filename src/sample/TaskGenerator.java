package sample;

import java.util.Random;

public class TaskGenerator {
    private static final Random RANDOM = new Random();
int taskRate =70;
    Task simulationStep(int stepNr){
        if (RANDOM.nextInt(100)<taskRate)
            return new Task(stepNr);
        else return null;
    }
}
