/*
 * Ribosome.java
 *
 * Created on March 22, 2001, 11:09 PM
 */

package dynetica.entity;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.system.*;
import java.util.*;

/**
 *
 * @author  Lingchong You
 * @version 0.1
 */
public class Ribosome extends GeneExpressionMachinery{
    private static int ribosomeIndex = 0;
    private List genes = new ArrayList();
    
    /** Creates new Ribosome */
    public Ribosome() {
        setName("Ribosome" + ribosomeIndex++);
    }
   
    
    public Ribosome(String name, GeneticSystem system) {
        this(name, system, 100, 20, 0, 0, 1e300);
    }
    
    public Ribosome(String name, 
            GeneticSystem system,
            double elongationRate,
            double spacing,
            double iv,
            double min, 
            double max) {
                setName(name);
                setSystem(system);
                setSpacing(new Constant(spacing));
                setElongationRate(new Constant(elongationRate));
                setInitialValue(iv);
                setMin(min);
                setMax(max);
    }

    public void addGene(Gene gene) {
        genes.add(gene);
    }
    
    public void removeGene(Gene gene) {
        genes.remove(gene);
    }
    
    //
    // the following method is to be used to set up the rate expression
    // for Translation. The ribosomes are assumed to be allocated
    // to different genes based on their translation activity: 
    // kTranslation, which in turn can be determined from the 
    // strengths of the ribosome binding sites of the genes and the
    // relative abundance of the RNAs.
    //
    public GeneralExpression getTotalTranslationActivity() {
        //
        // the following line of code is a hack to make sure that
        // the total translation activity is never zero even when
        // no RNA is present. 
        GeneralExpression ge = new Constant(1e-100);
                
        for (int i = 0; i < genes.size(); i++) {
            Gene g = (Gene) genes.get(i);
            if (g.isTranslatable())
                ge = DMath.sum(ge, DMath.multiply(g.getRna(), g.getKTranslation()));
        }
            
        return ge;
    }

    public void setProperty(String propertyName, String propertyValue)
    throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.compareToIgnoreCase("ElongationRate") == 0) {
            try {
                setElongationRate(propertyValue);
            }
            catch( IllegalExpressionException iee) {
                throw new InvalidPropertyValueException(iee.getMessage());
            }
        }
        else if (propertyName.compareToIgnoreCase("Spacing") == 0 ){
            try {
                setSpacing(ExpressionParser.parse(getSystem(), propertyValue));
            }
            catch (IllegalExpressionException iee) {
                throw new InvalidPropertyValueException(iee.getMessage());
            }
        }
        
        else if (propertyName.compareToIgnoreCase("max") == 0) 
            setMax(Double.parseDouble(propertyValue));
        else if (propertyName.compareToIgnoreCase("min") == 0)
            setMin(Double.parseDouble(propertyValue));
        else if (propertyName.compareToIgnoreCase("InitialValue") == 0 || propertyName.compareToIgnoreCase("Value") == 0)
            setInitialValue(Double.parseDouble(propertyValue));
        else if (propertyName.compareToIgnoreCase("visible") == 0)
            setVisible(Boolean.getBoolean(propertyValue));       
        else
            throw new UnknownPropertyException(propertyName);
    }

    
//    public javax.swing.JPanel editor() {
//        return new dynetica.gui.RibosomeEditor(this);
//    }
    
    public String getCompleteInfo() {
        StringBuffer sb = new StringBuffer(
        getFullName() + " {" +  NEWLINE + 
        "\t InitialValue { " + getInitialValue() + " } " + NEWLINE +
       "\t Min { " + getMin() + " } " + NEWLINE + 
        "\t Max { " + getMax() + " } " + NEWLINE + 
        "\t Visible { " + isVisible() + " } " + NEWLINE + 
        "\t Spacing { " + getSpacing() + " } " + NEWLINE +
        "\t ElongationRate { " + getElongationRate() + " } " + NEWLINE);
        sb.append("}" + NEWLINE);
        return sb.toString();
   }
    
}
