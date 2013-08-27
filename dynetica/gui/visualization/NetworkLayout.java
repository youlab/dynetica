/*
 * NetworkLayout.java
 *
 * Created on April 20, 2005, 12:03 PM
 */

package dynetica.gui.visualization;

import dynetica.gui.visualization.AbstractNode;
import dynetica.system.ReactiveSystem;
import dynetica.exception.*;
import java.util.*;

/**
 * 
 * @author lingchong you
 */
public class NetworkLayout extends dynetica.entity.Entity {
    //
    // graph layout information
    //
    public double arrowSize = 8.0;
    public int width = 500;
    public int height = 400;
    public int margin = 20;
    public float lineWidth = 0.5f;

    /** Creates a new instance of NetworkLayout */
    public NetworkLayout(String name, ReactiveSystem system) {
        // currently the network layout will ignore any user-defined name.
        super("Layout", system);
    }

    public String getCompleteInfo() {
        StringBuffer sb = new StringBuffer(getFullName() + " {" + NEWLINE
                + "\t width { " + width + "}" + NEWLINE + "\t height {"
                + height + "}" + NEWLINE + "\t arrowSize {" + arrowSize + "}"
                + NEWLINE + "\t margin {" + margin + "}" + NEWLINE
                + "\t lineWidth {" + lineWidth + "}" + NEWLINE);
        sb.append("}" + NEWLINE);
        return sb.toString();
    }

    public void setProperty(String propertyName, String propertyValue)
            throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.compareToIgnoreCase("width") == 0)
            width = Integer.parseInt(propertyValue);
        else if (propertyName.compareToIgnoreCase("height") == 0)
            height = Integer.parseInt(propertyValue);
        else if (propertyName.compareToIgnoreCase("arrowSize") == 0)
            arrowSize = Double.parseDouble(propertyValue);
        else if (propertyName.compareToIgnoreCase("lineWidth") == 0)
            lineWidth = Float.parseFloat(propertyValue);
        else if (propertyName.compareToIgnoreCase("margin") == 0) {
            margin = Integer.parseInt(propertyValue);
        } else
            throw new UnknownPropertyException(
                    "Unknown property for Substance " + getName() + ":"
                            + propertyName);
        if (getSystem() != null)
            getSystem().fireSystemStateChange();
    }

    public AbstractNode getNode() {
        return null;
    }

    public Object clone() {
        return null;
    }
}
