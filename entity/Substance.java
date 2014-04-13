/***************************************************************************
                          Substance.java  -  description
                             -------------------
    begin                : Mon Mar 18 2000
    copyright            : (C) 2000 by Lingchong You
    email                : you@cae.wisc.edu
 ***************************************************************************/
package dynetica.entity;

import dynetica.system.*;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.util.*;
import dynetica.reaction.*;
import java.util.*;

public class Substance extends EntityVariable {
    private static int substanceIndex = 0;
    /** Holds the value of the substance; i. e., the # of molecules */
    double initialValue = 0.0;
    double rate = 0.0;

    protected DoubleList values = new DoubleList();
    protected DoubleList rates = new DoubleList();

    // stores a list of reactions whose rates this Substance affects
    List reactions = new ArrayList();

    // a rate expression to indicate the rate of change in the concentration of
    // this substance
    GeneralExpression rateExpression;

    // Added on August 23 2006. This stores the "noise" term for each specices,
    // following Lagevin formulation
    GeneralExpression stochasticTerm;

    /**
     * Substance class describes the entities with an emphasis on their
     * quantities (copy number & concentration).
     */

    public Substance() {
        this("Substance" + substanceIndex++, null, 0.0);
    }

    public Substance(String name) {
        this(name, null, 0.0);
    }

    public Substance(String name, AbstractSystem system) {
        this(name, system, 0.0);
    }

    public Substance(String name, AbstractSystem system, double iv) {
        super(name, system);
        this.initialValue = iv;
    }

    public Substance(String name, AbstractSystem system, double iv, double min,
            double max) {
        super(name, system, iv, min, max);
        this.initialValue = iv;
        setToPrint(false);
    }

    /**
     * sets the rate expression for the substance directly, only if the
     * substance doesn't participate in any reaction
     * 
     * this is convenient when the user wants to write out the rate expressions
     * for the substances without writing out the reactions.
     */

    public void setRateExpression(String exprStr)
            throws IllegalExpressionException {
        try {
            if (rateExpression == null && !hasReactions()) {
                rateExpression = ExpressionParser.parse(getSystem(), exprStr);
            } else {
                System.out.println("WARNING: rate expression for: " + this
                        + " has already been set.");
            }
        } catch (IllegalExpressionException iee) {
            throw iee;
        }
    }

    //
    // this method is automatically called from within Reaction when a Reaction
    // object
    // is being initialized.
    //
    public void addReaction(Reaction r) {
        reactions.add(r);
    }

    public void removeReaction(Reaction r) {
        reactions.remove(r);
    }

    public boolean hasReactions() {
        return (!reactions.isEmpty());
    }

    //
    // set the rate expression for this substance based on the rate expressions
    // of the reactions
    // that this substance is involved in.
    //
    // although this method appears messy, it doesn't. the underlying logic is
    // quite simple.
    //
    // all it does is to collect the rate expression from each reaction and its
    // stoichiometry
    // from each *ProgressiveReaction* and combine them to the current rate
    // expression.
    //
    public void setRateExpression() {
        //
        // first reset the rate expression of this substance to null.
        //
        rateExpression = null;
        Iterator rItr = reactions.iterator();
        while (rItr.hasNext()) {
            Reaction r = (Reaction) rItr.next();
            double c = r.getCoefficient(this);
            // it's impossible to construct rate expression for Equilibrated
            // reactions.
            if (c != 0.0) {
                if (r instanceof ProgressiveReaction) {
                    if (rateExpression == null) {
                        if (c == 1.0)
                            rateExpression = ((ProgressiveReaction) r)
                                    .getRateExpression();
                        else
                            rateExpression = ExpressionBuilder.buildBinary("*",
                                    new Constant(c), ((ProgressiveReaction) r)
                                            .getRateExpression());
                    } else {
                        if (c == 1.0)
                            rateExpression = ExpressionBuilder.buildBinary("+",
                                    rateExpression, ((ProgressiveReaction) r)
                                            .getRateExpression());
                        else if (c == -1.0)
                            rateExpression = ExpressionBuilder.buildBinary("-",
                                    rateExpression, ((ProgressiveReaction) r)
                                            .getRateExpression());
                        else {
                            if (c > 0.0) {
                                GeneralExpression ge = ExpressionBuilder
                                        .buildBinary("*", new Constant(c),
                                                ((ProgressiveReaction) r)
                                                        .getRateExpression());
                                rateExpression = ExpressionBuilder.buildBinary(
                                        "+", rateExpression, ge);
                            } else {
                                GeneralExpression ge = ExpressionBuilder
                                        .buildBinary("*", new Constant(-c),
                                                ((ProgressiveReaction) r)
                                                        .getRateExpression());
                                rateExpression = ExpressionBuilder.buildBinary(
                                        "-", rateExpression, ge);
                            }
                        }
                    }
                }
            }
        }
    }

