package dynetica.expression;
/**
 * Implements logical operation, but returns a double value.
 * This is easier to implement but may sacrifice some computation efficiency
 * 2016/6/4
 *  a == b returns 1.0  if the two values are equal, and 0 if otherwise.
 * @author lingchong
 */

public class EQ extends Expression {

    public EQ(GeneralExpression a, GeneralExpression b) {
        super(a, b);
        type = ExpressionConstants.EQ;
    }

    public void compute() {
        if (a.getValue() == b.getValue())
            value = 1.0;
        else
            value = 0.0;
    }

    public String toString() {
        return "(" + a.toString() + " == " + b.toString() + ")";
    }
} // EQ
