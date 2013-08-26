/**
 * Exp.java
 *
 *
 * Created: Tue Aug 29 15:18:00 2000
 *
 * @author Lingchong You
 * @version 0.1
 */
package dynetica.expression;

public class Exp extends Expression {
    
    public Exp(GeneralExpression a) {
	super(a);
	type = ExpressionConstants.EXP;
    }
    
    public void compute() { value = Math.exp(a.getValue()); }
    public String toString () { return "exp(" + a.toString() +  ")"; }
} // Exp
