package sample.messageObjects;

import sample.Task;
import sample.TaskType;

import java.io.Serializable;

public class TransferS1MsgObj implements Serializable {
   public String s1Name;
   public TaskType desiredType;
   public Boolean isNeeded;

    public TransferS1MsgObj(String s1Name, TaskType desiredType, boolean isNeeded) {
        this.desiredType = desiredType;
        this.s1Name = s1Name;
        this.isNeeded = isNeeded;

    }

}
