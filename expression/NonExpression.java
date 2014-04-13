/**
 * NonExpression.java
 *
 *
 * Created: Wed Oct 3, 15:30:00 2001
 *
 * @author Apirak Hoonlor
 * @version 0.01
 */

package dynetica.expression;

import dynetica.entity.*;

abstract public class NonExpression implements GeneralExpression {
    protected int type = -1; // the precedence of an operator should be embodied
                             // here.
    protected double value;

    public NonExpression() {
    }

    abstract public void compute(); // this should set the value.

    public double getValue() {
        compute();
        return value;
    }

    public int getType() {
        return type;
    }

    // public Expression absorb(double f);

    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getName());
        sb.append("()");
        return sb.toString();
    }

} // NonExpression

