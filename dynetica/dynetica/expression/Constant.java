/**
 * Constant.java
 *
 *
 * Created: Sun Aug 27 17:08:59 2000
 *
 * @author Lingchong You
 * @version 0.01
 */
package dynetica.expression;

public class Constant implements dynetica.expression.GeneralExpression {
    double value;

    public Constant(double value) {
        this.value = value;
    }

    public int getType() {
        return Integer.MAX_VALUE;
    }

    /**
     * Get the value of value.
     * 
     * @return Value of value.
     */
    public double getValue() {
        return value;
    }
    //added by LY 6/2016
    //to allow resetting of expression state (useful for some expressions that carry internal states).
    @Override
    public void reset(){
    }
    
    /**
     * Set the value of value.
     * 
     * @param v
     *            Value to assign to value.
     */
    public String toString() {
        return String.valueOf(getValue());
    }
}
