/**
 * ExpressionBuilder.java
 *
 *
 * Created: Tue Aug 29 14:43:56 2000
 *
 * @author Lingchong You
 * @version 0.1
 */
package dynetica.expression;
import java.util.*;

/**
 *  ExpressionBuilder is used to build all kinds of expressions.
 *  Since only one object of ExpressionBuilder is needed always,
 *  it is designed as a singleton.
 */
public class ExpressionBuilder extends ExpressionConstants{
    public static ExpBuilder exprBuilder = ExpBuilder.getInstance();
    /**
     * Computes the type (an integer value) of a function or operator specified
     * by the parameter <code> expr </code>.
     * @param <code> expr </code> is the 
     *
     */

    public static GeneralExpression buildBinary(String expr, 
					 GeneralExpression a, 
					 GeneralExpression b) {
	switch (getType(expr)){
	case SUM:
	    return DMath.sum(a, b);
	case SUBSTRACT:
	    return DMath.substract(a, b);
	case MULTIPLY:
	    return DMath.multiply(a, b);
	case DIVIDE:
	    return DMath.divide(a, b);
	case POW:
	    return DMath.pow(a, b);
	case COMP:
	    return DMath.compare(a,b);
	default:
	    System.out.println("Shouldn't be here at binary: " + expr);
	    return null;
	}
    }

    public static GeneralExpression buildUnary(String expr, GeneralExpression a) {

	switch (getType(expr)) {
	case LOG:
	    return DMath.log(a);
	case EXP:
	    return DMath.exp(a);
	case SQRT:
	    return DMath.sqrt(a);
	case SIN:
	    return DMath.sin(a);
	case COS:
	    return DMath.cos(a);
	case TAN:
	    return DMath.tan(a);
            //abs added by Lingchong You 4/30/2005
        case ABS:
            return DMath.abs(a);
            
        //round(a), floor(a), ceil(a) added by LY, 8/2013    
        case ROUND:
            return DMath.round(a);
        case FLOOR:
            return DMath.floor(a);
        case CEIL:
            return DMath.ceil(a);
            
	case RANDENG:
	    return new DRandENG(a);
            
	default:
	    System.out.println("Shouldn't be here at unary: " + expr);
	    return null;
	}
    }

   public static GeneralExpression buildNonpara(String expr){
	switch(getType(expr)) {
	case RANDOMN:
		return new RandomN();
            case NORMAL:
                return new Normal();
	default:
		System.out.println("Unknown function");
		return null;
	}
   }

   public static GeneralExpression buildLogical(String expr, GeneralExpression a,
			GeneralExpression b) throws IllegalExpressionException{
	int type;
	type = getType(expr);
	switch(type){
	case AND:
		if( !(a instanceof LogicalExpression) && !(b instanceof LogicalExpression) ){
			throw new IllegalExpressionException("Only logical expression can be used as AND's parameters");
		} else {
			return new And((LogicalExpression)a,(LogicalExpression)b);
		}
	case OR:
		if( !(a instanceof LogicalExpression) && !(b instanceof LogicalExpression) ){
			throw new IllegalExpressionException("Only logical expression can be used as OR's parameters");
		} else {
			return new Or((LogicalExpression)a,(LogicalExpression)b);
		}
	case NOT:
		if( !(a instanceof LogicalExpression)){
			throw new IllegalExpressionException("Only logical expression can be used as NOT's parameter");
		} else {
			return new Not((LogicalExpression)a);
		}
	case GT:
		if( (a instanceof LogicalExpression) && (b instanceof LogicalExpression) ){
			throw new IllegalExpressionException("Only non-logical expression can be used as GT's parameters");
		} else {
			return new GT(a,b);
		}
	case GTE:
		if( (a instanceof LogicalExpression) && (b instanceof LogicalExpression) ){
			throw new IllegalExpressionException("Only non-logical expression can be used as GTE's parameters");
		} else {
			return new GTE(a,b);
		}
	case LT:
		if( (a instanceof LogicalExpression) && (b instanceof LogicalExpression) ){
			throw new IllegalExpressionException("Only non-logical expression can be used as LT's parameters");
		} else {
			return new LT(a,b);
		}
	case LTE:
		if( (a instanceof LogicalExpression) && (b instanceof LogicalExpression) ){
			throw new IllegalExpressionException("Only non-logical expression can be used as LTE's parameters");
		} else {
			return new LTE(a,b);
		}
	case EQ:
		if( (a instanceof LogicalExpression) && (b instanceof LogicalExpression) ){
			throw new IllegalExpressionException("Only non-logical expression can be used as EQ's parameters");
		} else {
			return new EQ(a,b);
		}
	case NEQ:
		if( (a instanceof LogicalExpression) && (b instanceof LogicalExpression) ){
			throw new IllegalExpressionException("Only non-logical expression can be used as NEQ's parameters");
		} else {
			return new NEQ(a,b);
		}
	default:
	    System.out.println("Shouldn't be here at logical: " + expr);
	    return null;
	}
	
   }

