/**
 * Multiply.java
 *
 *
 * Created: Tue Aug 29 13:05:01 2000
 *
 * @author Lingchong You
 * @version 1.0
 */
package dynetica.expression;

public class Multiply extends SimpleOperator {

    public Multiply(GeneralExpression a, GeneralExpression b) {
        super('*', a, b);
        type = ExpressionConstants.MULTIPLY;
    }

    public void compute() {
        value = a.getValue() * b.getValue();
    }

} // Multiply
