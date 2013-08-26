
/**
 * ExpressionConstants.java
 *
 *
 * Created: Tue Aug 29 15:04:51 2000
 * Rewrited: Fri Nov 17 16:00:00 2000
 * @author Lingchong You
 * @version
 */
package dynetica.expression;
import java.util.*;

public class ExpressionConstants  {
    
    public static final int LEFT_PAREN = 0; // (
    public static final int RIGHT_PAREN = 100; // )

    //binary expressions
    public static final int SUM = 1; //  a + b
    public static final int SUBSTRACT = 2; // a - b
    public static final int MULTIPLY = 3; // a * b
    public static final int DIVIDE = 4; //  a / b
    public static final int POW = 5;  // a ^ b
    

    //unary expressions.

    ///
    public static final int LOG = 10; // log(a)
    public static final int EXP = 11; // exp(a)
    public static final int SQRT = 12; // sqrt(a)
    public static final int SIN = 13;  // sin(a)
    public static final int COS = 14;  // cos(a)
    public static final int TAN = 15; // tan(a)
    public static final int ABS = 16; //abs(a)
    public static final int RANDENG = 17; //what is this for?
    
    public static final int CEIL = 18; // Math.ceil()
    public static final int ROUND = 19; // Math.round(a)
    public static final int FLOOR = 20; // Math.floor(a)
    
    
    public static final int COMP = 21;//compare(a,b)
    public static final int STEP = 22;//step(a,b)
    public static final int MAX = 23; // max(a, b)
    public static final int MIN = 24; // min(a, b)
    public static final int SUMM = 25;
    public static final int MUL = 26;
    public static final int COMPI = 27;
    public static final int PULSE = 28; 
    public static final int RANDOM = 29;
    
    
    //added by Lingchong You 4/30/05 to generate a continuous sequence of square pulses.
    public static final int PULSES = 30; //pulses(T, start, period, duration)
    public static final int TRIGGERAT = 31; //triggerAt(x0, x1, x2)
    
    //added by Lingchong You 1/13/13 to implement the Hill function
    public static final int HILL = 33;
    
    //added by Lingchong You 7/31/13 to implement the Delay function (to allow solution of Delay differential equations).
    public static final int DELAY = 34; // delay(substance, time_delay)

    //Expressions with no parameters
    public static final int RANDOMN = 41;
    
    // added by Lingchong You 4/30/05 to indicate the expression normal();
    public static final int NORMAL = 42;

    //logic expression.
    public static final int NOT = 50;
    public static final int AND = 51;
    public static final int OR = 52;
    public static final int IF = 53;
    public static final int ELSE = 54;
    public static final int ELSE_IF = 55;
    public static final int GT = 61;
    public static final int GTE = 62;
    public static final int LT = 63;
    public static final int LTE = 64;
    public static final int EQ = 65;
    public static final int NEQ = 66;
    public static final int COMMA = 99;// ,

//
// generates a random number.
//
    public static Random doubleNumber = new Random();

    protected static Map exprMap = new HashMap();
    protected static ExpressionConstants instance = new ExpressionConstants();

