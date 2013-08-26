/**
 * Sum.java
 *
 *
 * Created: Tue Aug 29 12:56:27 2000
 *
 * @author Lingchong You
 * @version 0.01
 */
package dynetica.expression;

public class Sum extends SimpleOperator{
    
    public Sum(GeneralExpression a, GeneralExpression b) {
	super('+', a, b);
	type = ExpressionConstants.SUM;
    }

    @Override
    public void compute() {
	value = a.getValue() + b.getValue();
    }
    
} // Sum
