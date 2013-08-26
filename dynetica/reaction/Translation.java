/*
 * Translation.java
 *
 * Created on March 22, 2001, 2:28 PM
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
public class Translation extends ProgressiveReaction {
    private static int translationIndex = 0;
    private Gene gene = null;
    Substance aminoAcid = null;
    

    /** Creates new Translation */
    public Translation() {
        this(null, null);
        setName("Translation" + translationIndex++);
    }

    public Translation(Gene gene) {
        this(gene, null);
    }
    
    public Translation(Gene gene, Substance aa) {
        if (gene != null) {
            setGene(gene);
            setName("Translation_" + gene.getName());
            setSystem(gene.getGeneticSystem());
        }
        if (aa != null) {
            setAminoAcid(aa);
        }
        setToPrint(false);
    }
    
    public void setup() {
       clear();
       Protein protein = gene.getProtein();
       if (aminoAcid != null) addSubstance(aminoAcid, -1.0); 
       addSubstance(gene.getRibosome(), 0.0);
       addSubstance(protein, 3.0 / (gene.getLength()));
       rateExpression = DMath.multiply(gene.getRibosome().getElongationRate(),
             DMath.min (   
                   DMath.multiply(gene.getRibosome(),
                      DMath.divide( DMath.multiply(gene.getKTranslation(), gene.getRna()), 
                         gene.getRibosome().getTotalTranslationActivity()
                    )
                  ),
                 //
                 // the maximum # of ribosomes that can be allocated to a gene
                 // is the gene length divided by the ribosome spacing requirement.
                 //
                 DMath.divide(
                    DMath.multiply(new Constant(gene.getLength() / 3.0), gene.getRna()),
                    gene.getRibosome().getSpacing())
             )
         );
       //
       // hack: used to make sure that when aminoAcid is set, the rate should be zero
       // when amino acids are all consumed.
       //
       // better implementation is by using the MichaelisMenten reaction scheme, but
       // it needs an additional parameter.
       //
       
       if (aminoAcid != null)
           rateExpression = DMath.multiply(rateExpression, 
                DMath.divide(aminoAcid, DMath.sum(aminoAcid, new Constant(1e4))));
       
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
        gene.setTranslation(this);
    }
    
    public void setProperty(String propertyName, String propertyValue)
     throws UnknownPropertyException, InvalidPropertyValueException {
         if (propertyName.compareToIgnoreCase("Gene") == 0) {
             setGene((Gene) ((getSystem().get(propertyValue))));
         }
         else
             super.setProperty(propertyName, propertyValue);
     }
     
    /** Getter for property aminoAcid.
     * @return Value of property aminoAcid.
 */
    public Substance getAminoAcid() {
        return aminoAcid;
    }
    
    /** Setter for property aminoAcid.
     * @param aminoAcid New value of property aminoAcid.
 */
    public void setAminoAcid(Substance aminoAcid) {
        this.aminoAcid = aminoAcid;
    }
    
    public void addSubstance( Substance s, Double d) {
        super.addSubstance(s, d);
        if (s.getName().compareTo("AminoAcid") == 0) aminoAcid = s;
    }
    
    public String toString() {
        return(getFullName() + "{" + NEWLINE + 
        "\t Gene { " + gene.getLongName() + " } " + NEWLINE +
        "\t stoichiometry { " + getStoichiometry() + " } " + NEWLINE +
        "\t kinetics { " + getKinetics() + " } " + NEWLINE +
               "}" );
    }
    
}
