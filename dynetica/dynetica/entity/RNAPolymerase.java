/*
 * RNAPolymerase.java
 *
 * Created on March 22, 2001, 9:14 PM
 */

package dynetica.entity;

import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.system.*;
import java.util.*;

/**
 * 
 * @author Lingchong You
 * @version 0.1
 */
public class RNAPolymerase extends GeneExpressionMachinery {
    private static int rnapIndex = 0;
    private List genes = new ArrayList();

    /** Creates new RNAPolymerase */
    public RNAPolymerase() {
        this("RNAPolymerase" + rnapIndex++, null);
    }

    /*
     * public RNAPolymerase(String name, GeneticSystem system, int start, int
     * end) { super(name, system, start, end); }
     */

    public RNAPolymerase(String name, GeneticSystem system) {
        setName(name);
        setSystem(system);
    }

    //
    // the following method is to be used to set up the rate expression
    // for Transcription. The RNAP molecules are assumed to be allocated
    // to different genes based on their transcription activity:
    // kTranscription.
    //
    public GeneralExpression getTotalTranscriptionActivity() {
        GeneralExpression ge = new Constant(1e-100);
        //
        // I probably need to check whether kTranscription is null for
        // a gene.
        //
        for (int i = 0; i < genes.size(); i++) {
            ge = DMath.sum(ge, DMath.multiply(
                    ((Gene) genes.get(i)).getKTranscription(),
                    ((Gene) genes.get(i))));
        }

        return ge;
    }

    public void addGene(Gene gene) {
        genes.add(gene);
    }

    public void removeGene(Gene gene) {
        genes.remove(gene);
    }

    public void setProperty(String propertyName, String propertyValue)
            throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.compareToIgnoreCase("ElongationRate") == 0) {
            try {
                setElongationRate(propertyValue);
            } catch (IllegalExpressionException iee) {
                throw new InvalidPropertyValueException(iee.getMessage());
            }
        } else if (propertyName.compareToIgnoreCase("Spacing") == 0) {
            try {
                setSpacing(ExpressionParser.parse(getSystem(), propertyValue));
            } catch (IllegalExpressionException iee) {
                throw new InvalidPropertyValueException(iee.getMessage());
            }
        } else if (propertyName.compareToIgnoreCase("max") == 0)
            setMax(Double.parseDouble(propertyValue));
        else if (propertyName.compareToIgnoreCase("min") == 0)
            setMin(Double.parseDouble(propertyValue));
        else if (propertyName.compareToIgnoreCase("InitialValue") == 0
                || propertyName.compareToIgnoreCase("Value") == 0)
            setInitialValue(Double.parseDouble(propertyValue));
        else if (propertyName.compareToIgnoreCase("visible") == 0)
            setVisible(Boolean.getBoolean(propertyValue));
        else
            throw new UnknownPropertyException(propertyName);
    }

    public String getCompleteInfo() {
        StringBuffer sb = new StringBuffer(getFullName() + " {" + NEWLINE
                + "\t InitialValue { " + getInitialValue() + " } " + NEWLINE
                + "\t Min { " + getMin() + " } " + NEWLINE + "\t Max { "
                + getMax() + " } " + NEWLINE + "\t Visible { " + isVisible()
                + " } " + NEWLINE + "\t Spacing { " + getSpacing() + " } "
                + NEWLINE + "\t ElongationRate { " + getElongationRate()
                + " } " + NEWLINE);
        sb.append("}" + NEWLINE);
        return sb.toString();
    }

}