package sample;

public class ToolChanger {
int toolChangeTime =10;
int curProgress=0;
public boolean isChanging = false;
public TaskType nextTaskType;

public void startChange(TaskType nextTaskType){
    isChanging = true;
   this.nextTaskType = nextTaskType;

}
public boolean simStep() {//returns true if tool change just finished
   boolean justFinished = false;
    if (isChanging){
        curProgress++;
        if(curProgress>=toolChangeTime){
            curProgress =0;
            isChanging =false;
            justFinished = true;
        }

    }

return justFinished;
}
}
