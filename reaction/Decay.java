/*
 * Decay.java
 *
 * Created on April 15, 2001, 3:17 PM
 */

package dynetica.reaction;

import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.system.*;
import dynetica.exception.*;

/**
 * Decay reaction describes the following simple reaction: stoichiometry: A ->
 * kinetics: k [A]
 * 
 * @author Lingchong You
 * @version 0.1
 */
public class Decay extends ProgressiveReaction {
    Substance substrate;
    GeneralExpression k; // the decay rate constant

    /** Creates new Decay */
    public Decay() {
    }

    public Decay(String name, ReactiveSystem system, Substance s,
            GeneralExpression k) {
        super(name, system);
        setSubstrate(s);
        setK(k);
    }

    /**
     * Getter for property substrate.
     * 
     * @return Value of property substrate.
     */
    public Substance getSubstrate() {
        return substrate;
    }

    /**
     * Setter for property substrate.
     * 
     * @param substrate
     *            New value of property substrate.
     */
    public void setSubstrate(Substance substrate) {
        remove(this.substrate);
        this.substrate = substrate;
        addSubstance(substrate, -1.0);
        if (k != null && substrate != null)
            setRateExpression(DMath.multiply(k, substrate));
    }

    /**
     * Getter for property k.
     * 
     * @return Value of property k.
     */
    public GeneralExpression getK() {
        return k;
    }

    /**
     * Setter for property k.
     * 
     * @param k
     *            New value of property k.
     */
    public void setK(GeneralExpression k) {
        this.k = k;
        setRateExpression(DMath.multiply(k, substrate));
    }

    public String toString() {
        return (getFullName() + " {" + NEWLINE + "  Substrate { "
                + getSubstrate().getName() + "}" + NEWLINE + " k {"
                + getK().toString() + "}" + NEWLINE
                +
                // the following two 'addition' were added 4/19/05 by LY
                "  X  {" + getX() + "}" + NEWLINE + "  Y  {" + getY() + "}"
                + NEWLINE +

                "}" + NEWLINE);
    }

    public void setProperty(String propertyName, String propertyValue)
            throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.compareToIgnoreCase("k") == 0) {
            try {
                setK(ExpressionParser.parse(this.getSystem(), propertyValue));
            } catch (IllegalExpressionException iee) {
                throw new InvalidPropertyValueException(iee.getMessage());
            }
        } else if (propertyName.compareToIgnoreCase("substrate") == 0) {
            // System.out.println("OK here!");
            if (getSystem().contains(propertyValue)) {
                setSubstrate(getSystem().getSubstance(propertyValue));
            } else {
                setSubstrate(new Substance(propertyValue, getSystem()));
            }
            // System.out.println("OK here after setting reaction equation!");
        }
        // the following two IF statements were added 4/19/05 by LY
        else if (propertyName.compareToIgnoreCase("X") == 0) {
            setX(Double.parseDouble(propertyValue));
        } else if (propertyName.compareToIgnoreCase("Y") == 0) {
            setY(Double.parseDouble(propertyValue));
        }

        else
            throw new UnknownPropertyException(
                    "Unknown property for ProgressiveReaction:" + propertyName);
    }

    public javax.swing.JPanel editor() {
        return new dynetica.gui.reactions.DecayEditor(this);
    }

    // added 5/2/2005 L. You
    /* Creates a clone of this reaction */
    public Object clone() {
        Decay d = new Decay(this.name + "_clone", (ReactiveSystem) this.system,
                substrate, k);
        return d;
    }
}
