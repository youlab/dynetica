/*
 * Gene.java
 *
 * Created on April 7, 2000, 12:18 AM
 */
 
package dynetica.entity;
import dynetica.reaction.*;
import dynetica.system.*;
import dynetica.expression.*;
import dynetica.exception.*;

import java.util.*;

/** 
 *
 * @author  lingchong you
 * @version 0.01
 */
public class Gene extends GeneticElement{
    private static int geneIndex = 0;
    RNA rna;
    Protein protein;
    String rnaName;
    String proteinName;
    Reaction transcription;
    Reaction translation;
    
   RNAPolymerase RNAP;
   
   /** Holds value of property kTranscription. 
    ***** Note
    * kTranscription is defined as an expression rather than a parameter
    * so that it will be more flexible: it can accept a single parameter
    * or combinations of variables rather than a single parameter.
    * The same is true for kTranslation.
    * Both kTranscription and kTranslation can be initialized using the
    * Expression parser defined in the expression package (this parser, well,
    * is indeed very important!!!). In a sense, this approach is probably
    * inspired by Mathematica, where everything is an expression.
    */
   
   private GeneralExpression kTranscription;
   
   /** Holds value of property kTranslation. */
   private GeneralExpression kTranslation;
   
   /** Holds value of property ribosome. */
   private Ribosome ribosome;   
   
   public static final byte mRNA_GENE = 0;
   
   public static final byte tRNA_GENE = 1;

   public static final byte rRNA_GENE = 2;
   
   /** Holds value of property type. A gene is by default an mRNA_GENE */
   private byte geneType = mRNA_GENE ;   
   
   // 
   // Also, I assume the user will somehow provide the relative
   // transcription activity (kTx) and translation activity (kTl) of this 
   // gene. In doing the simulation, I will use an "allocation model" 
   // (any other good term to describe this?)
   // in simulating the processes of transcription and translation.
   // For instance, the RNAP will be allocated using the following 
   // equation :
   //  N_i = kTx_i/(kTx_1 + kTx_2 + ...) * N_RNAP
   //
   
  /** Creates new Gene */
    public Gene() {
	this("Gene" + geneIndex++);    
    }

    public Gene(String name) {
        this(name, null, 0, 0);        
    }
    
    public Gene(String name, Genome genome, int start, int end) {
        this(name, genome, start, end, mRNA_GENE);
    }
    
    public Gene(String name, Genome genome, int start, int end, byte type) {
        super (name, genome, start, end);
        setGeneType(type);
    }
        
    //
    // what did i write this method for??? (L You)
    //
    public void removeMe() {
        GeneticSystem sys = getGeneticSystem();
        for (int i = 0; i < reactions.size(); i ++) {
            sys.remove(((Reaction) reactions.get(i)).getName());
        }
        ribosome.removeGene(this);
        RNAP.removeGene(this);
        sys.setupGeneticInteractions();
        reactions.removeAll(reactions);
    }
   
    public double getValue() {
        if (isActive()) return 1.0;
        else return 0.0;
    }
    /** Getter for property protein.
     * @return Value of property protein.
    */
    public Protein getProtein() {
        return protein;
    }
    
    public String getRnaName() {
        if (rnaName == null) 
           return "RNA_" + getName();
        return rnaName;
    }
    
    public String getProteinName() {
        if (geneType != mRNA_GENE) 
            return null;
        if (proteinName == null) 
            return "Protein_" + getName();
        return proteinName;
    }
    
    public void setRnaName(String name) {
        rnaName = name;
    }
    
    public void setProteinName( String name) {
        if (this.geneType == mRNA_GENE)
            proteinName = name;
    }
    
