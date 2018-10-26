package sample;

public class Battery {
    public enum BatteryState {FULL, CHARGING, EMPTY, DISCHARGING}

public     int energyLeft = 100;
    int maxEnergyCapacity = 100;
    int chargePerSecond = 5;//20 iterations to full charge
    BatteryState state = BatteryState.FULL;

    /**
     * @return false if battery is full
     */
   public boolean charge() {
        state = BatteryState.CHARGING;
        energyLeft += chargePerSecond;

        if (energyLeft >= maxEnergyCapacity) {
            energyLeft = maxEnergyCapacity;
            state = BatteryState.FULL;
            return false;
        }
        return true;
    }

    /**
     * @param amount amount of energy to remove from battery
     * @return false, if battery is empty, otherwise true
     */
  public   boolean discharge(double amount) {

        energyLeft -= amount;
   //   System.out.println("en "+energyLeft);
        state = BatteryState.DISCHARGING;
        if (energyLeft <= 0) {
            energyLeft = 0;
            state = BatteryState.EMPTY;
            return false;
        }
        return true;
    }
}
