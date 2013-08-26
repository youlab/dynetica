package dynetica.expression;

public class DRandom extends FunctionExpression {
    public static int parameter_num = 2;

    public DRandom(GeneralExpression[] a) {
	super(a);
	type = ExpressionConstants.RANDOM;
    }
    
    public void compute() {
	double x = variables[0].getValue();
	double y = variables[1].getValue();
	double diff = y - x;
	double diffRand = ExpressionConstants.doubleNumber.nextDouble();
	double randNum = diffRand*diff;
	value = x + randNum;
    }
  public String toString () {
	return "random(" + variables[0] + " ," + variables[1] + " )";
  }
} // End