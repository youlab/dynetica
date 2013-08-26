
package dynetica.expression;
/**
 * Works similarly as Math.ceil(a) -- rounding the argument to the smallest integer value that's greater than a (but converted to double in Dynetica).
 * August, 2013
 * @author lingchong
 */

public class Ceil extends Expression {
    
    public Ceil(GeneralExpression a) {
	super(a);
	type = ExpressionConstants.CEIL;
    }
    
    public void compute() { value = Math.ceil(a.getValue()); }
    
    @Override
    public String toString () { return "ceil(" + a.toString() + ")"; }

} // Cos
