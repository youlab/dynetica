//=================================================================
// CustomParser.java
// Last updated: Mon Aug 7, 1:23 2001
//	details
//	   -- Add comments
// @author Apirak Hoonlor
// @version 0.1
//=================================================================


//=================================================================
// CustomParser is used to parse the expression string which locates
// in "function.dat". 
// CustomParser is a "non-static" version of ExpressionParser. Since,
// it is a non-static class, it allows Custom functions to be parse
// multiple times. 
//=================================================================

package dynetica.expression;
import java.util.*;
import dynetica.entity.*;
import dynetica.system.*;

public class CustomParser{

    public Stack operandStack; 
    public Stack operatorStack;
    private   ExpressionConstants exprBuilder = ExpressionConstants.getInstance();
    private   ExpBuilder expBu = ExpBuilder.getInstance();
    

    public CustomParser(){
	operandStack = new Stack();
	operatorStack = new Stack();
    }	

    public GeneralExpression parse(AbstractSystem system, String inputStr) 
	throws IllegalExpressionException{
	int i = 0; // index of the character
	List commaList = new LinkedList();
	int flag = 0;
	
	while (i < inputStr.length()) {
	    char c = inputStr.charAt(i);
	    if (Character.isWhitespace(c)) i++;
	    
	    else if (Character.isLetter(c)&& !(exprBuilder.isCompareOp(c))
			&& !(isIfOp(inputStr, i)))   {
		int j = i;
		while ( i < inputStr.length() && 
			(Character.isLetterOrDigit(inputStr.charAt(i))||
			isValidChar(inputStr.charAt(i)))) i++;
		String name = inputStr.substring(j,i).trim();
		if ((!exprBuilder.isFunction(name)) && (!expBu.isCustomFunc(name))) {
		    //
		    // if the super system has the Parameter
		    //
		    if (system.hasEntity(name)) {
			if (! (system.getEntity(name) instanceof Parameter)) 
			    System.out.println("Warning:" + name + " is not a parameter");
			operandStack.push(system.getEntity(name));
		    }
		    //
		    // if the super system doesn't have the parameter, creat one.
		    //
		    else {
			Parameter p = new Parameter(name, system);
			system.addEntity(p);
			operandStack.push(p);
		    }
		    
		}
		
		else {
		    operatorStack.push(name);
		}
	    }
	    //
	    // Substance names are enclosed by brackets.
	    //
	    else if (c == '[') {
		int j = i;
		while (i < inputStr.length() && inputStr.charAt(i++) != ']') {
		    if ( !(Character.isLetterOrDigit(inputStr.charAt(i))||
			isValidChar(inputStr.charAt(i)) ) &&
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
		
		if (system.hasEntity(substanceName))  
		    operandStack.push(system.getSubstance(substanceName));
		else {
		    System.out.println("Warning: The system doesn't have the specified substance: " + substanceName);
		    System.out.println("Warning: A new substance with the given name will be created.");
		    Substance s = new Substance(substanceName, system);
		    system.addEntity(s);
		    operandStack.push(s);
		}
	    }		
	    
	    
	    else if (exprBuilder.isOperator(c)|| exprBuilder.isCompareOp(c) || isIfOp(inputStr, i))  {
		int opType = 0;
		int tempType = 0;
		switch (c) {
		case '(':
		    operatorStack.push("(");
		    break;
		case '{':
		    operatorStack.push("{");
		    break;
		case ')':
		    String op = (String) operatorStack.peek();
		    if(exprBuilder.isFunction(op)) 
		    	opType = exprBuilder.functionType(op);
		    else
			opType = expBu.functionType();
		    while (op != null && op.compareTo("(") != 0) {
			if(opType == 4){
			    doUnary(op);
			    operatorStack.pop();
			    
			}else if (opType == 7){
			    doNonPara(op);
			    operatorStack.pop();
			}else if(opType == 2){
			    
			    commaList = doComma();
			}else if(opType == 5){
			    doMulti(op,commaList);
			    operatorStack.pop();
			}else if(opType == 6){
			    doLogic(op);
			    operatorStack.pop();
				
			}else{
			    //System.out.println(op);
			    doBinary(op);
			    operatorStack.pop();
			}
			op = (String) operatorStack.peek();
		      if(exprBuilder.isFunction(op)) 
		    	    opType = exprBuilder.functionType(op);
		      else{
			    opType = expBu.functionType();
			}
		    }
		    //
		    // pop off the left parenthesis.
		    //
		    operatorStack.pop();
		    if(!operatorStack.empty()){
			String tempOp = (String) operatorStack.peek();
		    	if(exprBuilder.isFunction(tempOp)) 
		    		tempType = exprBuilder.functionType(tempOp);
		    	else
				tempType = expBu.functionType();
			if(tempType == 4){
			    doUnary(tempOp);
			    operatorStack.pop();
			}else if (opType == 7){
			    doNonPara(tempOp);
			    operatorStack.pop();
			}else if(tempType == 5){
			    doMulti(tempOp,commaList);
			    operatorStack.pop();
			}else if(tempType == 6){
			    if(!tempOp.equalsIgnoreCase("if")){
			    	doLogic(tempOp);
			    	operatorStack.pop();
			    }
			}
		    }else if(operatorStack.empty() && commaList.size() == 0 ){
				throw new IllegalExpressionException("Incorrect uses of parameter without function!!");
		    }
		    break;
		case '}':
		    String ope = (String) operatorStack.peek();
		    if(exprBuilder.isFunction(ope)) 
		    	opType = exprBuilder.functionType(ope);
		    else
			opType = expBu.functionType();
		    while (ope != null && ope.compareTo("{") != 0) {
			if(opType == 4){
			    doUnary(ope);
			    operatorStack.pop();
			    
			}else if (opType == 7){
			    doNonPara(ope);
			    operatorStack.pop();
			}else if(opType == 2){
			    
			    commaList = doComma();
			}else if(opType == 5){
			    doMulti(ope,commaList);
			    operatorStack.pop();
				
			}else if(opType == 6){
			    doLogic(ope);
			    operatorStack.pop();
			}else{
			    doBinary(ope);
			    operatorStack.pop();
			}
			ope = (String) operatorStack.peek();
		      if(exprBuilder.isFunction(ope)) 
		    	    opType = exprBuilder.functionType(ope);
		      else{
			    opType = expBu.functionType();
			}
		    }
		    //
		    // pop off the left parenthesis.
		    //
		    operatorStack.pop();
		    // pop off if
		    doLogic("if");
		    operatorStack.pop();
		    break;

		case '+':
		case '-':
		case '*':
		case '/':
		case '^':
		case ',':
		    processOp(String.valueOf(c));
		    break;
		case '<':
		    if(i < inputStr.length()){
		       if(inputStr.charAt(i+1) == '='){
		       	processOp("<=");
				i++;
		    	 }else {
		       	processOp(String.valueOf(c));
			 }
		     } else {
			throw new IllegalExpressionException("Missing Paramiters for Logical function!!");
 		     }
		    break;
		case '>':
		    if(i < inputStr.length()){
		       if(inputStr.charAt(i+1) == '='){
		       	processOp(">=");
				i++;
		    	 }else {
		       	processOp(String.valueOf(c));
			 }
		     } else {
			throw new IllegalExpressionException("Missing Paramiters for Logical function!!");
 		     }
		    break;
		case '!':
		    if(i < inputStr.length()){
		       if(inputStr.charAt(i+1) == '='){
		       	processOp("!=");
				i++;
		    	 }else {
		       	processOp(String.valueOf(c));
			 }
		     } else {
			throw new IllegalExpressionException("Missing Paramiters for Logical function!!");
 		     }
		    break;
		case '=':
		    if(i < inputStr.length() && inputStr.charAt(i+1) == '='){
		       	processOp("==");
				i++;
		    }else {
			throw new IllegalExpressionException("Missing Paramiters for Logical function!!");
 		    }
		    break;
		case '|':
		    if(i < inputStr.length() && inputStr.charAt(i+1) == '|'){
		       	processOp("||");
				i++;
		    }else {
			throw new IllegalExpressionException("Missing Paramiters for Logical function!!");
 		    }
		    break;
		case '&':
		    if(i < inputStr.length() && inputStr.charAt(i+1) == '&'){
		       	processOp("&&");
				i++;
		    }else {
			throw new IllegalExpressionException("Missing Paramiters for Logical function!!");
 		    }
		    break;
		case 'i':
		    if(isIfOp(inputStr,i)){
		       	processOp("if");
				i++;
		    }else {
			throw new IllegalExpressionException("Missing Paramiters for Logical function!!");
 		    }
		    break;

		}
		i++;
	    }
	    
	    else if (Character.isDigit(c)) {
		int j = i;
		while (i < inputStr.length() && 
		       (Character.isDigit(inputStr.charAt(i)) || 
		       inputStr.charAt(i) == '.'|| inputStr.charAt(i) == 'e' ||
			inputStr.charAt(i) == 'E')){
			if(inputStr.charAt(i) == 'E'||inputStr.charAt(i) == 'e'){
				if(inputStr.charAt(i+1) == '+' || inputStr.charAt(i+1) == '-'){
					i = i + 2;
				}else{
					i++;
				}
			}else
				i++;
		}
		String aNumber = inputStr.substring(j, i);
		operandStack.push(new Constant(parseNumber(aNumber)));
	    }
	}
	
	while (! operatorStack.empty()) {
	    int type = 0;
	    String op = (String) operatorStack.peek();
	    if(exprBuilder.isFunction(op))
	    	type = exprBuilder.functionType(op);
	    else
		type = expBu.functionType();
	    if(op.length() <= 1){
		if(type == 6) doLogic(op);
		else doBinary(op);
	    }else{
		if(type == 4) doUnary(op);
		else if(type == 7) doNonPara(op);
		else if(type == 6) doLogic(op);
		else doBinary(op);
	    }
	    operatorStack.pop();
	}	
	GeneralExpression result = (GeneralExpression) operandStack.pop();
	while(!operandStack.empty()) operandStack.pop();
	while(!operatorStack.empty()) operatorStack.pop();
	return result;
    }

    public   double parseNumber (String inputStr){
	int i = 0;
	int j;
	boolean positive = true;
	boolean multiply = false;
	boolean from = true;
	while (i < inputStr.length() && 
		       (Character.isDigit(inputStr.charAt(i)) || 
		       inputStr.charAt(i) == '.')) i++;
	String number = inputStr.substring(0,i);
	double base = Double.parseDouble(number);
	double power = 0;
	if (i < inputStr.length() &&(inputStr.charAt(i) == 'e' || inputStr.charAt(i) == 'E')){
		System.out.println("Not here");
		multiply = true;
		i++;
		if(!(Character.isDigit(inputStr.charAt(i)))){
			if(inputStr.charAt(i) == '-')
				positive = false;
			i++;
		}
		j = i;
		while (i < inputStr.length() && 
		       Character.isDigit(inputStr.charAt(i))) i++;
		number = inputStr.substring(j,i);
		power = Double.parseDouble(number);	
	}
	power = Math.pow(10.0,power);
	if(positive && multiply){
		return base*power;
	}else if(!positive && multiply){
		return base/power;
	}else 
		return base;
   }
	      
    private void doBinary (String theOp) {
	GeneralExpression b = (GeneralExpression) operandStack.peek();
	operandStack.pop();
	GeneralExpression a = (GeneralExpression) operandStack.peek();
	operandStack.pop();
	operandStack.push(ExpressionBuilder.buildBinary(theOp, a, b));
    }
    
    private void doUnary (String theOp){
	GeneralExpression a = (GeneralExpression) operandStack.peek();
	operandStack.pop();
	operandStack.push(ExpressionBuilder.buildUnary(theOp,a));
    }
    
    private void doLogic (String theOp)throws IllegalExpressionException{
	int theOp_type = exprBuilder.checkLogicType(theOp);
	if(theOp_type != 1){
	   GeneralExpression b = (GeneralExpression) operandStack.peek();
	   operandStack.pop();
	   GeneralExpression a = (GeneralExpression) operandStack.peek();
	   operandStack.pop();
	   operandStack.push(ExpressionBuilder.buildLogical(theOp, a, b));
	} else {
	   GeneralExpression b = null;
	   GeneralExpression a = (GeneralExpression) operandStack.peek();
	   operandStack.pop();
	   operandStack.push(ExpressionBuilder.buildLogical(theOp, a, b));
	} 	   
   }	

    private List doComma (){
	List newList = new LinkedList();
	GeneralExpression test1, test2;
	test1 = (GeneralExpression) operandStack.peek();
	operandStack.pop();
	newList.add(0,test1);
	String op = (String) operatorStack.peek();
	int opType;
	if(exprBuilder.isFunction(op))
		opType = exprBuilder.functionType(op);
	else
		opType = expBu.functionType();

	while( opType == 2 ){
	    test2 = (GeneralExpression) operandStack.peek();
	    operandStack.pop();
	    newList.add(0,test2);
	    operatorStack.pop();
	    op = (String) operatorStack.peek();
	    if(exprBuilder.isFunction(op))
		opType = exprBuilder.functionType(op);
	    else
		opType = expBu.functionType();
	}

	return newList;
    }
    
    private void doMulti (String theOp, List operandComma)
	throws IllegalExpressionException{
	operandStack.push(ExpressionBuilder.buildMulti(theOp, operandComma));
    }
    private void doNonPara (String theOp){
	operandStack.push(ExpressionBuilder.buildNonpara(theOp));
    }

    private void processOp(String theOp)throws IllegalExpressionException {
	String before;
	if(! operatorStack.empty()) {
	    before = (String) operatorStack.peek();
	    if ((!before.equalsIgnoreCase("(")) && (!before.equalsIgnoreCase("{")) && 
		(!((getPrecedence(theOp) == 1) && (getPrecedence(before) == 1))) &&
		getPrecedence(theOp) <= getPrecedence(before)) {		
		if(exprBuilder.isLogicOp(before)){
		   if(!before.equalsIgnoreCase("if"))
		   	doLogic(before);
		} else		
		   doBinary(before);
		operatorStack.pop();
		if(getPrecedence(theOp) == 1){
		   before = (String) operatorStack.peek();
		   while((!before.equalsIgnoreCase("(")) && (!before.equalsIgnoreCase(","))){
			if(exprBuilder.isLogicOp(before)){
		  	   if(!before.equalsIgnoreCase("if"))
		   	      doLogic(before);
			} else		
		   	   doBinary(before);
			operatorStack.pop();
			before = (String) operatorStack.peek();
		   }
		}

	    }
	}
	
	operatorStack.push(theOp);
    }

   public int getPrecedence(String op){ 
        int tempType;
	  if( (op.equalsIgnoreCase("{")) || (op.equalsIgnoreCase("}")))
		return 8;
	  if(exprBuilder.isFunction(op))
		tempType = exprBuilder.getType(op); 
	  else
		tempType = expBu.getType(op);
        if(tempType == 0 || tempType == 100) return 8; 
        else if(tempType == 1 || tempType == 2) return 4;
	  else if(tempType == 99) return 1;
        else if(tempType == 5) return 6;
	  else if(tempType >= 50 && tempType <= 59) return 2;
	  else if(tempType >= 61 && tempType <= 74) return 3;  
        else if(tempType > 7 && tempType < 99) return 7; 
        else return 5; 
   } 

    // isValidChar will check whether the character is valid to be a name or not
    // To add more valid character just put more case in the switch cause.

    private boolean isValidChar(char c){
	switch(c){
	case '_': return true;
	case '.': return true;
	case '$': return true;
	case '~': return true;
	case '-': return true;
	default: return false;
        }
    }

   private boolean isIfOp(String testSt, int index){
	if(testSt.charAt(index) != 'i')
		return false;
	else if(testSt.charAt(index+1) != 'f')
		return false;
	else 
		return true;
   }

    public GeneralExpression simplify(GeneralExpression a){return null;}
	
} // CustomParser
