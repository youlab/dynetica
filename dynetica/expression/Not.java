package dynetica.expression;

public class Not extends LogicalExpression {
    
    public Not(LogicalExpression a) {
	super(a);
	type = ExpressionConstants.NOT;
    }
    
    public void compute() { 
	double tempValue = a.getValue();
	if(tempValue > 0.5)
		value = 0.0; 
	else
		value = 1.0;
    }
    public String toString () { 
	return " !" + a.toString(); 
    }
} // Not
