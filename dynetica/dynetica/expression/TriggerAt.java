
// 5/3/2006
// Implemented by Lingchong You
//
package dynetica.expression;

/**
 * Usage: triggerAt (defaultvalue, condition, targetvalue) set the value of the
 * function to targetvalue if condition > 0. Can be switched only once. The
 * subsequent switching events are ignored. Useful for recording the time at
 * which a certain event happens.
 * 
 * Need a way to reset the value when repeating a simulation using the same triggerAt function.
 * 
 */

public class TriggerAt extends FunctionExpression {
    public static int parameter_num = 3;
    private boolean switchedON = false;
    public TriggerAt(GeneralExpression[] a) {
        super(a);
        type = ExpressionConstants.TRIGGERAT;
        // value = 0.0;
        value = variables[0].getValue();
    }

    public void compute() {
        double x_2 = variables[2].getValue(); // value 2.
        double x_1 = variables[1].getValue(); // the "condition"
        double x_0 = variables[0].getValue(); // value 1;

        if (!switchedON) {
            if (x_1 > 0) {
                value = x_2;
                switchedON = true;
            }
            else
                value = x_0;
        }
       
    }
//added by LY 6/2016
    //to allow resetting of expression state (useful for some expressions that carry internal states).
    public void reset(){
        value = variables[0].getValue();
        switchedON = false;
    }
    
    
    @Override
    public String toString() {
        return "triggerAt(" + variables[0].toString() + ", "
                + variables[1].toString() + ", " + variables[2].toString()
                + ")";
    }
} // End