public class TrafficController {

    // number of red cars on the bridge from left to right
    private int redCarsOnBridge = 0;
    // number of blue cars on the bridge from right to left
    private int blueCarsOnBridge = 0;

    public synchronized void enterLeft() {
        // wait as long as there are blue cars on the bridge
        while (blueCarsOnBridge > 0) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        // no blue cars, so red cars can enter
        redCarsOnBridge++;
    }

    public synchronized void enterRight() {
        // wait as long as there are red cars on the bridge
        while (redCarsOnBridge > 0) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        // no red cars, so blue cars can enter
        blueCarsOnBridge++;
    }

    public synchronized void leaveRight() {
        // red car leaves the bridge
        redCarsOnBridge--;

        // if it is the last red car, notify waiting blue cars
        if (redCarsOnBridge == 0) {
            notifyAll();
        }
    }

    public synchronized void leaveLeft() {
        // blue car leaves the bridge
        blueCarsOnBridge--;

        // if it is the last blue car, notify waiting red cars
        if (blueCarsOnBridge == 0) {
            notifyAll();
        }
    }
}