/*
 * EquilibratedMassAction.java
 *
 * Created on August 23, 2000, 1:41 PM
 */

package dynetica.reaction;

import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.system.*;
import java.util.*;

/**
 * 
 * @author Lingchong You
 * @version 0.01
 */
public class EquilibratedMassAction extends EquilibratedReaction {

    /**
     * Holds value for the equilibrium constant kExpression. This constant could
     * actually be a complexity algebraic expression
     */
    GeneralExpression kExpression;

    /**
     * the equilibrium constant expressed in a string.
     */
    String k;

    //
    // these two arrays are created to facilitate updating of the reaction.
    // they will be initialized or modifited when one more more properties
    // are changed.
    //
    double[] reactantValues;
    double[] reactantCoefficients; // the coefficients of the reactants
    double[] productValues;
    double[] productCoefficients; // the coefficients of the products

    /** Creates new EquilibratedMassAction */
    public EquilibratedMassAction() {
        super();
    }

    public EquilibratedMassAction(String name, ReactiveSystem system) {
        super(name, system);
    }

    public void reset() {
        reactantValues = new double[reactants.size()];
        reactantCoefficients = new double[reactants.size()];

        //
        // note only the coefficients of reactants and products are initialized
        // here.
        //
        for (int i = 0; i < reactantValues.length; i++)
            // converted to positive values
            reactantCoefficients[i] = -getCoefficient((Substance) reactants
                    .get(i));

        productValues = new double[products.size()];
        productCoefficients = new double[products.size()];

        for (int i = 0; i < productValues.length; i++)
            productCoefficients[i] = getCoefficient((Substance) products.get(i));
    }

    //
    // Added by Lingchong You, 2/11/2000
    //
    public void setProperty(String propertyName, String propertyValue)
            throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.compareToIgnoreCase("K") == 0) {
            try {
                setK(propertyValue);
            } catch (IllegalExpressionException iee) {
                throw new InvalidPropertyValueException(iee.getMessage());
            }
        } else if (propertyName.compareToIgnoreCase("stoichiometry") == 0) {
            // System.out.println("OK here!");
            setStoichiometry(propertyValue);
        }
        // the following two IF statements were added 4/19/05 by LY
        else if (propertyName.compareToIgnoreCase("X") == 0) {
            setX(Double.parseDouble(propertyValue));
        } else if (propertyName.compareToIgnoreCase("Y") == 0) {
            setY(Double.parseDouble(propertyValue));
        }

        else {
            System.out.println(propertyName);
            throw new UnknownPropertyException(
                    "Unknown property for EquilibratedMassAction:"
                            + propertyName);
        }
    }

    /** Updates the concentration of all the reactants and products */
    //
    // Updated by Lingchong You 2/11/2001.
    //
    // Note:
    // the generic implemenation of this method will be very difficult:
    // the appropriate way is to update multiple equlibratiing reactions
    // simultaneously by solving all the equlibrium equations.
    //
    // The following algorithm calculated the extent of this particular reaction
    // based on the current concentrations of the reacting species, using a
    // trial
    // and error method.
    //
    public void update() {
        double kvalue = kExpression.getValue();
        // System.out.println(kvalue);

        // the extent of reaction is between minReactantEps and -minProductEps.
        double minReactantEps = Double.MAX_VALUE; // =
                                                  // min([Product_i]/[Product_i_coeff])
        double minProductEps = Double.MAX_VALUE; // =
                                                 // min([Reactant_i]/[Reactant_i_coeff]);

        for (int i = 0; i < reactantValues.length; i++) {
            reactantValues[i] = ((Substance) reactants.get(i)).getValue();
            minReactantEps = Math.min(minReactantEps, reactantValues[i]
                    / reactantCoefficients[i]);
        }

        for (int j = 0; j < productValues.length; j++) {
            productValues[j] = ((Substance) products.get(j)).getValue();
            minProductEps = Math.min(minProductEps, productValues[j]
                    / productCoefficients[j]);
        }

        double maxEps = minReactantEps;
        double minEps = -minProductEps;
        // System.out.println(maxEps);
        // System.out.println(minEps);

        double eps = 0;
        while (maxEps - minEps > 1.0e-6 * (minReactantEps + minProductEps)) {
            eps = (maxEps + minEps) / 2.0;
            // System.out.println(eps);
            double kprime = 1.0;
            for (int i = 0; i < productValues.length; i++)
                kprime *= Math.pow((productValues[i] + eps
                        * productCoefficients[i]), productCoefficients[i]);
            for (int i = 0; i < reactantValues.length; i++)
                kprime *= Math.pow((reactantValues[i] - eps
                        * reactantCoefficients[i]), -reactantCoefficients[i]);

            if (kprime > kvalue) {// eps is too large
                maxEps = eps;
            } else if (kprime < kvalue) { // eps is too small
                minEps = eps;
            } else
                break;
        }

        for (int i = 0; i < reactantValues.length; i++)
            ((Substance) reactants.get(i)).setValue(reactantValues[i]
                    - reactantCoefficients[i] * eps);

        for (int i = 0; i < productValues.length; i++)
            ((Substance) products.get(i)).setValue(productValues[i]
                    + productCoefficients[i] * eps);

    }

    /**
     * Getter for property equilibrationConstant.
     * 
     * @return Value of property equilibrationConstant.
     */
    public String getK() {
        return k;
    }

    public void setK(String kString) throws IllegalExpressionException {
        this.k = kString;
        try {
            this.kExpression = ExpressionParser
                    .parse(this.getSystem(), kString);
        } catch (IllegalExpressionException iee) {
            throw iee;
        }

    }

    public String toString() {
        return (getFullName() + "{" + NEWLINE + " stoichiometry {"
                + getStoichiometry() + "}" + NEWLINE + " K {" + getK() + "}"
                + NEWLINE
                +
                // the following two 'addition' were added 4/19/05 by LY
                "  X  {" + getX() + "}" + NEWLINE + "  Y  {" + getY() + "}"
                + NEWLINE +

        "}");
    }

    public javax.swing.JPanel editor() {
        return new dynetica.gui.reactions.EquilibratedMassActionEditor(this);
    }

    // Added 5/2/2005 by L You
    /* Creates a clone of this object */

    public Object clone() {
        EquilibratedMassAction ema = new EquilibratedMassAction(this.name
                + "_clone", (ReactiveSystem) this.system);
        ema.setAnnotation(annotation);
        try {
            ema.setK(this.k);
        } catch (Exception e) {
        }
        ema.setStoichiometry(this.getStoichiometry());
        ema.setX(getX() + 1);
        ema.setY(getY() + 1);
        return ema;
    }
}
