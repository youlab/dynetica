package dynetica.expression;
/**
 * Implements logical operation, but returns a double value.
 * This is easier to implement but may sacrifice some computation efficiency
 * 2016/6/4
 *  or(a,b) returns 1.0 if either are positive and 0 otherwise.
 * @author lingchong
 */

public class Or extends Expression {
    public Or(GeneralExpression a, GeneralExpression b) {
        super(a, b);
        type = ExpressionConstants.OR;
    }

    public void compute() {
        if (a.getValue() > 0 || b.getValue() > 0)
            value = 1.0;
        else
            value = 0.0;
    }

    public String toString() {
        return "(" + a.toString() + " || " + b.toString() + ")";
    }
} // Or
