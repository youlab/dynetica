
/**
 * MichaelisMentenReaction.java
 *
 *
 * Created: Wed Aug 23 17:27:56 2000
 *
 *
 * @author Lingchong You
 * @version 0.02
 * Modified by Lingchong You on 2/9/2001
 * Todo: it's probably better to define vmax and km as Parameters
 */
package dynetica.reaction;
import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.system.*;
import java.util.*;

public class MichaelisMentenReaction extends ProgressiveReaction {
    private static int mmReactionIndex = 0;
    private GeneralExpression vmax; 
    private GeneralExpression km;
    private Substance enzyme;
    private Substance substrate;
    private Substance product;
        
    public MichaelisMentenReaction() {
	this("MichaelisMentenReaction" + mmReactionIndex++, null, null, null);
    }
    
    public MichaelisMentenReaction(String name, 
    ReactiveSystem system, 
    GeneralExpression vmax, 
    GeneralExpression km) {
	super(name, system);
	this.vmax = vmax;
	this.km = km;
    }

    public void setSubstrate(Substance s) {
        remove(substrate);
        this.substrate = s;
	addSubstance(s, -1.0);
        updateRateExpression();
    }
    
    public Substance getSubstrate() {
        return substrate;
    }
    
    public void setEnzyme(Substance e) {
        remove(enzyme);
	this.enzyme = e;
	addSubstance(e, 0.0);
        updateRateExpression();
    }
    
    public Substance getEnzyme() {
        return enzyme;
    }
    
    public void setProduct(Substance p) {
        remove(product);
	this.product = p;
	addSubstance(p, 1.0);
        updateRateExpression();
    }
    
    public Substance getProduct() {
        return product;
    }
    

    /**
       * Get the value of vmax.
       * @return Value of vmax.
       */
    public GeneralExpression getVmax() {return vmax;}
    
    /**
       * Set the value of vmax.
       * @param v  Value to assign to vmax.
       */
    public void setVmax(GeneralExpression v) {
        this.vmax = v;
        updateRateExpression();
    }
    
    public void setVmax(String expression) throws IllegalExpressionException {
        try {
            setVmax(ExpressionParser.parse(getSystem (), expression));
        }
        catch (IllegalExpressionException iee ) {
            throw iee;
        }
    }
    
    
    /**
       * Get the value of km.
       * @return Value of km.
       */
    public GeneralExpression getKm() {return km;}
    
    /**
       * Set the value of km.
       * @param v  Value to assign to km.
       */
    public void setKm(GeneralExpression v) {
        this.km = v;
        updateRateExpression();
    }
    
    public void setKm(String expression) throws IllegalExpressionException {
        try {
            setKm(ExpressionParser.parse(getSystem (), expression));
        }
        catch (IllegalExpressionException iee ) {
            throw iee;
        }
    }

   
    
    
    //
    // the following method updates the rate expression after a change is made to
    // any listed substance or parameter. called by setProperty.
    //
    private void updateRateExpression() {
        if ( substrate != null && enzyme != null && product != null 
        && km != null && vmax != null) {
           
            //
            // the following updating scheme does not
            // depend on the expression parse tree, thus is more robust.
            //
             GeneralExpression e = DMath.divide( DMath.multiply(DMath.multiply(vmax, enzyme), substrate),
                                        DMath.sum(km, substrate));
             setRateExpression(e);
        }
    }
     public void setProperty(String propertyName, String propertyValue)
     throws UnknownPropertyException, InvalidPropertyValueException{
         if (propertyName.compareTo("substrate") == 0) {
             setSubstrate(getSystem().getSubstance(propertyValue));
         }
         else if (propertyName.compareTo("product") == 0) {
             setProduct(getSystem().getSubstance(propertyValue));
         }
         else if (propertyName.compareTo("enzyme") == 0) {
             setEnzyme(getSystem().getSubstance(propertyValue));
         }
         
         else if (propertyName.compareTo("vmax") == 0) {
             try {
              setVmax(propertyValue);
             }
             catch ( IllegalExpressionException iee ) {
                throw new InvalidPropertyValueException(iee.getMessage());
             }
         }
         
         else if (propertyName.compareTo("km") == 0) {
             try {
               setKm(propertyValue);
             }
             catch (IllegalExpressionException iee) {
                 throw new InvalidPropertyValueException(iee.getMessage());
             }
         }
         
        // the follow two IF statements were added 4/19/2005
        else if (propertyName.compareToIgnoreCase("X") == 0) {
            setX(Double.parseDouble(propertyValue));
        }
        else if (propertyName.compareToIgnoreCase("Y") == 0) {
           setY(Double.parseDouble(propertyValue));
         }

         else
            throw 
            new UnknownPropertyException("Unknown property for ProgressiveReaction:" + propertyName);   
     }
          
     public String toString() {
         StringBuffer tmp = new StringBuffer ( getFullName() 
                 + " {" + NEWLINE);
         if (substrate != null) 
             tmp.append("  substrate { " + substrate.getName() + "}" + NEWLINE);
         if (product != null)
             tmp.append("  product { " + product.getName() + "}" + NEWLINE);
         if (enzyme != null)
             tmp.append("  enzyme { " + enzyme.getName() + " } " + NEWLINE);
         if (vmax != null)
             tmp.append("  vmax { " + vmax.toString() + " } " + NEWLINE);
         if (km != null)
             tmp.append("  km { " + km.toString() + " } " + NEWLINE);
         
         //added 4/19/2005 by LY
         tmp.append ("  X {" + getX() + "}" + NEWLINE+ 
                 "  Y  {" + getY()+"}" + NEWLINE);
         
         tmp.append("}");
         return tmp.toString();
    }
    
    public javax.swing.JPanel editor() {
       return new dynetica.gui.reactions.MichaelisMentenReactionEditor(this);
    }
    
} // MichaelisMentenReaction
