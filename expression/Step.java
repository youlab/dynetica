package dynetica.expression;

/**
 * Usage: step(a,b). This function returns 1 if a > b and 0 if otherwise.
 * 
 * @author lingchong
 * 
 */
public class Step extends Expression {

    public Step(GeneralExpression a, GeneralExpression b) {
        super(a, b);
        type = ExpressionConstants.STEP;
    }

    public void compute() {
        double xx = a.getValue();
        double yy = b.getValue();
        if (xx > yy)
            value = 1;
        else
            value = 0;
    }

    @Override
    public String toString() {
        return "step(" + a.toString() + ", " + b.toString() + ")";
    }
}
