//
// Greater than
//

package dynetica.expression;

public class GT extends LogicalExpression {

    public GT(GeneralExpression a, GeneralExpression b) {
        super(a, b);
        type = ExpressionConstants.GT;
    }

    public void compute() {
        if (a.getValue() > b.getValue())
            value = 1.0;
        else
            value = 0.0;
    }

    public String toString() {
        return a.toString() + " > " + b.toString();
    }
} // GT
