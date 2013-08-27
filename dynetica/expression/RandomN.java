/** Generates a random number in (0,1) following the uniform distribution
 *
 */
package dynetica.expression;

public class RandomN extends NonExpression {

    public RandomN() {
        super();
        type = ExpressionConstants.RANDOMN;
    }

    public void compute() {
        value = ExpressionConstants.doubleNumber.nextDouble();
    }

    public String toString() {
        return "rand()";
    }
} // End