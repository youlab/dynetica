package dynetica.expression;

public class Compare extends FunctionExpression {
    public static int parameter_num = 2;
    public Compare(GeneralExpression[] a) {
	super(a);
	type = ExpressionConstants.COMP;
    }
    
    public void compute() {
	double xx = variables[0].getValue();
	double yy = variables[1].getValue();
	if(xx > yy)   value =  1;
	else if(xx < yy)   value = -1;
	else        value =  0;
    }
  public String toString () { return "compare(" + variables[0].toString() 
					+ ", " + variables[1].toString() + ")"; }
} // End