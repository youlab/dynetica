/*
 * GraphNode.java
 *
 * Created on September 3, 2001, 4:33 PM
 */

package dynetica.gui.visualization;

/**
 * 
 * @author Lingchong You
 * @version 0.1
 */
public interface GraphNode extends dynetica.entity.Editable {
    String getNodeName();

    void setLocation(double x, double y);
    // void draw(java.awt.Graphics2D g);
    // void redraw();
}
