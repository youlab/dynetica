/*
 * Generation.java
 *
 * Created on April 15, 2001, 4:21 PM
 */

package dynetica.reaction;

import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.entity.*;
import dynetica.system.*;

/**
 * 
 * @author Lingchong You
 * @version 0.1
 */
public class Generator extends ProgressiveReaction {
    Substance product;

    /** Creates new Generation */
    public Generator() {
    }

    public Generator(String name, ReactiveSystem system, Substance p,
            GeneralExpression generationRate) {
        super(name, system);
        setProduct(p);
        setRateExpression(generationRate);
    }

    /**
     * Getter for property product.
     * 
     * @return Value of property product.
     */
    public Substance getProduct() {
        return product;
    }

    /**
     * Setter for property product.
     * 
     * @param product
     *            New value of property product.
     */
    public void setProduct(Substance product) {
        remove(this.product);
        addSubstance(product, 1.0);
        this.product = product;
    }

    public String toString() {
        return (getFullName() + " {" + NEWLINE + " Product { "
                + getProduct().getName() + "}" + NEWLINE + " Kinetics { "
                + getKinetics().toString() + "}" + NEWLINE
                +
                // the following two 'addition' were added 4/19/05 by LY
                "  X  {" + getX() + "}" + NEWLINE + "  Y  {" + getY() + "}"
                + NEWLINE +

                "}" + NEWLINE);
    }

    public void setProperty(String propertyName, String propertyValue)
            throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.compareToIgnoreCase("product") == 0) {
            if (getSystem().contains(propertyValue)) {
                setProduct(getSystem().getSubstance(propertyValue));
            } else {
                setProduct(new Substance(propertyValue, getSystem()));
            }
        }
        // the following two IF statements were added 4/19/05 by LY
        else if (propertyName.compareToIgnoreCase("X") == 0) {
            setX(Double.parseDouble(propertyValue));
        } else if (propertyName.compareToIgnoreCase("Y") == 0) {
            setY(Double.parseDouble(propertyValue));
        }

        else
            super.setProperty(propertyName, propertyValue);
    }

    public javax.swing.JPanel editor() {
        return new dynetica.gui.genetics.GeneratorEditor(this);
    }

    // Added 5/2/2005 by L. You
    /* creates a clone of this reaction */
    public Object clone() {
        Generator g = new Generator(this.name + "_clone",
                (ReactiveSystem) this.system, this.product, this.rateExpression);
        return g;
    }
}