    public void setStochasticTerm() {
        //
        // first reset the rate expression of this substance to null.
        //
        stochasticTerm = null;
        Iterator rItr = reactions.iterator();
        while (rItr.hasNext()) {
            Reaction r = (Reaction) rItr.next();
            double c = r.getCoefficient(this);
            // it's impossible to construct rate expression for Equilibrated
            // reactions.
            if (c != 0.0) {
                if (r instanceof ProgressiveReaction) {
                    if (stochasticTerm == null) {
                        if (c == 1.0)
                            stochasticTerm = ((ProgressiveReaction) r)
                                    .getStochasticRateExpression();
                        else
                            stochasticTerm = ExpressionBuilder.buildBinary("*",
                                    new Constant(c), ((ProgressiveReaction) r)
                                            .getStochasticRateExpression());
                    } else {
                        if (c == 1.0)
                            stochasticTerm = ExpressionBuilder.buildBinary("+",
                                    stochasticTerm, ((ProgressiveReaction) r)
                                            .getStochasticRateExpression());
                        else if (c == -1.0)
                            stochasticTerm = ExpressionBuilder.buildBinary("-",
                                    stochasticTerm, ((ProgressiveReaction) r)
                                            .getStochasticRateExpression());
                        else {
                            if (c > 0.0) {
                                GeneralExpression ge = ExpressionBuilder
                                        .buildBinary(
                                                "*",
                                                new Constant(c),
                                                ((ProgressiveReaction) r)
                                                        .getStochasticRateExpression());
                                stochasticTerm = ExpressionBuilder.buildBinary(
                                        "+", stochasticTerm, ge);
                            } else {
                                GeneralExpression ge = ExpressionBuilder
                                        .buildBinary(
                                                "*",
                                                new Constant(-c),
                                                ((ProgressiveReaction) r)
                                                        .getStochasticRateExpression());
                                stochasticTerm = ExpressionBuilder.buildBinary(
                                        "-", stochasticTerm, ge);
                            }
                        }
                    }
                }
            }
        }
    }

    public GeneralExpression getRateExpression() {
        //
        // Modified July 07, 2002
        // NOTE Lingchong You. The following statement is added to ensure that
        // the rate expression
        // is updated. But it may slow down the program. This needs to be found
        // out.
        //
        if (rateExpression == null)
            setRateExpression();
        // End Modified.
        return rateExpression;
    }

    /**
     * gets the current rate (of change) for this substance
     */
    public double getRate() {
        // if the rate expression for the substance is empty, the level of
        // the substance won't change with time.
        if (rateExpression == null)
            return 0.0;
        return rateExpression.getValue();
    }

    public double getNoise() {
        if (stochasticTerm == null)
            return 0.0;
        return stochasticTerm.getValue();
    }

    /**
     * used for debugging
     */
    public void update(double dt) {
        setValue(Math.max(getValue() + dt * getRate(), min));
    }

    public double getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(double iv) {
        initialValue = iv;
        if (getSystem() != null)
            getSystem().fireSystemStateChange();
    }

