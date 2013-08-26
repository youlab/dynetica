/**
 * Divide.java
 *
 *
 * Created: Tue Aug 29 13:06:22 2000
 *
 * @author Lingchong You
 * @version
 */
package dynetica.expression;

public class Divide extends SimpleOperator {
    
    public Divide(GeneralExpression a, GeneralExpression b) {
	super('/', a, b);
	type = ExpressionConstants.DIVIDE;
    }

    public void compute() { value = a.getValue() / b.getValue(); }
    
} // Divide