    public void setProducts() {
       if (getGeneticSystem().contains(getRnaName())) 
           rna = (RNA) (getGeneticSystem().get(getRnaName()));
       else {
           rna = new RNA(this);
           rna.setVisible(true);
       }
       
       //
       // set up the protein product for the gene only if it's going to be translated.
       //
       if (geneType == mRNA_GENE) {
           if (getGeneticSystem().contains(getProteinName())) {
               try {
                protein = (Protein) (getGeneticSystem().get(getProteinName()));
               }
               catch (Exception e) {
                System.out.println(getProteinName());
               }
           }
           else {
                protein = new Protein(this);
                protein.setVisible(true);
           }
       }
    }
    
    /** Getter for property kTranscription.
     * @return Value of property kTranscription.
     */
    public GeneralExpression getKTranscription() {
        if (kTranscription != null) {
                return kTranscription;
        }
        
        //
        // if kTranscription is not set, use the length of the gene as the weighting factor
        //
        return new Constant(getLength());
    }
    
    /** Setter for property kTranscription.
     * @param kTranscription New value of property kTranscription.
 */
    public void setKTranscription(GeneralExpression kTranscription) {
        this.kTranscription = kTranscription;
        if (getSystem() != null) getSystem().fireSystemStateChange();
    }
    
    /**
     * set the transcription activity based on an expression string
     */
    public void setKTranscription(String expression)    
    throws IllegalExpressionException {
        try {
            setKTranscription(ExpressionParser.parse(this.getGeneticSystem(), expression));
        }
        catch (IllegalExpressionException iee) {
            throw iee;
        }
    }
        
    /** Getter for property kTranslation.
     * @return Value of property kTranslation.
 */
    public GeneralExpression getKTranslation() {
        if (kTranslation != null) {
            return kTranslation;
        }
        //
        // otherwise use the length of the gene as the weighting factor
        //
        if ( ! isTranslatable() ) return new Constant(0);
        
        return new Constant(getLength());
    }
    
    public GeneralExpression getActualKTranscription() {
          GeneralExpression actualKTranscription = new GeneralExpression () {
                public double getValue() {
                    if (isActive())
                        return getKTranscription().getValue();
                    else
                        return 0;
                }
                
                public int getType() {
                    return Integer.MAX_VALUE;
                }
            }; 
            return actualKTranscription;
    }
    
    /** Setter for property kTranslation.
     * @param kTranslation New value of property kTranslation.
 */
    public void setKTranslation(GeneralExpression kTranslation) {
        this.kTranslation = kTranslation;
        if (getSystem() != null) getSystem().fireSystemStateChange();
    }
    
    /**
     * set the translation activity based on an expression string
     */
    public void setKTranslation(String expression)    
    throws IllegalExpressionException {
        try {
            setKTranslation(ExpressionParser.parse(this.getGeneticSystem(), expression));
        }
        catch (IllegalExpressionException iee) {
            throw iee;
        }
    }
    
    public void setProperty(String propertyName, String propertyValue) 
    throws UnknownPropertyException, InvalidPropertyValueException{
        if (propertyName.compareToIgnoreCase("start") == 0 )
            setStart(Integer.parseInt(propertyValue));
        else if (propertyName.compareToIgnoreCase("end") == 0) 
            setEnd(Integer.parseInt(propertyValue));
        else if (propertyName.compareToIgnoreCase("RNA") == 0) {
         //   System.out.println("New mRNA name for " + getName());
            setRnaName(propertyValue);
        }
        else if (propertyName.compareToIgnoreCase("Protein") == 0)
            setProteinName(propertyValue);
        //
        // the RNAP of the gene is specified by its name
        //
        else if (propertyName.compareToIgnoreCase("RNAP") == 0) {
            if (getGeneticSystem().contains(propertyValue)) {
               setRNAP((RNAPolymerase) (getGeneticSystem().get(propertyValue)));
            }
            //
            // if the specified RNAP doesn't yet exist, create one.
            //
            // There seems to be a problem here. (3/22/2001)
            //
            else {
              System.out.println(propertyValue + " created");
              setRNAP(new RNAPolymerase(propertyValue, getGeneticSystem()));
            }
        }
        else if (propertyName.compareToIgnoreCase("kTranscription") == 0) {
            try {
              setKTranscription(propertyValue);
            }
            catch ( IllegalExpressionException iee ) {
              throw new InvalidPropertyValueException(iee.getMessage());
            }
        }
        else if (propertyName.compareToIgnoreCase("kTranslation") == 0) {
            try {
              setKTranslation(propertyValue);
            }
            catch( IllegalExpressionException iee) {
                throw new InvalidPropertyValueException(iee.getMessage());
            }
        }
        else
            throw new UnknownPropertyException("UnknownProperty for Gene " + propertyName);

        if (getSystem() != null) getSystem().fireSystemStateChange();

     }    
      
