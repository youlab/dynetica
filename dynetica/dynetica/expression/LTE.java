package dynetica.expression;

/**
 * Implements logical operation, but returns a double value.
 * This is easier to implement but may sacrifice some computation efficiency
 * 2016/6/4
 *   returns 1.0  a <= b, 0 otherwise.
 * @author lingchong
 */

public class LTE extends Expression {

    public LTE(GeneralExpression a, GeneralExpression b) {
        super(a, b);
        type = ExpressionConstants.LTE;
    }

    public void compute() {
        if (a.getValue() > b.getValue())
            value = 1.0;
        else
            value = 0.0;
    }

    public String toString() {
        return "(" + a.toString() + " <= " + b.toString() + ")";
    }
} // LTE