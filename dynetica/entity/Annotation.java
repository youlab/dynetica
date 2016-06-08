/*
 * Annotation.java
 *
 * Created on May 7, 2005, 2:25 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package dynetica.entity;

import dynetica.exception.*;

/**
 * 
 * @author Lingchong You
 */
public class Annotation extends Entity {

    /** Creates a new instance of Annotation */
    public Annotation() {
        this(null, null);
    }

    public Annotation(dynetica.system.AbstractSystem s, String name) {
        this.system = s;
        if (name != null) {
            this.name = name;
        } else
            name = " Annotation";
    }

    @Override
    // Modified by Kanishk Asthana 28 June 2013
    public void setProperty(String propertyName, String propertyValue)
            throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.equalsIgnoreCase("annotation")) {
            setAnnotation(propertyValue);
        } else if (this.getSystem() instanceof dynetica.system.AbstractModule) {
            if (propertyName.compareToIgnoreCase("X") == 0) {
                ((dynetica.system.AbstractModule) this.getSystem()).setX(Double
                        .parseDouble(propertyValue));
            } else if (propertyName.compareToIgnoreCase("Y") == 0) {
                ((dynetica.system.AbstractModule) this.getSystem()).setY(Double
                        .parseDouble(propertyValue));
            }
        } else
            throw new UnknownPropertyException(
                    "Unknown property for Substance " + getName() + ":"
                            + propertyName);
        if (getSystem() != null)
            getSystem().fireSystemStateChange();
    }

    public javax.swing.JPanel editor() {
        return new dynetica.gui.entities.AnnotationEditor(this);
    }

    public dynetica.gui.visualization.AbstractNode getNode() {
        // no need to show Annotation in the graph
        return null;
    }

    // Modified by Kanishk Asthana 28 June 2013
    public String getCompleteInfo() {
        StringBuffer sb = new StringBuffer(getFullName() + " {" + NEWLINE
                + " Annotation { " + getAnnotation() + " }" + NEWLINE);

        if (this.getSystem() instanceof dynetica.system.AbstractModule) {
            dynetica.system.AbstractModule system = (dynetica.system.AbstractModule) this
                    .getSystem();
            sb.append(" X  {" + system.getX() + "}" + NEWLINE + " Y  {"
                    + system.getY() + "}" + NEWLINE);
        }

        sb.append("}" + NEWLINE);
        return sb.toString();
    }

    public Object clone() {
        return null;
    }
}
