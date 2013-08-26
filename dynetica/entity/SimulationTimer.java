/**
 * SimulationTimer.java
 *
 *
 * Created: Fri Sep 01 21:43:49 2000
 *
    *
    * This should be simplified in the future.
    * 
    *
 * @author Lingchong You
 * @version 0.01
 */
package dynetica.entity;
import dynetica.util.*;
import dynetica.system.*;

public class SimulationTimer extends Parameter{
    private static int timerIndex = 0;
    DoubleList timepoints = new DoubleList();

    public SimulationTimer() {
        this("Timer" + timerIndex++, null);
    }
    
    public SimulationTimer(String name, ReactiveSystem system) {
        super(name, system);
        setValue(0.0);
    }
    public double[] getTimePoints() {
	return timepoints.doubleValues();
    }
    public void setTimePoints(double[] timepoints) {
	this.timepoints.setValues(timepoints);
    }

    public void storeTimePoint() { timepoints.add(value); }
    public double getTimePoint(int i) {
	return timepoints.get(i);
    }
    
    @Override
    public void reset() { 
        super.reset();
	timepoints.clear(); 
	value = 0.0;
    }
    public void step(double dt) {value += dt;}
    public void setTime(double t) { value = t;}
    public double getTime() {return value;}

} // SimulationTimer