    public double getValue(int i) {
        return values.get(i);
    }

    @Override
    public double getValue() {
        // System.out.println(value);
        return value;
    }

    public void storeValue() {
        values.add(value);
    }

    public double[] getValues() {
        return values.doubleValues();
    }

    public void setValues(double[] vals) {
        values = new DoubleList();
        for (int i = 0; i < vals.length; i++) {
            values.add(vals[i]);
        }
    }

    public double getRate(int i) {
        return rates.get(i);
    }

    // Revised by Lingchong You. July 2013
    // could cause problem if equilibriated reactions are involved.
    //
    public void storeRate() {
        if (rateExpression == null)
            rates.add(0.0);
        else
            rates.add(rateExpression.getValue());
    }

    public double[] getRates() {
        return rates.doubleValues();
    }

    @Override
    public void reset() {
        super.reset();
        if (!values.isEmpty()) {
            values.clear();
            rates.clear();
        }
        setRateExpression();
        setStochasticTerm();
        value = initialValue;
    }

    @Override
    public String toString() {
        return ("[" + getName() + "]");
    }

    public String getCompleteInfo() {
        StringBuffer sb = new StringBuffer(getFullName() + " {" + NEWLINE
                + " InitialValue { " + getInitialValue() + "}" + NEWLINE
                + "Value {" + getValue() + "}" + NEWLINE + " Min {" + getMin()
                + "}" + NEWLINE + " Max {" + getMax() + "}" + NEWLINE
                +
                // the getX and getY methods were implemented on 4/19/2005
                " X  {" + getX() + "}" + NEWLINE + " Y  {" + getY() + "}"
                + NEWLINE + " Annotation { " + getAnnotation() + " }" + NEWLINE);
        sb.append("}" + NEWLINE);
        return sb.toString();
    }

    @Override
    public void setProperty(String propertyName, String propertyValue)
            throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.compareToIgnoreCase("max") == 0)
            setMax(Double.parseDouble(propertyValue));
        else if (propertyName.compareToIgnoreCase("min") == 0)
            setMin(Double.parseDouble(propertyValue));
        else if (propertyName.compareToIgnoreCase("InitialValue") == 0)
            setInitialValue(Double.parseDouble(propertyValue));
        else if (propertyName.compareToIgnoreCase("Value") == 0)
            setValue(Double.parseDouble(propertyValue));
        else if (propertyName.compareToIgnoreCase("visible") == 0) {
            setVisible(Boolean.valueOf(propertyValue).booleanValue());
        } else if (propertyName.compareToIgnoreCase("toPrint") == 0) {
            setToPrint(Boolean.valueOf(propertyValue).booleanValue());
        }

        // the following two if statements were added on 4/19/2005
        else if (propertyName.compareToIgnoreCase("X") == 0) {
            setX(Double.parseDouble(propertyValue));
        } else if (propertyName.compareToIgnoreCase("Y") == 0) {
            setY(Double.parseDouble(propertyValue));
        }

        else if (propertyName.equalsIgnoreCase("annotation")) {
            setAnnotation(propertyValue);
        } else
            throw new UnknownPropertyException(
                    "Unknown property for Substance " + getName() + ":"
                            + propertyName);
        if (getSystem() != null)
            getSystem().fireSystemStateChange();
    }

    @Override
    public javax.swing.JPanel editor() {
        return new dynetica.gui.entities.SubstanceEditor(this);
    }

    @Override
    public dynetica.gui.visualization.AbstractNode getNode() {
        if (this.node != null)
            return this.node;
        return new dynetica.gui.visualization.SNode(this);
    }

    @Override
    public Object clone() {
        Substance s = new Substance(this.name + "_clone", this.system);
        s.setInitialValue(this.initialValue);
        s.setValue(this.value);
        s.setX(getX() + 4);
        s.setY(getY() + 4);
        return s;
    }

    public List getReactions() {
        return reactions;
    }
}
