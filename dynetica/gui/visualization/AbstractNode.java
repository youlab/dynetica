/*
 * AbstractNode.java
 *
 * Created on September 16, 2001, 10:42 PM
 */

package dynetica.gui.visualization;

import dynetica.gui.DrawingConstants;

/**
 * 
 * @author You
 * @version 0.01
 */
public interface AbstractNode extends dynetica.entity.Editable,
        DrawingConstants {
    String getNodeName();

    void setLocation(double x, double y);

    double getX();

    double getY();

    void setX(double x);

    void setY(double y);

    void draw(java.awt.Graphics2D g);

    void erase(java.awt.Graphics2D g);

    void setChangeRatio(double size);

    double getWidth();

    double getHeight();

    boolean contains(double x, double y);

    double getCenterX();

    double getCenterY();

    void setTextVisible(boolean visible);

    dynetica.entity.Entity getEntity();

    boolean isSelected();

    void setSelected(boolean s);

    // Following methods were added by Kanishk Asthana on 28 August 2013 9:55pm
    boolean showInformationBox();

    void drawInformationBox(boolean draw);

}
