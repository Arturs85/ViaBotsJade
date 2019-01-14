package sample.behaviours;

import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import sample.Simulation;
import sample.ViaBot;

public class ManagingRoleChekerBhvr extends OneShotBehaviour {

    ViaBot owner;

    public ManagingRoleChekerBhvr(ViaBot owner) {
        super(owner);
        this.owner = owner;

    }

    @Override
    public void action() {
        checkManagingRoles();
    }


    //pievieno pirmo brīvo vadības lomu, paredzēta izsaukt no ManagingRoleCheckerBehaviour klases
    public synchronized void checkManagingRoles() {
        synchronized (ViaBot.lock){
        for (int i = 0; i < Simulation.managingRolesFilled.length; i++) {
            if (!Simulation.managingRolesFilled[i]) {
                owner.mAddBehaviour(Simulation.managingRoles[i]);
                break;
            }
        }
        }
    }
}
