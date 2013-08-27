//: Entity.java 
/**
 *  @author begin: Mon Mar 18 2000
 *  @author copyright: (C) 2000 by Lingchong You
 *  @author Department of Chemical Engineering
 *  @author University of Wisconsin-Madison
 *  @author email: you@cae.wisc.edu
 *  @version 1.0
 */
package dynetica.entity;

import dynetica.gui.visualization.AbstractNode;
import dynetica.gui.entities.EntityEditor;
import dynetica.util.*;
import dynetica.system.*;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.gui.*;

abstract public class Entity implements SystemProperties, Editable, Cloneable {
    // protected static int entityIndex = 0;
    /**
     * @param name
     *            the name of the entity
     * @param the
     *            supersystem of the entity Note: when an Entity is constructed
     *            this way, the system automatically registers it into its
     *            HashMap.
     */
    public Entity(String name, AbstractSystem ss) {
        this.name = name;
        this.system = ss;
        if (ss != null)
            ss.add(this);
    }

    public Entity(String name) {
        this.name = name.trim();
    }

    public Entity() {
        // this("Entity" + entityIndex++);
    }

    //
    // Tentatively added by Lingchong You on 2/10/2000 to facilitate
    // the processing of properties of an object. Need to find out
    // whether Java already provides something like this.
    //
    // it may be better to define this method in an interface.
    public void setProperty(String propertyName, String propertyValue)
            throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.compareTo(name) == 0)
            name = propertyValue;
        else
            throw new UnknownPropertyException(propertyName);
    }

    /**
     * check whether a character is suitable for being used as Entity name
     */
    public static boolean isLegalCharacterForEntityName(char c) {
        return (Character.isLetterOrDigit(c));
    }

    public final boolean hasSystem() {
        return (system != null);
    }

    public AbstractSystem getSystem() {
        return system;
    }

    public void setSystem(AbstractSystem ss) {
        system = ss;
        if (system != null)
            system.add(this);
    }

    /**
     * reset the entity back to its initial state
     */
    // this appears to be better defined as an abstract method.
    public void reset() {
        // int i = 0;
        // System.out.println("Reseting " + getName());
    }

    public String toString() {
        //
        // Modified by Lingchong You on 2/9/2001.
        //
        return getFullName();
    }

    /**
     * The super system of the entity.
     */
    protected AbstractSystem system;

    /** Holds value of property annotation. */
    protected String annotation = "";

    /** Holds value of property visible. */
    private boolean visible = true;

    /** Holds value of property name. */
    protected String name;

    /** Holds value of property toPrint. */
    private boolean toPrint = true;

    /**
     * Holds value of property x, y.
     */
    private double x = 0.0d;
    private double y = 0.0d;

    /**
     * Holds value of property node.
     */
    protected AbstractNode node;

    /**
     * gets the full qualified name of the entity.
     */
    //
    // Added by Lingchong You on 2/9/2001
    //
    public String getFullName() {
        StringBuffer str = new StringBuffer(getClass().getName() + "   ");
        // if (hasSystem())
        // str.append(system.getName() + "::" + name);
        // return(system.getName() + "::" + name);
        // else
        str.append(name);

        return (str.toString());
    }

    /**
     * Getter for property annotation.
     * 
     * @return Value of property annotation.
     */
    public String getAnnotation() {
        return annotation;
    }

    /**
     * Setter for property annotation.
     * 
     * @param annotation
     *            New value of property annotation.
     */
    public void setAnnotation(String annotation) {
        // Note by Lingchong You. August 6, 2002.
        // the replacement is a hack so that the annotation is on a single line,
        // such that the
        // system builder can properly read it. need to modify the system
        // builder.
        //
        this.annotation = annotation;
    }

    public javax.swing.JPanel editor() {
        return new EntityEditor(this);
    }

    /**
     * Getter for property visible.
     * 
     * @return Value of property visible.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Setter for property visible.
     * 
     * @param visible
     *            New value of property visible.
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Getter for property name.
     * 
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }

    public String getShortClassName() {
        String className = getClass().getName();
        return className.substring(className.lastIndexOf('.') + 1);
    }

    /**
     * Setter for property name.
     * 
     * @param name
     *            New value of property name.
     */
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        if (hasSystem())
            getSystem().rename(oldName, name);
    }

    /**
     * Getter for property toPrint.
     * 
     * @return Value of property toPrint.
     */
    public boolean isToPrint() {
        return toPrint;
    }

    /**
     * Setter for property toPrint.
     * 
     * @param toPrint
     *            New value of property toPrint.
     */
    public void setToPrint(boolean toPrint) {
        this.toPrint = toPrint;
    }

    /**
     * Getter for property x.
     * 
     * @return Value of property x.
     */
    public double getX() {

        return this.x;
    }

    /**
     * Setter for property x.
     * 
     * @param x
     *            New value of property x.
     */
    public void setX(double x) {

        this.x = x;
    }

    /**
     * Getter for property y.
     * 
     * @return Value of property y.
     */
    public double getY() {
        return this.y;
    }

    /**
     * Setter for property y.
     * 
     * @param y
     *            New value of property y.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Getter for property node.
     * 
     * @return Value of property node.
     */
    public AbstractNode getNode() {
        return null;
    }

    abstract public Object clone();
}
