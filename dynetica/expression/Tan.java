/**
 * Tan.java
 *
 *
 * Created: Tue Aug 29 15:18:00 2000
 *
 * @author Lingchong You
 * @version 0.1
 */
package dynetica.expression;

public class Tan extends Expression {
    
    public Tan(GeneralExpression a) {
	super(a);
	type = ExpressionConstants.TAN;
    }
    
    public void compute() { value = Math.tan(a.getValue()); }
    
    @Override
    public String toString () { return "tan(" + a.toString()  + ")"; }
} // Tan
