package dynetica.expression;

public class LT extends LogicalExpression {

    public LT(GeneralExpression a, GeneralExpression b) {
        super(a, b);
        type = ExpressionConstants.LT;
    }

    public void compute() {
        if (a.getValue() < b.getValue())
            value = 1.0;
        else
            value = 0.0;
    }

    public String toString() {
        return a.toString() + " < " + b.toString();
    }
} // LT
