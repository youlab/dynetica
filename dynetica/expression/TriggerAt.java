

// 5/3/2006
// Implemented by Lingchong You
//
package dynetica.expression;
/**
 * Usage: triggerAt (defaultvalue, condition, targetvalue)
 * set the value of the function to targetvalue if condition > 0. Can be switched only once. The subsequent 
 * switching events are ignored. Useful for recording the time at which a certain event happens.
 * 
 */

public class TriggerAt extends FunctionExpression {
    public static int parameter_num = 3;
//    private boolean switchON = false;
    double  previousCondition = 1.0; // this is used to store the previous point as reference. 
    
    double previous_x2 = - Double.MAX_VALUE;
//    private boolean switchOFF = false;
    
    public TriggerAt(GeneralExpression[] a) {
	super(a);
	type = ExpressionConstants.TRIGGERAT;
        //value = 0.0;
        value = variables[0].getValue();
    }
    
    public void compute() {  
        double x_2 = variables[2].getValue(); // value 2.
	double x_1 = variables[1].getValue(); // the "condition"
        double x_0 = variables[0].getValue(); // value 1;
        
        if (previous_x2  < x_2) previous_x2 = x_2;
        
        if (x_2 < previous_x2) {// reset the value
            value = x_0;
            previousCondition = 1.0;
            previous_x2 = - Double.MAX_VALUE;
            return;
        }
              
        if(x_1 >= 0.0 && previousCondition < 0.0)  {
            value =  x_2;
        }
        
        previousCondition = x_1;
    }
    
    @Override
  public String toString () { return "triggerAt(" + variables[0].toString() 
					+ ", " + variables[1].toString() 
                                        + ", " + variables[2].toString() 
                                        + ")"; }
} // End