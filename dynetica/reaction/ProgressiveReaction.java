/**
 * ProgressiveReaction.java
 *
 *
 * Created: Wed Aug 23 17:00:28 2000
 *
 * @author Lingchong You
 * @version 1.0
 *
 * @revision 1.2 4/19/2005
 *  added coordinates for ProgressiveReaction
 */

package dynetica.reaction;
import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.system.*;
import java.util.*;

public class ProgressiveReaction extends Reaction
    implements Progressive {
    private static int pReactionIndex = 0;
    GeneralExpression rateExpression = null;
 //   GeneralExpression stochasticTerm = null;
    Normal gaussianNoise = null;

    double rate;
    /**
       * Get the value of rateExpression.
       * @return Value of rateExpression.
       */
    public GeneralExpression getRateExpression() {return rateExpression;}
    
    
    // Added August 24 2006
    // L. You note:  this implementation could cause the following bug:
    // When the value of the rateExpression is close to 0, numerical error can make its value slightly negative. Taking the 
    // square root of its value would generate a complex number, causing a problem in simulation.
    
    // A quick fix is done in Pow.java and Sqrt.java to avoid taking the power or square root of negative values.
    //
    public GeneralExpression getStochasticRateExpression(){
        GeneralExpression stochasticExpression = ExpressionBuilder.buildBinary("^", rateExpression, new Constant(0.5));
        //GeneralExpression stochasticExpression = ExpressionBuilder.buildUnary("sqrt", rateExpression);
        return ExpressionBuilder.buildBinary("*", stochasticExpression, gaussianNoise);
    }
    
    //August 25 2006, "quick n dirty" way to make sure noise term is properly generated
    public void resetNoiseTerm(){
        gaussianNoise.reset();
    }
  
       
    public void setRateExpression(GeneralExpression ge) {
        rateExpression = ge;
        kinetics = ge.toString();
        updateEntityList();
        if (getSystem()!=null) getSystem().fireSystemStateChange();
    }
    
    /** Holds value of property kinetics. */
    String kinetics;

    public ProgressiveReaction() {
	this("ProgressiveReaction" + pReactionIndex++, null);
    }

    public ProgressiveReaction(String name, ReactiveSystem system) {
	super(name, system);
	rateExpression = null;
        gaussianNoise = new Normal();
    }


    public ProgressiveReaction (String name, ReactiveSystem system,
				          String reaction, String rateExprStr) 
	throws IllegalExpressionException {
	super(name, system, reaction);
        try {
	   this.rateExpression = ExpressionParser.parse(this.getSystem(), rateExprStr);
	}

	catch (IllegalExpressionException iee) {
	   throw iee;
	}     
     }
     
     public void setProperty(String propertyName, String propertyValue)
     throws UnknownPropertyException, InvalidPropertyValueException {
         if (propertyName.compareToIgnoreCase("kinetics") == 0) {
             try {
                 setKinetics(propertyValue);
             }
             catch (IllegalExpressionException iee) {
	        throw new InvalidPropertyValueException(iee.getMessage());
	     }
         }
         else if (propertyName.compareToIgnoreCase("stoichiometry") == 0) {
             setStoichiometry(propertyValue);
         }
         else if (propertyName.compareToIgnoreCase("X") == 0) {
            setX(Double.parseDouble(propertyValue));
        }
        else if (propertyName.compareToIgnoreCase("Y") == 0) {
           setY(Double.parseDouble(propertyValue));
         }

         else if (propertyName.compareToIgnoreCase("annotation") == 0 ) {
             setAnnotation(propertyValue);
         }
         else
            throw 
            new UnknownPropertyException("Unknown property for ProgressiveReaction:" + propertyName);    
     }
     
    public void update( double dt) {
	double x = getRate() * dt;
	Iterator itr = iterator();
	while(itr.hasNext()) {
	    Substance s = (Substance) itr.next();
	    s.setValue(s.getValue() + x * getCoefficient(s));
	}
    }

    public void computeRate() {
	rate = rateExpression.getValue();
    }

    public double getRate() {
	computeRate ();
	return rate;
    } 
    
    /** Getter for property kinetics.
     * @return Value of property kinetics.
     */
    public String getKinetics() {
        if (rateExpression != null)
            return rateExpression.toString();
        else 
            return null;
    }
    
    public void setStoichiometry(String s) {
        super.setStoichiometry(s);
        updateEntityList();
    }
    
    /**
     *   Compiles (1) a list of substances that participate (is consumed or produced, or serves as 
     *  an enzyme) in this reaction.
     *           (2) a list of paramters that define the kinetics of the reaction.
     */
    protected void updateEntityList() {
        clearParameters();
        clearCatalysts();
        if (rateExpression != null) {
            GeneralExpression [] exps = DMath.toArray(rateExpression);
            for (int i = 0; i < exps.length; i++) {
                if (exps[i] instanceof Substance) {
                    Substance s = (Substance) (exps[i]);
                    if (! this.contains(s)){
                        this.addSubstance(s, 0.0);
                     //   System.out.println(s);
                    }
                }
                
                if (exps[i] instanceof Parameter) {
                    Parameter p = (Parameter) (exps[i]);
                    if (! this.contains(p)) this.addParameter(p);
                }
            }
        }
    }
    
    /** Setter for property kinetics.
     * @param kinetics New value of property kinetics.
     */
    public void setKinetics(String kinetics) throws IllegalExpressionException {            
            try {
	        setRateExpression( ExpressionParser.parse(this.getSystem(), kinetics) );              
	    }

	    catch (IllegalExpressionException iee) {
	        throw iee;
	    }     
    }
    
    // returns the reaction fomula and the rate expression of the progressive reaction
    //
    // Added on 11/2/2000 by L. You.
    // updated 4/19/2005

    public String toString() {
        return(getFullName() + "{" + NEWLINE + 
        "  stoichiometry { " + getStoichiometry() + " } " + NEWLINE +
        "  kinetics { " + getKinetics() + " } " + NEWLINE +
        // the following two 'addition' were added 4/19/05 by LY
        "  X  {" + getX()+"}" + NEWLINE+
        "  Y  {" + getY()+"}" + NEWLINE+              
        "  Annotation {" + getAnnotation() + "}" + NEWLINE+
               "}" );
    }

    public javax.swing.JPanel editor() {
        return new dynetica.gui.reactions.ProgressiveReactionEditor(this);
    }
    
    public dynetica.gui.visualization.AbstractNode getNode() {
        if (this.node !=null) return this.node;
        return new dynetica.gui.visualization.ProgressiveRNode(this);
    }
    
    //
    //Added 5/2/2005. Create a deep copy of this object
    //
    public Object clone(){
        ProgressiveReaction pr = new ProgressiveReaction(this.name+"_clone", (ReactiveSystem) this.system);
        try {
            pr.setKinetics(this.kinetics);
        }
        catch (Exception e){}
        
        pr.setX(getX()+2);
        pr.setY(getY()+2);
        pr.setStoichiometry(this.getStoichiometry());
        pr.setAnnotation(this.getAnnotation());
        return pr;
    }
} // ProgressiveReaction
