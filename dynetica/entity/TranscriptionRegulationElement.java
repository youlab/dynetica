
/**
 * TranscriptionRegulationElement.java
 *
 *
 * Created: Thu Aug 24 10:34:10 2000
 *
 * @author Lingchong You
 * @version
 */
package dynetica.entity;
import dynetica.system.*;
import dynetica.exception.*;

public class TranscriptionRegulationElement extends GeneticElement {
    
    double efficiency;
    RNAPolymerase RNAP;
     
    /**
       * Get the value of efficiency.
       * @return Value of efficiency.
       */
    public double getEfficiency() {return efficiency;}
    
    /**
       * Set the value of efficiency.
       * @param v  Value to assign to efficiency.
       */
    public void setEfficiency(double  v) {this.efficiency = v;}
    
    /**
       * Get the value of RNAP.
       * @return Value of RNAP.
       */
    public RNAPolymerase getRNAP() {return RNAP;}
    
    /**
       * Set the value of RNAP.
       * @param v  Value to assign to RNAP.
       */
    public void setRNAP(RNAPolymerase v) {this.RNAP = v;}
    
    
    public TranscriptionRegulationElement() {
        this("TranscriptionRegulationElement", null);	
    }
    
    public TranscriptionRegulationElement(String name, Genome genome) {
	this(name, genome, 0, 0, 1.0);
    }

    public TranscriptionRegulationElement(String name, Genome genome,
					  int start, int end, double efficiency) {
	super(name, genome, start, end);
	this.efficiency = efficiency;
    }

    public javax.swing.JPanel editor () {
        return new dynetica.gui.genetics.TranscriptionRegulationElementEditor(this);
    }
    
    public String getCompleteInfo() {
        StringBuffer tmp = new StringBuffer (getFullName() + "{" + NEWLINE + 
        "\tstart {" + getStart() + "}" + NEWLINE +
        "\tend {" + getEnd() + "}" + NEWLINE + 
        "\tefficiency {" + getEfficiency() + "}" + NEWLINE);
        if (getRNAP() != null)
            tmp.append("\tRNAP {" + getRNAP().getName() + "}" + NEWLINE);
        tmp.append("}");
        return tmp.toString();
    }
    
    public void setProperty(java.lang.String propertyName,java.lang.String propertyValue) throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.compareToIgnoreCase("start") == 0 )
            setStart(Integer.parseInt(propertyValue));
        else if (propertyName.compareToIgnoreCase("end") == 0) 
            setEnd(Integer.parseInt(propertyValue));
        else if (propertyName.compareToIgnoreCase("efficiency") == 0)
            setEfficiency(Double.parseDouble(propertyValue));
        else if (propertyName.compareToIgnoreCase("RNAP") == 0) {
            if (getSystem().contains(propertyValue)) 
                setRNAP( (RNAPolymerase) getGeneticSystem().get(propertyValue));
            else {
                setRNAP( new RNAPolymerase(propertyValue, getGeneticSystem()));
            }
        }                
        else
            throw new UnknownPropertyException("UnknownProperty for class GeneticElement:" + propertyName);
}
    
} // TranscriptionRegulationElement
