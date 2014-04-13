package dynetica.expression;

/**
 * Works similarly as Math.round(a) -- rounding the argument to the nearest
 * integer value (but converted to double in Dynetica). August, 2013
 * 
 * @author lingchong
 */

public class Round extends Expression {

    public Round(GeneralExpression a) {
        super(a);
        type = ExpressionConstants.ROUND;
    }

    public void compute() {
        value = Math.round(a.getValue());
    }

    public String toString() {
        return "round(" + a.toString() + ")";
    }

}
