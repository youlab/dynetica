/*
 * MassAction.java
    * Lingchong You
 *
 * Created on August 23, 2000, 11:41 AM
    * last modified on 4/12/2001. 
 */

package dynetica.reaction;

import dynetica.entity.*;
import dynetica.system.*;
import dynetica.expression.*;
import dynetica.exception.*;
import java.util.*;

/**
 * MassAction represents reactions that follow
 * the simple mass action principle: the reaction
 * rate is proportional to the concentrations of
 * the reactants (to their respective powers).
 *
 * @author  you
 * @version 
 */
public class MassAction extends ProgressiveReaction {
    private static int massActionIndex = 0;
    /** Holds value of property rateConstant. */
    GeneralExpression k1 = new Constant(0.0);
    GeneralExpression k2 = new Constant(0.0);
    
    /** Creates new MassAction */
    public MassAction() {
        this("MassAction" + massActionIndex++, null);
    }
    
    public MassAction(String name, ReactiveSystem s) {
        setName(name);
        setSystem(s);
    }
    
    public void setStoichiometry(String s) {
        super.setStoichiometry(s);
        resetRateExpression();
    }
    
    private void resetRateExpression() {       
        GeneralExpression forward = null;
        if (k1 != null) {
            if (!(k1 instanceof Constant && k1.getValue() == 0.0)) {
                forward = k1;
                for (int i = 0; i < reactants.size(); i++) {
                    Substance s = (Substance) reactants.get(i);
                    double power = - getCoefficient(s);
                    if (power == 1.0) {
                        forward = DMath.multiply(forward, s);
                    }
                    else 
                        forward = DMath.multiply(forward, DMath.pow(s, new Constant(power)));
                }
            }
        }
        
        GeneralExpression backward = null;
        if (k2 != null) {
            if (!(k2 instanceof Constant && k2.getValue() == 0.0)) {
                backward = k2;
                for (int i = 0; i < products.size(); i++) {
                    Substance s = (Substance) products.get(i);
                    double power = getCoefficient(s);
                    if (power == 1.0) {
                        backward = DMath.multiply(backward, s);
                    }
                    else 
                        backward = DMath.multiply(backward, 
                              DMath.pow(s, new Constant(power)));
                }
            }
        }
        
        if (forward != null & backward != null) {
            setRateExpression(DMath.substract(forward, backward));
        }
        else if (forward != null) {
            setRateExpression(forward);
        }
        
        else if (backward != null) {
            setRateExpression(DMath.substract( new Constant(0.0), backward));
        }        
    }

    /** Getter for property rateConstant.
     * @return Value of property k.
     */
    public GeneralExpression getK1() {
        return k1;
    }
    /** Setter for property rateConstant.
     * @param rateConstant New value of property k.
     */
    public void setK1(GeneralExpression k) {
        this.k1 = k;
        resetRateExpression();
    }
    
    /** Getter for property k2.
     * @return Value of property k2.
     */
    public GeneralExpression getK2() {
        return k2;
    }    
    
    /** Setter for property k2.
     * @param k2 New value of property k2.
     */
    public void setK2(GeneralExpression k2) {
        this.k2 = k2;
        resetRateExpression();
    }
    
    public void setProperty(String propertyName, String propertyValue) throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.compareToIgnoreCase("k1") == 0) {
            try {
                setK1(ExpressionParser.parse(getSystem(), propertyValue));
            }
            catch (IllegalExpressionException iee) {
                throw new InvalidPropertyValueException(iee.getMessage());
            }
        }
        else if (propertyName.compareToIgnoreCase("k2") == 0) {
            try {
                setK2(ExpressionParser.parse(getSystem(), propertyValue));
            }
            catch (IllegalExpressionException iee) {
                throw new InvalidPropertyValueException(iee.getMessage());
            }
        }
       //the following two IF statements were added 4/19/05 by LY 
        else if (propertyName.compareToIgnoreCase("X") == 0) {
            setX(Double.parseDouble(propertyValue));
        }
        else if (propertyName.compareToIgnoreCase("Y") == 0) {
           setY(Double.parseDouble(propertyValue));
         }

        else {
            super.setProperty(propertyName, propertyValue);
        }
    }    
    
    public String toString() {
        return(getFullName() + "{" + NEWLINE + 
        "  stoichiometry { " + getStoichiometry() + " } " + NEWLINE +
        "  k1 { " + getK1() + " } " + NEWLINE +
        "  k2 { " + getK2() + " } " + NEWLINE +
                       // the following two 'addition' were added 4/19/05 by LY
        "  X  {" + getX()+"}" + NEWLINE+
        "  Y  {" + getY()+"}" + NEWLINE+              
 
               "}" );
    }
    
    public javax.swing.JPanel editor () {
        return new dynetica.gui.reactions.MassActionEditor(this);
    }

    //
    // Added 5/2/2005; create a deep copy of the current object.
    //
    public Object clone(){
        MassAction ma = new MassAction(this.name+"_clone", (ReactiveSystem) this.system);
        ma.setK1(k1);
        ma.setK2(k2);
        ma.setX(getX()+1);
        ma.setY(getY()+1);
        ma.setStoichiometry(this.getStoichiometry());
        return ma;
    }
}
