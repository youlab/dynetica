package dynetica.expression;

public class LTE extends LogicalExpression {

    public LTE(GeneralExpression a, GeneralExpression b) {
        super(a, b);
        type = ExpressionConstants.LTE;
    }

    public void compute() {
        if (a.getValue() <= b.getValue())
            value = 1.0;
        else
            value = 0.0;
    }

    public String toString() {
        return a.toString() + " <= " + b.toString();
    }
} // LTE
