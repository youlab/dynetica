


package dynetica.expression;
/**
 * Implements logical operation, but returns a double value.
 * This is easier to implement but may sacrifice some computation efficiency
 * 2016/6/4
 *  a && b returns 1.0 if both are positive and 0 otherwise.
 * @author lingchong
 */

public class And extends Expression {

    public And(GeneralExpression a, GeneralExpression b) {
        super(a, b);
        type = ExpressionConstants.AND;
    }

    public void compute() {
        if (a.getValue() > 0 && b.getValue() > 0)
            value = 1.0;
        else
            value = 0.0;
    }

    public String toString() {
//        return("and(" + a.toString() + ", " + b.toString() + ")");
       return "(" + a.toString() + " && " + b.toString()+ ")";
    }
} // And
