/**
 * Cos.java
 *
 *
 * Created: Tue Aug 29 15:19:43 2000
 *
 * @author Lingchong You
 * @version 0.1
 */
package dynetica.expression;

public class Cos extends Expression {

    public Cos(GeneralExpression a) {
        super(a);
        type = ExpressionConstants.COS;
    }

    public void compute() {
        value = Math.cos(a.getValue());
    }

    public String toString() {
        return "cos(" + a.toString() + ")";
    }

} // Cos