   public static GeneralExpression buildCondition(String expr, List list)
	throws IllegalExpressionException{
	int type;
	type = getType(expr);
	switch(type){
	case IF:
		if(!((list.get(0)) instanceof LogicalExpression)){
			throw new IllegalExpressionException("Only logical expression can be used as IF's first parameter");
		} else if ( (list.get(1)) instanceof LogicalExpression) {
			throw new IllegalExpressionException("Only non-logical expression can be used as IF's second parameter");
		} else {
			return new IF(DMath.linkToArray(list));
		}
	case ELSE:
		int length = list.size();
		int if_else = length%2;
		int index = 0;
		if(if_else == 0){
			while(index < length){
			   if(!((list.get(index)) instanceof LogicalExpression)){
				throw new IllegalExpressionException("Only logical expression can be used as IF's first parameter");
			   } else if ( (list.get(index+1)) instanceof LogicalExpression) {
				throw new IllegalExpressionException("Only non-logical expression can be used as IF's second parameter");
			   }
			   index = index + 2;
			}
			return new Else(DMath.linkToArray(list));
		} else {
			while(index < length - 1){
			   if(!((list.get(index)) instanceof LogicalExpression)){
				throw new IllegalExpressionException("Only logical expression can be used as IF's first parameter");
			   } else if ( (list.get(index+1)) instanceof LogicalExpression) {
				throw new IllegalExpressionException("Only non-logical expression can be used as IF's second parameter");
			   }
			   index = index + 2;
			}
			if((list.get(index)) instanceof LogicalExpression) {
				throw new IllegalExpressionException("Only non-logical expression can be used as IF's second parameter");
			} else {
				return new Else(DMath.linkToArray(list));
			}
		}			
				
	default:
	    System.out.println("Shouldn't be here at Condition: " + expr);
	    return null;
	}
   }
	
   public static GeneralExpression buildMulti(String expr, List list) 
	throws IllegalExpressionException{
	 int type;
       if(isFunction(expr)) 
       {
            type = getType(expr);
       }
       else
       {
            type = exprBuilder.getType(expr);
       }
       switch (type) {
       case STEP: return DMath.step(list);
       case MAX: return DMath.max(list);
       case MIN: return DMath.min(list);
       case SUMM: return DMath.sum(list);
	 case COMP:
		if(list.size() != Compare.parameter_num){
			throw new IllegalExpressionException("Incorrect use of:"+ expr);
		}
                else
                {
			return DMath.compare(list);
                }
	 
       case MUL: return DMath.mul(list);
	 case PULSE:
		if(list.size() != Pulse.parameter_num){
			throw new IllegalExpressionException("Incorrect # of parameters for:"+ expr);
		}
                else
                {
			return new Pulse(DMath.linkToArray(list));
                }
           // 4/30/2005
           // the following was added by Lingchong You to handle the pulses function.
           case PULSES:
                if (list.size() != Pulses.parameter_num) {
                    throw new IllegalExpressionException("Incorrect # of parameters for:"+ expr);
                }
                else
                {
                    return new Pulses(DMath.linkToArray(list));
                }
           // 5/3/2006; added by Lingchong You to handle the triggerAt(x1,x2) function.
           case TRIGGERAT:
                if (list.size() != TriggerAt.parameter_num) {
                    throw new IllegalExpressionException("Incorrect # of parameters for:"+ expr);
                }
                else
                {
                    return new TriggerAt(DMath.linkToArray(list));
                }  
               //1/13/2013: added by Lingchong You to handle Hill function 
           case HILL: 
               if (list.size() != Hill.parameter_num) {
                   throw new IllegalExpressionException ("Incorrect # of parameters for:" + expr);
               }
               else
               {
                   return new Hill(DMath.linkToArray(list));
               } 
               //7/31/2013: added by Lingchong You to handle Delay function 
           case DELAY: 
               if (list.size() != Delay.parameter_num) {
                   throw new IllegalExpressionException ("Incorrect # of parameters for:" + expr);
               }
               else
               {
                   return new Delay(DMath.linkToArray(list));
               } 

	 case RANDOM:
		if(list.size() != DRandom.parameter_num){
			throw new IllegalExpressionException("Incorrect # of parameters for:"+ expr);
		}
                else
                {
			return new DRandom(DMath.linkToArray(list));
                }
       default:
		try{
		   if(!exprBuilder.isCustomFunc(expr)){
			throw new IllegalExpressionException("Function " + expr +
						 " is not in the functdata base.");
		   } else if(exprBuilder.getLength(expr) != list.size()){
			throw new IllegalExpressionException("Incorrect # of parameters for:"+ expr);
		   }else {
			return new CustomBuilder(expr, DMath.linkToArray(list));
		   }
		}catch (java.io.FileNotFoundException err){
			throw new IllegalExpressionException("Function for:"+ expr + " Not Found");
		}catch (java.io.IOException err1){
			throw new IllegalExpressionException("Function for:"+ expr + " Not Found");
		}
       }
   }

} // ExpressionBuilder
