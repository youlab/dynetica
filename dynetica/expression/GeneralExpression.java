/**
 * GeneralExpression.java
 *
 *
 * Created: Sat Aug 26 10:53:36 2000
 *
 * @author Lingchong You 
 * @version 0.01
 */
package dynetica.expression;

/**
 * A GeneralExpression includes variables and functions. It is the foundation to
 * implement all kinds of mathematical expressions used in Dynetica.
 */

public interface GeneralExpression {
    public int getType(); // get the type of the expression

    public double getValue();
} // Expression
