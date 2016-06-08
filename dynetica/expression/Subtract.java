/**
 * Subtract.java
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
 * 
 * @author lingchong
 */

public class Subtract extends SimpleOperator {
    public Subtract(GeneralExpression a, GeneralExpression b) {
        super('-', a, b);
        type = ExpressionConstants.SUBTRACT;
    }

    @Override
    public void compute() {
        value = a.getValue() - b.getValue();
    }

} // Subtract
