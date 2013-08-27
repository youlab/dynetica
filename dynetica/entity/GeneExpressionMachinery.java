/*
 * GeneExpressionMachinery.java
 *
 * Created on August 14, 2001, 11:53 AM
 */

package dynetica.entity;

/**
 *
 * @author  Lingchong You
 * @version 0.01
 */
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.system.*;
import java.util.*;

public class GeneExpressionMachinery extends Protein {

    /** Holds value of property elongationRate. */
    private GeneralExpression elongationRate;

    /**
     * Spacing is the minimal distance (in NTPs) between neighboring RNAP along
     * a DNA.
     */
    private GeneralExpression spacing;

    /** Creates new GeneExpressionMachinery */
    public GeneExpressionMachinery() {
    }

    /**
     * Getter for property elongationRate.
     * 
     * @return Value of property elongationRate.
     */
    public GeneralExpression getElongationRate() {
        return elongationRate;
    }

    /**
     * Getter for property spacing.
     * 
     * @return Value of property spacing.
     */
    public GeneralExpression getSpacing() {
        return spacing;
    }

    /**
     * Setter for property elongationRate.
     * 
     * @param elongationRate
     *            New value of property elongationRate.
     */
    public void setElongationRate(GeneralExpression elongationRate) {
        this.elongationRate = elongationRate;
    }

    public void setElongationRate(String expression)
            throws IllegalExpressionException {
        try {
            elongationRate = ExpressionParser.parse(getSystem(), expression);
        } catch (IllegalExpressionException iee) {
            throw iee;
        }
    }

    /**
     * Setter for property spacing.
     * 
     * @param spacing
     *            New value of property spacing.
     */
    public void setSpacing(GeneralExpression spacing) {
        this.spacing = spacing;
    }

    public void setSpacing(String spacingString)
            throws IllegalExpressionException {
        try {
            spacing = ExpressionParser.parse(getSystem(), spacingString);
        } catch (IllegalExpressionException iee) {
            throw iee;
        }
    }

    public javax.swing.JPanel editor() {
        return new dynetica.gui.genetics.GeneExpressionMachineryEditor(this);
    }
}
