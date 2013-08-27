/*
 * Translocation.java
 *
 * Created on August 6, 2001, 8:58 PM
 */

package dynetica.reaction;

/**
 *
 * @author  Lingchong You
 * @version 0.01
 */

import dynetica.entity.*;
import dynetica.system.*;
import dynetica.exception.*;
import dynetica.expression.*;

import java.util.List;

public class GenomeTranslocation extends ProgressiveReaction implements Genetic {

    /** Holds value of property genome. */
    private Genome genome;

    /** Creates new Translocation */
    public GenomeTranslocation() {
    }

    public GenomeTranslocation(Genome genome) {
        setGenome(genome);
    }

    /**
     * Getter for property genome.
     * 
     * @return Value of property genome.
     */
    public Genome getGenome() {
        return genome;
    }

    /**
     * Setter for property genome.
     * 
     * @param genome
     *            New value of property genome.
     */
    public void setGenome(Genome genome) {
        this.genome = genome;
        genome.setTranslocation(this);
    }

    public void reset() {
        super.reset();
        // genome.reset();
    }

    public void update(double dt) {
        int lin = genome.getLengthInCell();
        int endPosition = genome.getEnd();
        if (lin < endPosition) {
            // System.out.println(" "+ len + "  " + lin);
            lin += (int) getRate() * dt;
            genome.setLengthInCell(lin);
            GeneticElement ge;
            List elements = genome.getElements();
            //
            // search for the first element whose "tail" is still outside of the
            // cell.
            //
            for (int i = 0; i < elements.size(); i++) {
                ge = (GeneticElement) elements.get(i);
                if (ge.getEnd() > lin)
                    break;
                if (!ge.isActive())
                    ge.setActive(true);
            }
        }
    }

    public void setProperty(String propertyName, String propertyValue)
            throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.compareToIgnoreCase("kinetics") == 0) {
            kinetics = propertyValue;
            try {
                this.rateExpression = ExpressionParser.parse(this.getSystem(),
                        propertyValue);
            } catch (IllegalExpressionException iee) {
                throw new InvalidPropertyValueException(iee.getMessage());
            }
        } else if (propertyName.equalsIgnoreCase("Genome")) {
            setGenome((Genome) (this.getSystem().get(propertyValue)));
        } else
            throw new UnknownPropertyException(
                    "Unknown property for GenomeTranslocation:" + propertyName);
    }

    public javax.swing.JPanel editor() {
        return new dynetica.gui.genetics.GenomeTranslocationEditor(this);
    }

    public String toString() {
        return (getFullName() + "{" + NEWLINE + "\t genome {"
                + genome.getName() + "}" + NEWLINE + "\t kinetics {"
                + getKinetics() + "}" + NEWLINE + "}");
    }

}
