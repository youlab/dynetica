/**
 * Max.java
 *
 *
 * Created: Tue Aug 29 13:24:26 2000
 *
 * @author Lingchong You
 * @version 0.01
 */
package dynetica.expression;

public class Max extends Expression {

    public Max(GeneralExpression a, GeneralExpression b) {
        super(a, b);
        type = ExpressionConstants.MAX;
    }

    public void compute() {
        value = Math.max(a.getValue(), b.getValue());
    }

    public String toString() {
        return "max(" + a.toString() + ", " + b.toString() + ")";
    }
} // Max
