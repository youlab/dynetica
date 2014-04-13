package dynetica.expression;

public class Or extends LogicalExpression {

    public Or(LogicalExpression a, LogicalExpression b) {
        super(a, b);
        type = ExpressionConstants.OR;
    }

    public void compute() {
        double tempValue = a.getValue() + b.getValue();
        if (tempValue > 0.5)
            value = 1.0;
        else
            value = 0.0;
    }

    public String toString() {
        return a.toString() + " || " + b.toString();
    }
} // Or
