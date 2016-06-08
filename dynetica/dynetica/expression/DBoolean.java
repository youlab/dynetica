/**
 * DBoolean.java
 *
 *
 * Created: Sun Aug 27 17:08:59 2000
 *
 * @author Lingchong You
 * @version 0.01
 */
package dynetica.expression;

public class DBoolean implements GeneralExpression {
    boolean value;

    public DBoolean(boolean value) {
        this.value = value;
    }

    public int getType() {
        return Integer.MAX_VALUE;
    }

    public double getValue() {
        if (value)
            return 1.0;
        return 0.0;
    }

    //added by LY 6/2016
    //to allow resetting of expression state (useful for some expressions that carry internal states).
    public void reset(){
    }
    
    public String toString() {
        if (value)
            return "true";
        else
            return "false";
    }
}
