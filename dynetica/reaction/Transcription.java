/*
 * Transcription.java
 *
 * Created on March 16, 2001, 1:24 PM
 */

package dynetica.reaction;

import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.exception.*;

/**
 *
 * @author  Lingchong You
 * @version 0.1
 */
public class Transcription extends ProgressiveReaction {
    private static int transcriptionIndex = 0;
    Gene gene = null; // gene will hold the information about the rate expression and its mRNA
    Substance NTP = null;
     
    /** Creates new Transcription */
    public Transcription() {
        this(null, null);
        setName("Transcription" + transcriptionIndex++);
    }
    
    public Transcription(Gene gene) {
        this(gene, null);
    }
    
    public void addSubstance( Substance s, Double d) {
        super.addSubstance(s, d);
        if (s.getName().compareTo("NTP") == 0) NTP = s;
    }
    
    public Transcription(Gene gene, Substance NTP) {
        if (gene != null) {
            setGene(gene);
            setName("Transcription_" + gene.getName());
            setSystem(gene.getGeneticSystem());
            gene.addReaction(this);
        }
        if (NTP != null)
            setNTP(NTP);
        
    }
    /**
     *set up the transcription reaction: its reaction formula, and its rate expression
     *the rate is calculated based on the number of NTPs.
     */
    public void setup() {
        clear();
        RNA rna = gene.getRna();
        if (NTP != null) addSubstance(NTP, -1.0);
        addSubstance(gene.getRNAP(), 0.0);
        addSubstance(rna, 1.0 / (gene.getLength()));
         rateExpression = 
         DMath.multiply(gene.getRNAP().getElongationRate(),
           DMath.min(
             DMath.multiply(gene.getRNAP(),
                 DMath.divide(DMath.multiply(gene.getKTranscription(), gene), 
                     gene.getRNAP().getTotalTranscriptionActivity()
                )
               ), 
             //
             // the maximum # of RNAPs that can be allocated to a gene
             // is the gene length divided by the RNAP spacing requirement.
             //
             DMath.divide(new Constant(gene.getLength()),gene.getRNAP().getSpacing())
             )
           );
         
         // LY:
         // The following code sets the rate expression to be somewhat like the
         // Michaelis Menten kinetics. The constant 1e3 is a value arbitrarily chosen
         // for Km (The user can always modify it through the GUI, or can they?) .
         //
         if (NTP != null)
             rateExpression = DMath.multiply(rateExpression, 
                DMath.divide(NTP, DMath.sum(NTP, new Constant(1e3))));
    }
    
    /** Getter for property NTP.
     * @return Value of property NTP.
     */
    public Substance getNTP() {
        return NTP;
    }
    
    /** Setter for property NTP.
     * @param NTP New value of property NTP.
 */
    public void setNTP(Substance NTP) {
        this.NTP = NTP;
    }
    
    /** Getter for property gene.
     * @return Value of property gene.
 */
    public Gene getGene() {
        return gene;
    }
    
    /** Setter for property gene.
     * @param gene New value of property gene.
 */
    public void setGene(Gene gene) {
        this.gene = gene;
        if (gene != null)
            gene.setTranscription(this);
    }
    
    public void setProperty(String propertyName, String propertyValue)
     throws UnknownPropertyException, InvalidPropertyValueException {
         if (propertyName.compareToIgnoreCase("Gene") == 0) {
             setGene((Gene) ((getSystem().get(propertyValue))));
         }
         else
             super.setProperty(propertyName, propertyValue);
     }

    public String toString() {
        return(getFullName() + "{" + NEWLINE + 
        "\t Gene { " + gene.getLongName() + " } " + NEWLINE +
        "\t stoichiometry { " + getStoichiometry() + " } " + NEWLINE +
        "\t kinetics { " + getKinetics() + " } " + NEWLINE +
               "}" );
    }
   
    public javax.swing.JPanel editor() {
        return new dynetica.gui.genetics.TranscriptionEditor(this);
    }
}
