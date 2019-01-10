package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskGenerator {

    List<DistributionWTime> taskDistributions = new ArrayList();
    private static final Random RANDOM = new Random();
    int taskRate = 70;
    int periodLocalCounter = 0;
    int periodLength = 0;
    public int[] curentPeriodTaskDistribution = new int[]{1, 1, 1};
    DistributionWTime curTaskDistribution; //for drawing
    DistributionWTime prevTaskDistribution; //for drawing

    public TaskGenerator() {
        taskDistributions.add(new DistributionWTime(new int[]{50, 30, 20}, 0, 400));
        taskDistributions.add(new DistributionWTime(new int[]{10, 20, 70}, 400, 800));
        taskDistributions.add(new DistributionWTime(new int[]{30, 40, 30}, 800, 1200));
        taskDistributions.add(new DistributionWTime(new int[]{10, 20, 70}, 1200, 1600));
        periodLength = periodSum();
        updateCurTaskDist();
    }

    int periodSum() {
        int sum = 0;
        for (DistributionWTime d :
                taskDistributions) {
            sum += d.timeEnd - d.timeStart;
        }
        return sum;
    }

    void updateCurTaskDist() {
        for (DistributionWTime d :
                taskDistributions) {
            if (d.timeStart < periodLocalCounter && d.timeEnd >= periodLocalCounter) {
                curentPeriodTaskDistribution = d.tasksDistribution;

                //if (curTaskDistribution != null)//for drawing
                    prevTaskDistribution = curTaskDistribution;
                //else
                  //  prevTaskDistribution = d;

                curTaskDistribution = d;

            }
        }
    }

    public Task simulationStep(int stepNr) {
        if (periodLocalCounter > periodLength) {
            periodLocalCounter = 0;
        }
      //  System.out.println("TaskGen period: "+periodLocalCounter);
        updateCurTaskDist();
        periodLocalCounter++;
        if (RANDOM.nextInt(100) < taskRate)
            return new Task(stepNr, curentPeriodTaskDistribution);
        else return null;

    }
public void reset(){
        periodLocalCounter = 0;

}
}
