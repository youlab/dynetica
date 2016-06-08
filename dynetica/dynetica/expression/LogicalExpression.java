/**
 * LogicalExpression.java
 *
 *
 * Created: Tue Aug 29 12:34:01 2000
 *
 * @author Lingchong You
 * @version 0.01
 */

package dynetica.expression;

abstract public class LogicalExpression implements GeneralExpression {
    protected GeneralExpression a;
    protected GeneralExpression b;
    protected int type = -1; // the precedence of an operator should be embodied
                             // here.
    protected double value;

    public LogicalExpression() {
        this(null, null);
    }

    public LogicalExpression(GeneralExpression a) {
        this(a, null);
    }

    public LogicalExpression(GeneralExpression a, GeneralExpression b) {
        this.a = a;
        this.b = b;
    }

    public GeneralExpression getA() {
        return a;
    }

    public GeneralExpression getB() {
        return b;
    }

    public void setA(GeneralExpression a) {
        this.a = a;
    }

    public void setB(GeneralExpression b) {
        this.b = b;
    }

    // public boolean isBinary() { return (a != null && b != null); }
    // public boolean isUnary() { return (a != null && b == null); }

    abstract public void compute(); // this should set the value.

    //added by LY 6/2016
    //to allow resetting of expression state (useful for some expressions that carry internal states).
    public void reset(){
    }
    
    public double getValue() {
        compute();
        return value;
    }

    public int getType() {
        return type;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer(getClass().getName());
        if (a != null)
            sb.append(a);
        if (b != null)
            sb.append("," + b);
        return sb.toString();
    }

} // LogicalExpression