      /** Getter for property RNAP.
       * @return Value of property RNAP.
 */
      public RNAPolymerase getRNAP() {
          return RNAP;
      }
      
      /** Setter for property RNAP.
       * @param RNAP New value of property RNAP.
 */
      public void setRNAP(RNAPolymerase RNAP) {
          this.RNAP = RNAP;
          RNAP.addGene(this);
          if (getSystem() != null) getSystem().fireSystemStateChange();
      }
      
      public String getCompleteInfo() {
          StringBuffer tmp = new StringBuffer(getFullName() + "{" + NEWLINE +
          "\tstart {" + getStart() + "}" + NEWLINE +
          "\tend {" + getEnd() + "}" + NEWLINE );
          if (kTranscription != null) 
              tmp.append("\tkTranscription {" + kTranscription + "}" + NEWLINE);
          if (kTranslation != null) 
              tmp.append("\tkTranslation {" + kTranslation + "}" + NEWLINE);
          if (RNAP != null) 
              tmp.append("\tRNAP {" + getRNAP().getName() + "}" + NEWLINE);
          tmp.append("\tRNA {" + getRnaName() + "}" + NEWLINE);
          tmp.append("\tProtein {" + getProteinName() + "}" + NEWLINE);
          // tmp.append("\tRNAP : " + "T7RNAP" + NEWLINE);
          tmp.append("}");
          return tmp.toString();
      }
      
      /** Getter for property ribosome.
       * @return Value of property ribosome.
       */
      public Ribosome getRibosome() {
          return ribosome;
      }
      
      /** Setter for property ribosome.
       * @param ribosome New value of property ribosome.
 */
      public void setRibosome(Ribosome ribosome) {
          this.ribosome = ribosome;
          ribosome.addGene(this);
            if (getSystem() != null) getSystem().fireSystemStateChange();
      }
      
      public javax.swing.JPanel editor() {
          return new dynetica.gui.genetics.GeneEditor(this);
      }
      
      /** Getter for property rna.
       * @return Value of property rna.
 */
      public RNA getRna() {
          return rna;
      }
      
      /** Setter for property rna.
       * @param rna New value of property rna.
 */
      public void setRna(RNA rna) {
          this.rna = rna;
            if (getSystem() != null) getSystem().fireSystemStateChange();
      }
      
      /** Getter for property type.
       * @return Value of property type.
 */
      public byte getGeneType() {
          return geneType;
      }
      
      /** Setter for property type.
       * @param type New value of property type.
 */
      public void setGeneType(byte type) {
          this.geneType = type;
          if (getSystem() != null) getSystem().fireSystemStateChange();
      }
      
      public boolean isTranslatable() {
          return geneType ==  mRNA_GENE;
      }
      
      /** Getter for property transcription.
       * @return Value of property transcription.
 */
      public Reaction getTranscription() {
          return transcription;
      }
      
      /** Setter for property transcription.
       * @param transcription New value of property transcription.
 */
      public void setTranscription(Reaction transcription) {
          this.transcription = transcription;
      }
      
      /** Getter for property translation.
       * @return Value of property translation.
 */
      public Reaction getTranslation() {
          return translation;
      }
      
      /** Setter for property translation.
       * @param translation New value of property translation.
 */
      public void setTranslation(Reaction translation) {
          this.translation = translation;
      }
      
}
