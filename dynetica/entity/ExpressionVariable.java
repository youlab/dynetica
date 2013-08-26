/*
 * ExpressionVariable.java
 *
 * Created on April 19, 2005, 6:44 PM
 */
package dynetica.entity;

import dynetica.system.*;
import dynetica.expression.*;
import dynetica.exception.*;

import java.util.*;

/**
 *
 * @author Lingchong You
 */
public class ExpressionVariable extends Substance {
  private static int expressionIndex = 0;
  // list of substances that this expression is dependent on.
  private List substances = new ArrayList();

    /**
     * Holds value of property expression.
     */
    private dynetica.expression.GeneralExpression expression;
    
    /** Creates a new instance of ExpressionVariable */
    public ExpressionVariable() {
      this("Expression"+expressionIndex++, null,null);
    }
    public ExpressionVariable(String name, String expStr, AbstractSystem system) {
        setName(name);
        setSystem(system);
        setExpression(expStr);
     }
    
    /**
     * Getter for property expression.
     * @return Value of property expression.
     */
    public dynetica.expression.GeneralExpression getExpression() {

        return this.expression;
    }

    /**
     * Setter for property expression.
     * @param expression New value of property expression.
     */
    public void setExpression(dynetica.expression.GeneralExpression expression) {

        this.expression = expression;
        updateSubstancesList();
        if (getSystem()!=null) getSystem().fireSystemStateChange();

    }

    public void setExpression(String expStr) {
        if (expStr!=null)
         try {
              this.expression = ExpressionParser.parse(this.getSystem(), expStr);
              updateSubstancesList();
              if (getSystem()!=null) getSystem().fireSystemStateChange();

 	    }

	    catch (IllegalExpressionException iee) {
	        System.out.println(iee);
	    }     
    }
    
    public List getSubstances(){
        return substances;
    }
    protected void updateSubstancesList() {
        substances.clear();
        if (expression != null) {
            GeneralExpression [] exps = DMath.toArray(expression);
            for (int i = 0; i < exps.length; i++) {
                if (exps[i] instanceof Substance) {
                    Substance s = (Substance) (exps[i]);
                    if (! substances.contains(s)) substances.add(s);
                }
        }
       }
    }
    
//  @Override
//  Lingchong You Note: As the expression should be properly updated in simualtions using update(). There should be no need to override 
    // getValue(), which is defined in EntityVariable.java.
    //
//    public double getValue() {
//        return expression.getValue();
//    }
    
//
// Added 05/04/2006, by Lingchong You to allow correct updating of values in stochastic simulation.
// 
    public void update(){
        value = expression.getValue();
    }
    
    public String getExpressionString() {
        if (expression !=null)
            return expression.toString();
        return "";
    }
    
  @Override
    public String toString() {
	return getName();
    }
    
  @Override
    public String getCompleteInfo() {
        return (  getFullName() + " {" +  NEWLINE + 
        " Expression { " + expression.toString() + "}" + NEWLINE +
                 // the getX and getY methods were implemented on 4/19/2005
            " X  {" + getX()+"}" + NEWLINE+
            " Y  {" + getY()+"}" + NEWLINE+              
       " Annotation { " + getAnnotation() + "}" + NEWLINE +
              "}" + NEWLINE);
    }
  
  @Override
 public void setProperty(String propertyName,String propertyValue) throws UnknownPropertyException, InvalidPropertyValueException {
        if (propertyName.compareToIgnoreCase("Expression") == 0)
            setExpression(propertyValue);
        else if (propertyName.compareToIgnoreCase("X") == 0) {
            setX(Double.parseDouble(propertyValue));
        }
        else if (propertyName.compareToIgnoreCase("Y") == 0) {
           setY(Double.parseDouble(propertyValue));
         }
      else if (propertyName.equalsIgnoreCase("annotation")) {
            setAnnotation(propertyValue);
       }
       else
            throw 
            new UnknownPropertyException(" Unknown property for ExpressionVariable " + propertyName);    
}
 
  @Override
   public javax.swing.JPanel editor() {
        return new dynetica.gui.entities.ExpressionEditor(this);
    }
   
  @Override
   public dynetica.gui.visualization.AbstractNode getNode() {
       if (this.node!=null) return this.node;
       return new dynetica.gui.visualization.ENode(this);
}
    
  @Override
   public Object clone(){
       ExpressionVariable newClone = new ExpressionVariable();
       newClone.setName(this.name + "_clone");
       newClone.setExpression(this.expression);
       return newClone;
   }
}
