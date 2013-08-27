/*
 * SimulationStateListener.java
 *
 * Created on November 12, 2001, 9:09 PM
 */

package dynetica.event;

/**
 * 
 * @author Lingchong You
 * @version 0.1
 */
public interface SimulationDoneEventListener extends java.util.EventListener {
    public void simulationDone(SimulationDoneEvent e);
}
