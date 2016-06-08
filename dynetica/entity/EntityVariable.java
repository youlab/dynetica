/*
 * EntityVariable.java
 *
 * Created on September 28, 2000, 11:04 AM
 */

/**
 *
 * @author  Lingchong You
 * @version 1.0
 * @version 1.2 updated 4/19/2005
 */
package dynetica.entity;

import dynetica.system.*;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.util.*;

public class EntityVariable extends Entity implements Variable, ModelConstants {
    double max;
    double min;
    double value;

    boolean logScale; // used to specify how to change the values from min to
                      // max

    public EntityVariable() {
        this(null, null);
    }

    public EntityVariable(String name) {
        this(name, null);
    }

    public EntityVariable(String name, double baseValue, double min,
            double max, boolean log) {
        super(name, null);
        this.value = baseValue;
        this.min = min;
        this.max = max;
        this.logScale = log;
    }

    public EntityVariable(String name, AbstractSystem system) {
        this(name, system, 0.0);
    }

    public EntityVariable(String name, AbstractSystem system, double value) {
        this(name, system, value, 0.0, Double.MAX_VALUE);
    }

    public EntityVariable(String name, AbstractSystem system, double val,
            double min, double max) {
        super(name, system);
        this.max = max;
        this.min = min;
        this.value = val;
    }

    public int getType() {
        return Integer.MAX_VALUE;
    }

    /**
     * Get the value of value.
     * 
     * @return Value of value.
     */

    public double getValue() {
        return value;
    }

    /**
     * Set the value of value.
     * 
     * @param v
     *            Value to assign to value.
     */
    public void setValue(double v) {
        if (v < min)
            value = min;
        else if (v > max)
            value = max;

        //
        // Lingchong You
        // The following code is written to prevent setting NaN to the value. It
        // might cause some slowdown in simulations.
        //
        else if (v >= min && v <= max)
            value = v;
        else {
            //
            // This should not happen. This is used to avoid setting the value
            // to NaN
            System.out.println("Check model: Attempting to set NaN value to "
                    + this.getName());
        }
    }

    // LY notes (8/7/2013)
    // The field logScale and the corresponding methods are implemented to allow
    // the use of EntityVariables
    // for numerical analysis (not merely as foundation class for Parameter and
    // Substance).
    //
    public void setLogScale(boolean log) {
        logScale = log;
    }

    public boolean isLogScale() {
        return logScale;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public void setMin(double min) {
        this.min = min;
        if (getSystem() != null)
            getSystem().fireSystemStateChange();
    }

    public void setMax(double max) {
        this.max = max;
        if (getSystem() != null)
            getSystem().fireSystemStateChange();
    }
    
    // modified by Billy Wan Sep 2015 to throw IllegalExpressionException
    @Override
    public void setProperty(String propertyName, String propertyValue)
            throws UnknownPropertyException, InvalidPropertyValueException, 
            IllegalExpressionException{
        if (propertyName.compareTo("Max") == 0)
            max = Double.parseDouble(propertyValue);
        else if (propertyName.compareTo("Min") == 0)
            min = Double.parseDouble(propertyValue);
        else if (propertyName.compareTo("Value") == 0)
            value = Double.parseDouble(propertyValue);
        else
            throw new UnknownPropertyException(
                    "Unknown property for EntityVariable " + getName() + " "
                            + propertyName);
    }

    public String getCompleteInfo() {
        return (super.toString() + " {" + NEWLINE + "\t Value { " + getValue()
                + "}" + NEWLINE + "\t Min {" + getMin() + "}" + NEWLINE
                + "\t Max {" + getMax() + "}" + NEWLINE + "\t Annotation { "
                + getAnnotation() + "}" + NEWLINE + "}" + NEWLINE);
    }

    @Override
    public String toString() {
        return getName();

    }

    @Override
    public dynetica.gui.visualization.AbstractNode getNode() {
        return null;
    }

    public Object clone() {
        return null;
    }
}
