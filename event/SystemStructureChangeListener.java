/*
 * SystemStructureChangeListener.java
 *
 * Created on September 8, 2001, 2:35 PM
 */

package dynetica.event;

/**
 * 
 * @author You
 * @version
 */
public interface SystemStructureChangeListener extends java.util.EventListener {
    public void systemStructureChanged(SystemStructureChangeEvent e);
}
