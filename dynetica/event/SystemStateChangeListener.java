/*
 * SystemStateChangeListener.java
 *
 * Created on September 8, 2001, 1:34 PM
 */

package dynetica.event;

/**
 * 
 * @author Lingchong You
 * @version 0.1
 */
public interface SystemStateChangeListener extends java.util.EventListener {
    public void systemStateChanged(SystemStateChangeEvent e);
}
