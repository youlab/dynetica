package dynetica.expression;

public class GTE extends LogicalExpression {
    
    public GTE(GeneralExpression a, GeneralExpression b) {
	super(a, b);
	type = ExpressionConstants.GTE;
    }
    
    public void compute() { 
	if(a.getValue() >= b.getValue())
		value = 1.0; 
	else
		value = 0.0;
    }
    public String toString () { 
	return a.toString()  + " >= " + b.toString(); 
    }
} // GTE
