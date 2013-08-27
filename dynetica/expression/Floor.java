package dynetica.expression;

/**
 * Works similarly as Math.floor(a) -- rounding the argument to the largest
 * integer value that's smaller than a (but converted to double in Dynetica).
 * August, 2013
 * 
 * @author lingchong
 */

public class Floor extends Expression {

    public Floor(GeneralExpression a) {
        super(a);
        type = ExpressionConstants.FLOOR;
    }

    public void compute() {
        value = Math.floor(a.getValue());
    }

    public String toString() {
        return "floor(" + a.toString() + ")";
    }

}