    protected ExpressionConstants() {
	exprMap.put("(", new Integer(LEFT_PAREN) );
	exprMap.put(")", new Integer(RIGHT_PAREN) );
	exprMap.put("+", new Integer(SUM) );
	exprMap.put("-", new Integer(SUBSTRACT) );
	exprMap.put("*", new Integer(MULTIPLY) );	
	exprMap.put("/", new Integer(DIVIDE) );	
	exprMap.put("^", new Integer(POW) );
	exprMap.put(",", new Integer(COMMA));
	exprMap.put("!", new Integer(NOT));
	exprMap.put("&&", new Integer(AND));
	exprMap.put("||", new Integer(OR));
	exprMap.put("if", new Integer(IF));
	exprMap.put("else", new Integer(ELSE));
	exprMap.put("else_if", new Integer(ELSE_IF));
	exprMap.put(">", new Integer(GT));
	exprMap.put(">=", new Integer(GTE));
	exprMap.put("<", new Integer(LT));
	exprMap.put("<=", new Integer(LTE));
	exprMap.put("==", new Integer(EQ));
	exprMap.put("!=", new Integer(NEQ));
	exprMap.put("max", new Integer(MAX) );	
	exprMap.put("min", new Integer(MIN) );	
	exprMap.put("log", new Integer(LOG) );	
	exprMap.put("exp", new Integer(EXP) );	
	exprMap.put("sqrt", new Integer(SQRT) );	
	exprMap.put("sin", new Integer(SIN) );	
	exprMap.put("cos", new Integer(COS) );	
	exprMap.put("tan", new Integer(TAN) );
	exprMap.put("mul", new Integer(MUL) );
	exprMap.put("sum", new Integer(SUMM));
	exprMap.put("compare", new Integer(COMP));
	exprMap.put("compareI", new Integer(COMPI));
	exprMap.put("step",new Integer(STEP));
	exprMap.put("pulse",new Integer(PULSE));
	exprMap.put("random", new Integer(RANDOM));
	exprMap.put("randENG", new Integer(RANDENG));
	exprMap.put("rand", new Integer(RANDOMN));
        
        // added by Lingchong You 4/30/2005
        exprMap.put("normal", new Integer(NORMAL));
        exprMap.put("pulses", new Integer(PULSES));
        exprMap.put("abs", new Integer(ABS));
        exprMap.put("triggerAt", new Integer(TRIGGERAT));
        
        // added by Lingchong You 1/13/2013
        exprMap.put("hill", new Integer(HILL));
        //added by Lingchong You 7/31/2013
        exprMap.put("delay", new Integer(DELAY));
        
        //added by LY 8/2013
        exprMap.put("round", new Integer(ROUND));
        exprMap.put("floor", new Integer(FLOOR));
        exprMap.put("ceil", new Integer(CEIL));
        
    }
    
    public static int getType(String expr) {
	return ((Integer) exprMap.get(expr)).intValue();
    }

    public static ExpressionConstants getInstance() {
	return instance;
    }

    public static boolean isOperator(char c) {
	return ( c == '(' || c == ')' || c == '+' ||
		 c == '-' || c == '*' || c == '/' ||
		 c == '^' || c == ',' || c == '{' ||
		 c == '}');
    }

    public static boolean isCompareOp(char c) {
	return ( c == '<' || c == '>' || c == '=' ||
		 c == '!' || c == '&' || c == '|');
    }

    public static int checkLogicType(String check){
	if(check.equalsIgnoreCase("!"))
		return 1;
	else if(check.equalsIgnoreCase("If") ||check.equalsIgnoreCase("else")
		|| check.equalsIgnoreCase("else_if"))
		return 2;
	else 
		return 0;
    }

    public static boolean isLogicOp(String check){
	return ( check.equalsIgnoreCase("!") || check.equalsIgnoreCase("&&") ||
		check.equalsIgnoreCase("||") || check.equalsIgnoreCase(">") ||
		check.equalsIgnoreCase(">=") || check.equalsIgnoreCase("<") ||
		check.equalsIgnoreCase("<=") || check.equalsIgnoreCase("==") ||
		check.equalsIgnoreCase("!=") || check.equalsIgnoreCase("if") ||
		check.equalsIgnoreCase("else") || check.equalsIgnoreCase("else_if"));
   }

    public static boolean isBracket(char c) {
	return (c == '[' || c == ']');
    }

    public static boolean isFunction(String name) {
	return exprMap.containsKey(name);
    }
    
    public static int functionType(String name) {
	int type = 0;
	if(exprMap.containsKey(name)){
		type = ((Integer)exprMap.get(name)).intValue();
		if(type == 0 || type == 100) 
                    return 1;      // left or right parenthesis.
		else if(type == 99) 
                    return 2;               // comma
		else if(type >= 1 && type <= 9) 
                    return 3;   // binary functions
		else if(type >= 10 && type <= 20) 
                    return 4; // unary functions
		else if(type == 60) 
                    return 5;
		else if(type >= 21 && type <= 40) 
                    return 5;  //functional expressions (each requiring multiple arguments.
		else if(type >= 41 && type <= 49) 
                    return 7; //non parameter, such as rand();
		else if(type >= 50 && type <= 59) 
                    return 6; //logical expressions
		else if (type >= 61 && type <= 70) 
                    return 6;
                else 
                    return -1;
	}
        else
		return 0;
    }

} // ExpressionConstants
