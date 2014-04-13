/**
 * Log.java
 *
 *
 * Created: Tue Aug 29 13:19:54 2000
 *
 * @author Lingchong You
 * @version 0.01
 */
package dynetica.expression;

public class Log extends Expression {

    public Log(GeneralExpression a) {
        super(a);
        type = ExpressionConstants.LOG;
    }

    public void compute() {
        value = Math.log(a.getValue());
    }

    public String toString() {
        return "log(" + a.toString() + ")";
    }
} // Log
