
/**
 * Substract.java
 *
 *
 * Created: Tue Aug 29 13:01:25 2000
 *
 * @author Lingchong You
 * @version 1.0
 */
package dynetica.expression;
/**
 * Implements subtraction. Usage a - b.
 * @author lingchong
 */

public class Substract extends SimpleOperator {
    public Substract(GeneralExpression a, GeneralExpression b) {
	super('-', a, b);
	type = ExpressionConstants.SUBSTRACT;
    }

    @Override
    public void compute() { value = a.getValue() - b.getValue();}
    
} // Substract
