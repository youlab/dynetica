//
// Function unclear (implemented by Apriak)
//

package dynetica.expression;

public class DRandENG extends Expression {

    public DRandENG(GeneralExpression a) {
	super(a);
	type = ExpressionConstants.RANDENG;
    }
    
    public void compute() {
	double x = a.getValue();
	double y = (-1/x)*(Math.log(1 - ExpressionConstants.doubleNumber.nextDouble()));
	value = y;
    }
  public String toString () {
	return "randENG(" + a + ")";
  }
} // End