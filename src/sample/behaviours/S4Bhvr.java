package sample.behaviours;

import sample.AgentState;
import sample.ViaBot;

public class S4Bhvr extends BaseBhvr {
    public S4Bhvr(ViaBot a, long period) {
        super(a, period);
    }
int s4count=0;
    ExecutiveBehaviourType behaviourType=ExecutiveBehaviourType.S4;


   @Override
    protected void onTick() {
        super.onTick();
        System.out.println(" s4c "+s4count++);

        //battery discharge
       if (owner.agentState == AgentState.WORKING) {
           boolean isBatOk = owner.dischargeBattery(behaviourType);
           if (!isBatOk) {
               owner.setToCharge();
           }
       }
//

   }

}
