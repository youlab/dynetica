/**
 * Sin.java
 *
 *
 * Created: Tue Aug 29 15:19:43 2000
 *
 * @author Lingchong You
 * @version 1.0
 */
package dynetica.expression;

public class Sin extends Expression {
    
    public Sin(GeneralExpression a) {
	super(a);
	type = ExpressionConstants.SIN;
    }
    
    public void compute() { value = Math.sin(a.getValue()); }
    public String toString () { return "sin(" + a.toString() + ")"; }
} // Sin
