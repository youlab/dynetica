/**
 * Pow.java
 *
 *
 * Created: Tue Aug 29 13:09:43 2000
 *
 * @author Lingchong You
 * @version
 */
package dynetica.expression;

public class Pow extends SimpleOperator {

    public Pow(GeneralExpression a, GeneralExpression b) {
        super('^', a, b);
        type = ExpressionConstants.POW;
    }

    @Override
    public void compute() {
        //
        // L. You 7/28/2013: The following code is implemented to avoid taking
        // the power of a negative value
        // This is because the exponent can be a non-integer.
        //
        double base = Math.max(0.0, a.getValue());
        //
        //
        value = Math.pow(base, b.getValue());
    }

} // Pow
