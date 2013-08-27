/*
 * Abs.java
 *
 * Created on May 1, 2005, 1:00 AM
 */

package dynetica.expression;

/**
 * 
 * @author Lingchong You
 * @version 1.0 computes the absolute value of the input
 */
public class Abs extends Expression {

    /** Creates a new instance of Abs */
    public Abs(GeneralExpression a) {
        super(a);
        type = ExpressionConstants.ABS;
    }

    public void compute() {
        value = Math.abs(a.getValue());
    }

    public String toString() {
        return "abs(" + a.toString() + ")";
    }
}
