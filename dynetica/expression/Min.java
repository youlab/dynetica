
/**
 * Min.java
 *
 *
 * Created: Tue Aug 29 13:33:54 2000
 *
 * @author Lingchong You
 * @version 0.01
 */

package dynetica.expression;

public class Min extends Expression {
    
    public Min(GeneralExpression a, GeneralExpression b) {
	super(a,b);
	type = ExpressionConstants.MIN;
    }

    public void compute() { value = Math.min(a.getValue(), b.getValue()); }
    
    //
    // This is added for correct output of min(a,b)
    //
    public String toString () { return "min(" + a.toString() + ", " + b.toString() + ")"; }
} // Min
