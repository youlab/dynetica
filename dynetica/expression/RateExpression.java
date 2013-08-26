/**
 * RateExpression.java
 *
 *
 * Created: Tue Aug 29 14:33:49 2000
 *
 * @author Lingchong You
 * @version 0.01
 */
//
// Log: 8/29/2000. The class RateExpression can now correctly parse rate
// expressions with binary operators, inlcuding  +, -, *, /, and ^.
// The rate expression should be specified after the following rules:
//  1) A Substance name should be enclosed in a pair of brackets
//  2) A Parameter name should appear without brackets.
//
// Here is an example of correct rate expression:
//    k_Parameter * [SubstanceX] ^ 2.0 / [SubstanceY] ^ 0.5 * ( [SubstanceZ] - 180)
//
//
//
// Todo: (1) improve the algorithm so that it can be more accurate in identifying
//           and reporting errors in the specified rate expression.
//       (2) implement some unary operators, such as Log, Exp, Sqrt, Abs, etc.
//       (3) implement logic operations (AND, OR, XOR, NOT).
//
//

package dynetica.expression;
import java.util.*;
import dynetica.entity.*;
import dynetica.system.*;

public class RateExpression extends Entity {
    private Stack operandStack = new Stack();
    private Stack operatorStack = new Stack();
    private GeneralExpression expression;
    private String inputStr;
    private ExpressionConstants exprBuilder = ExpressionConstants.getInstance();

    public RateExpression() {
	this(null, null);
    }

    public RateExpression(String input, ReactiveSystem system) {
	super(null, system);
        if (input!=null)
	inputStr = input.trim();
        //parse();
    }

    public RateExpression(GeneralExpression ge) {
	expression = ge;
    }

    public String toString() {
	return expression.toString();
    }

    public double getValue() { return expression.getValue(); }

    public GeneralExpression getExpression() {return expression;}
    
    public void absorb(String operation, GeneralExpression ge) {
	expression = ExpressionBuilder.buildBinary(operation, expression, ge);
    }

    public void parse() throws IllegalExpressionException{
	int i = 0; // index of the character
	int previousTokenType = 0;
	int tokenType = 1;

	while (i < inputStr.length()) {
	    char c = inputStr.charAt(i);
	    //	    System.out.println(c + " " + i + " " + inputStr.length());
	    if (Character.isWhitespace(c)) i++;
		
	    //
	    // a token starting with a letter is either a Parameter or
	    // a unary operator as defined in class Operator.
	    // 
	    // a parameter name should contain only letters and digits.
	    //
	    else if (Character.isLetter(c)) {
		int j = i;
		while ( i < inputStr.length() && 
			Character.isLetterOrDigit(inputStr.charAt(i++)));
		String name = inputStr.substring(j,i).trim();
		if (! exprBuilder.isFunction(name)) {
		    //
		    // if the super system has the Parameter
		    //
		    if (getSystem().contains(name)) {
			if (! (getSystem().get(name) instanceof Parameter)) 
			    System.out.println("Warning:" + name + " is not a parameter");
			operandStack.push(getSystem().getEntity(name));
		    }
		    //
		    // if the super system doesn't have the parameter, creat one.
		    //
		    else {
			Parameter p = new Parameter(name, getSystem());
			getSystem().addEntity(p);
			operandStack.push(p);
		    }
	
		}

		else {
		    //		    Operator op = new Operator(name);
		}
	    }
	    //
	    // Substance names are enclosed by brackets.
	    //
	    else if (c == '[') {
		int j = i;
		while (i < inputStr.length() && inputStr.charAt(i++) != ']') {
		    if ( !Character.isLetterOrDigit(inputStr.charAt(i)) &&
			 inputStr.charAt(i) != ']') {
			throw new IllegalExpressionException("Bad substance name:"+ 
							     inputStr + " " + 
							     inputStr.charAt(i));
		    }
		    //i++;
		}
		if (i == inputStr.length() && (inputStr.charAt(i-1) != ']'))
		    throw new IllegalExpressionException("Bad substance name:"+ inputStr +
						     "\n Unmatched \"[\"");
		if (i == j+2) 
		    throw new IllegalExpressionException("Empty substance name:" + inputStr);

		String substanceName = inputStr.substring(j+1, i-1);
		if (exprBuilder.isFunction(substanceName))
		    throw new IllegalExpressionException("You used function name: " +
							 substanceName + " as substance name");

		if (getSystem().hasEntity(substanceName))  
		    operandStack.push(getSystem().getSubstance(substanceName));
		else {
		    System.out.println("Warning: The system doesn't have the specified substance: " + substanceName);
		    System.out.println("Warning: A new substance with the given name will be created.");
		    Substance s = new Substance(substanceName, getSystem());
		    getSystem().addEntity(s);
		    operandStack.push(s);
		}
	    }		
		    

	    else if (exprBuilder.isOperator(c)) {
		switch (c) {
		case '(':
		    operatorStack.push("(");
		    break;
		case ')':
		    String op = (String) operatorStack.peek();
		    while (op.compareTo("(") != 0) {
			//System.out.println(op);
			doBinary(op);
			operatorStack.pop();
			op = (String) operatorStack.peek();
		    }
		    //
		    // pop off the left parenthesis.
		    //
		    operatorStack.pop();
		    break;
		case '+':
		case '-':
		case '*':
		case '/':
		case '^':
		    processOp(String.valueOf(c));
		    break;
		}
		i++;
	    }

	    else if (Character.isDigit(c)) {
		//System.out.println("Got a number");
		int j = i;
		while (i < inputStr.length() && 
		       (Character.isDigit(inputStr.charAt(i)) || 
		       inputStr.charAt(i) == '.')) i ++;
		String aNumber = inputStr.substring(j, i);
		operandStack.push(new Constant(Double.parseDouble(aNumber)));
	    }
	}
	
	while (! operatorStack.empty()) {
	    doBinary((String) operatorStack.peek());
	    operatorStack.pop();
	}
	
	expression = (GeneralExpression) operandStack.pop();
    }

     private void doBinary (String theOp) {
	GeneralExpression b = (GeneralExpression) operandStack.peek();
	operandStack.pop();
	GeneralExpression a = (GeneralExpression) operandStack.peek();
	operandStack.pop();
	operandStack.push(ExpressionBuilder.buildBinary(theOp, a, b));
    }

    private void processOp(String theOp) {
	while ( (! operatorStack.empty()) && 
		(ExpressionConstants.getType(theOp) < ExpressionConstants.getType((String) operatorStack.peek())) ){
	    doBinary((String) operatorStack.peek());
	    operatorStack.pop();
	}
	
	operatorStack.push(theOp);
    } 
    
    public Object clone(){
        //needs to revise.
        return null;
    }
    
} // RateExpression
