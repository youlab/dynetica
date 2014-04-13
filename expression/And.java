package dynetica.expression;

public class And extends LogicalExpression {

    public And(LogicalExpression a, LogicalExpression b) {
        super(a, b);
        type = ExpressionConstants.AND;
    }

    public void compute() {
        double tempValue = a.getValue() + b.getValue();
        if (tempValue < 1.5)
            value = 0.0;
        else
            value = 1.0;
    }

    public String toString() {
        return a.toString() + " && " + b.toString();
    }
} // And
