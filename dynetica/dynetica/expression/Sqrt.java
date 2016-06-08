/**
 * Sqrt.java
 *
 *
 * Created: Tue Aug 29 15:18:00 2000
 *
 * @author Lingchong You
 * @version 0.1
 */
package dynetica.expression;

public class Sqrt extends Expression {

    public Sqrt(GeneralExpression a) {
        super(a);
        type = ExpressionConstants.SQRT;
    }

    public void compute() {
        //
        // L. You 7/28/2013
        // The following code is added to avoid taking square root of negative
        // values.
        //
        double base = a.getValue();
        if (base < 0) {
            base = 0.0;
            System.out
                    .println("Check model: attempting to take square root of negative values");
        }
        value = Math.sqrt(a.getValue());
    }

    public String toString() {
        return "sqrt(" + a.toString() + ")";
    }
} // Sqrt
