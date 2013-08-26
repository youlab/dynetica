//=================================================================
// CustomBuilder.java
// Last updated: Mon Aug 7, 1:11 2001
//	details
//	   -- Add comments
// @author Apirak Hoonlor
// @version 0.1
//=================================================================


package dynetica.expression;
import dynetica.entity.*;
import dynetica.system.*;
import java.util.*;

/**
 *  CustomBuilder is used to build all kinds of expressions.
 *  Since only one object of CustomBuilder is needed always,
 *  it is designed as a singleton.
 */

public class CustomBuilder extends FunctionExpression{

   // Data members
   // exprBuilder -- current data of all Custom functions
   // currentExpr -- keep the expression of this custom function
   //			   with the given operands
   // function_name -- the name of this custom function

   public static ExpBuilder exprBuilder = ExpBuilder.getInstance(); 
   public GeneralExpression currentExpr;
   public String function_name;


   // CustomBuilder
   // This constructor will create the currentExpr using ExpBuilder.
   // In exception case, if the IllegalExpression is throw, null is asign
   // to currentExpr to indecate the error.
   // Parameters: funcName -- name of the custom function to be create
   // 		operands -- lists of all operands in funcName
   // Return: None
   // Precondition: None
   // Postcondition: If the IllegalExpression is throw, null is asign
   // to currentExpr to indecate the error.

   public CustomBuilder(String funcName, GeneralExpression[] operands){
	type = 60;
	try{
		variables = operands;
		function_name = funcName;
		currentExpr = exprBuilder.creatExp(funcName, operands);
	}
        catch (IllegalExpressionException err){
		System.err.println("Function " + funcName 
					+ "in function.dat could not be complie.");
		currentExpr = null;
	}
	catch (java.io.IOException err) {
        }
   }

   // toString
   // return the display of this custom function with its operands.

   public String toString(){
	int i = 0;
	StringBuffer sb = new StringBuffer(function_name);
	sb.append("( ");
	for(i = 0; i < (variables.length - 1); i++){
		sb.append(variables[i] + " , ");
	}
	sb.append(variables[i] + " )");
	return sb.toString();
   }

   // compute
   // return the current value of currentExpr

   public void compute(){
	value = currentExpr.getValue();
   }

   // customExpr
   // this function will return the current expression of this 
   // custom function

   public GeneralExpression customExpr(){
	return currentExpr;
   }

   // getType
   // return type of this custom function.

   public int getType(){
	return type;
   }

} // CustomBuilder
