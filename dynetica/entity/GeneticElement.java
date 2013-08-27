/***************************************************************************
 *  GeneticElement.java  -  description
 *  @author begin: Mon Mar 18 2000
 *  @author copyright: (C) 2000 by Lingchong You
 *  @author Department of Chemical Engineering
 *  @author University of Wisconsin-Madison
 *  @author email: you@cae.wisc.edu
 *  @version 0.01
 **/

package dynetica.entity;

import dynetica.system.*;
import dynetica.exception.*;

public class GeneticElement extends LinearSubstance {
    private static int geneticElementIndex = 0;
    /** Holds value of property active. */
    private boolean active = true;

    public GeneticElement() {
        this("GeneticElement" + geneticElementIndex++, null, 0, 0);
    }

    public GeneticElement(String name, Genome genome, int start, int end) {
        super(name, genome, start, end);
    }

    public GeneticElement(String name, Genome genome) {
        this(name, genome, 0, 0);
    }

    /**
     * returns the name of the genetic element prefixed with its genome's name
     */
    public String getLongName() {
        if (getGenome() != null)
            return getGenome().getName() + '$' + getName();
        return getName();
    }

    public Genome getGenome() {
        return (Genome) getSystem();
    }

    public void setGenome(Genome g) {
        setSystem(g);
    }

    public GeneticSystem getGeneticSystem() {
        return (GeneticSystem) (getSystem().getSystem());
    }

    public void setStart(int newStart) {
        if (getStart() != newStart) {
            super.setStart(newStart);
            if (getGenome() != null)
                getGenome().adjustElementPosition(this);
        }
    }

    public void shift(int distance) {
        /**
         * move the element leftward (when distance < 0) or rightward (when
         * distance>0) by |distance| units.
         */
        setStart(getStart() + distance);
        setEnd(getEnd() + distance);
    }

    public String getCompleteInfo() {
        return (getFullName() + " {" + NEWLINE + "\tstart {" + getStart() + "}"
                + NEWLINE + "\tend {" + getEnd() + "}" + NEWLINE + "}");
    }

    /**
     * Overrided to print out an ID that can be parsed by Expression Parser
     */
    public String toString() {
        return ("[" + getLongName() + "]");
    }

    /**
     * overrides the setRateExpression() method defined in Substance Currently,
     * it's assumed that the level of the GeneticElements won't change as the
     * function of time. Thus the rate expression is set to be zero.
     **/

    public void setRateExpression() {
        rateExpression = new dynetica.expression.Constant(0.0);
    }

    public void setProperty(java.lang.String propertyName,
            java.lang.String propertyValue) throws UnknownPropertyException,
            InvalidPropertyValueException {
        if (propertyName.compareToIgnoreCase("start") == 0)
            setStart(Integer.parseInt(propertyValue));
        else if (propertyName.compareToIgnoreCase("end") == 0)
            setEnd(Integer.parseInt(propertyValue));
        else
            throw new UnknownPropertyException(
                    "UnknownProperty for class GeneticElement:" + propertyName);
    }

    public javax.swing.JPanel editor() {
        return new dynetica.gui.genetics.GeneticElementEditor(this);
    }

    /**
     * Getter for property active.
     * 
     * @return Value of property active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Setter for property active.
     * 
     * @param active
     *            New value of property active.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public void reset() {
        super.reset();
        if (getGenome().getTranslocation() != null)
            setActive(false);
        else
            setActive(true);
    }

    public dynetica.gui.visualization.AbstractNode getNode() {
        return new dynetica.gui.genetics.GeneticElementNode(this);
    }

    public static class Tester {
        public static void main(String[] args) {
            Genome s = new Genome();
            GeneticElement elm1 = new GeneticElement("Genetic_Element", s, 10,
                    30);
            System.out.println(elm1);
            GeneticElement elm2 = new GeneticElement("Genetic_Element2", s, 10,
                    20);
            System.out.println(elm2);
            GeneticElement elm3 = new GeneticElement("GE5", s, 19, 31);
            System.out.println(elm3);

            if (elm2.isWithin(elm1))
                System.out.println(elm2.getName() + " is within "
                        + elm1.getName());
            if (elm2.overlaps(elm3))
                System.out.println(elm2.getName() + " overlaps "
                        + elm3.getName());

            // align elm3 to a new start
            elm3.align(10);
            System.out.println(elm3);
            if (elm3.isWithin(elm1))
                System.out.println(elm1.getName() + " contains "
                        + elm3.getName());
        }
    }
}
