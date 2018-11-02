package sample;

import java.util.Arrays;
import java.util.LinkedList;

public class Statistics {

    LinkedList<int[]> history = new LinkedList<int[]>();


    int noOfSamples = 80;
    int[] sums = new int[]{0, 0, 0};
    public int[] avg = new int[]{0, 0, 0};

    public void addNewFrame(int[] tasksDistr) {
      if(tasksDistr==null) return;
        if (history.size() >= noOfSamples) {
            //remove oldest frame from sums
            for (int i = 0; i < sums.length; i++) {
                sums[i] -= history.getFirst()[i];
            }

            history.removeFirst();

        }
        history.addLast(Arrays.copyOf(tasksDistr, tasksDistr.length));
        //add newest frame to sums
        for (int i = 0; i < sums.length; i++) {
            sums[i] += history.getLast()[i];
        }

    }

    public int[] calcAverageDist() {
        int total = sumOfsums();

        for (int i = 0; i < sums.length; i++) {
            if (total != 0)
                avg[i] = (sums[i] * 100) / total;
            else
                avg[i] = 0;
        }

        return avg;
    }

    int sumOfsums() {

        return Arrays.stream(sums).sum();

    }


}
