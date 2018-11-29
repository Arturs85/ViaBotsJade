package sample;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskGenerator {

    List<DistributionWTime> taskDistributions=new ArrayList();
    private static final Random RANDOM = new Random();
    int taskRate =70;
    int periodLocalCounter=0;
    int periodLength=0;
  public int[] curentPeriodTaskDistribution= new int[]{1,1,1};
   public TaskGenerator(){
        taskDistributions.add(new DistributionWTime(new int[]{50,30,20},0,200));
        taskDistributions.add(new DistributionWTime(new int[]{10,20,70},200,400));
        taskDistributions.add(new DistributionWTime(new int[]{30,40,30},400,600));
        taskDistributions.add(new DistributionWTime(new int[]{10,20,70},600,800));
periodLength=periodSum();
    }

   int periodSum(){
       int sum=0;
        for (DistributionWTime d :
               taskDistributions) {
           sum+=d.timeEnd-d.timeStart;
       }
        return sum;
   }

  void getCurrentPeriodTaskDistribution(){
       for (DistributionWTime d :
               taskDistributions) {
           if(d.timeStart<periodLocalCounter&&d.timeEnd>=periodLocalCounter)
           curentPeriodTaskDistribution=d.tasksDistribution;
       }
   }

 public    Task simulationStep(int stepNr){
        if(periodLocalCounter>periodLength){
            periodLocalCounter=0;
        }

        getCurrentPeriodTaskDistribution();
        periodLocalCounter++;
        if (RANDOM.nextInt(100)<taskRate)
            return new Task(stepNr,curentPeriodTaskDistribution);
        else return null;

    }
}
