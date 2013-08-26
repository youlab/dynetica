/**
 * Parameter.java
 *
 *
 * Created: Mon Aug 14 20:58:22 2000
 * Revised: 5/2/2005. Defined method Object clone();
 *
 * @author Lingchong You
 * @version 0.1
 */
package dynetica.entity;
import dynetica.reaction.*;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.util.*;
import dynetica.system.*;
import java.util.*;

public class Parameter extends EntityVariable {
    private static int parameterIndex = 0;
    /** Holds value of property defaultValue. */
    private double defaultValue;
    private double storedValue;
    
    /**
     *the field <code> reactions </code> stores a list of reactions that the Substance is involved in 
     */
    List reactions = new ArrayList();
    
    //
    // this method is automatically called from within Reaction when a Reaction object
    // is being initialized. How can I check whether a parameter is used in a certain
    // reaction?
    //
    public void addReaction(Reaction r) {
	reactions.add(r);
    }
    
    /*
     * remove the specified reaction from the list of reactions that this parameter is involved in.
     * This method should be used when the specified reaction is removed from a system.
     */
    public void removeReaction(Reaction r) {
	reactions.remove(r);
    }

    public boolean hasReactions() {
	return (! reactions.isEmpty());
    }

    public Parameter() {
	this("Parameter" + parameterIndex++, null);
    }
    
    public Parameter(String name, AbstractSystem system) {
	this(name, system, 0.0, 0.0, Double.MAX_VALUE);
        storedValue = value;
    }
    
    public Parameter(String name,
		     AbstractSystem system,
                     double defaultValue,
		     double min,
		     double max) {
        super(name, system, defaultValue, min, max);
        storedValue = value;
        setDefaultValue(defaultValue);
    }

    /** Getter for property defaultValue.
     * @return Value of property defaultValue.
     */
    public double getDefaultValue() {
        return defaultValue;
    }
    /** Setter for property defaultValue.
     * @param defaultValue New value of property defaultValue.
     */
    public void setDefaultValue(double defaultValue) {
        this.defaultValue = defaultValue;
        if (getSystem() != null) getSystem().fireSystemStateChange();
    }
    
    public void setValue(double  v) {
        super.setValue(v);
        storedValue = v;
        if (getSystem() != null) getSystem().fireSystemStateChange();
    }
    
    public void setSearchingValue(double v) {
        super.setValue(v);
        if (getSystem() != null) getSystem().fireSystemStateChange();
    }
    
    public void resetSearchingValue() {
        setValue(storedValue);
    }
    
    public void reset() {
 //       super.reset();
 //       setValue(defaultValue);
    }
    
     public String getCompleteInfo() {
        return (  getFullName() + " {" +  NEWLINE + 
        " Value { " + getValue() + "}" + NEWLINE +
        " Min {" + getMin() + "}" + NEWLINE + 
        " Max {" + getMax() + "}" + NEWLINE +
        " Annotation { " + getAnnotation() + "}" + NEWLINE +
              "}" + NEWLINE);
    }
   
    public void setProperty(String propertyName,String propertyValue) throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.compareToIgnoreCase("Max") == 0)
            setMax(Double.parseDouble(propertyValue));
        else if (propertyName.compareToIgnoreCase("Min") == 0)
            setMin(Double.parseDouble(propertyValue));
        else if (propertyName.compareToIgnoreCase("Value") == 0) {
            setDefaultValue(Double.parseDouble(propertyValue));
            setValue(Double.parseDouble(propertyValue));
         }
        
        //
        // Added by Lingchong You on August 06, 2002
        // Allows user to annotate the parameter
        //
       else if (propertyName.equalsIgnoreCase("annotation")) {
            setAnnotation(propertyValue);
       }
       else
            throw 
            new UnknownPropertyException("Unknown property for Parameter " + propertyName + " " + propertyValue);    
}
    
    public javax.swing.JPanel editor() {
        return new dynetica.gui.entities.ParameterEditor(this);
    }

    public Object clone(){
        Parameter p = new Parameter(this.name +"_clone", this.system);
        p.setValue(this.getValue());
        p.setDefaultValue(this.getDefaultValue());
        p.setMax(this.getMax());
        p.setMin(this.getMin());
        return p;
    }
} // Parameter
