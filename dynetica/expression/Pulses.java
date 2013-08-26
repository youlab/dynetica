/*
 * Pulses.java
 *
 * Created on April 30, 2005, 10:48 PM
 */

package dynetica.expression;

/**
 *
 * @author Lingchong You
 * @version 1.0
 * Generates a continuous sequence of square pulses.
 * Usage: pulses(T, T0, T1, T2)
 *  T should be the system Timer.
 *  T0 is the time point where the squence is initiated
 *  T1 is the period of the sequence
 *  T2 < T1 is duration of the pulse.
 *
 * returns 1 if 
 *  T0 + n*T1 <= T < T0 + n*T1 + T2 (n is an integer)
 * returns 0 otherwise
 */
public class Pulses extends FunctionExpression {
     public static int parameter_num = 4;
   
    /** Creates a new instance of Pulses */
    public Pulses(GeneralExpression[] a) {
	super(a);
	type = ExpressionConstants.PULSES;
    }
    
   public void compute() {
	double T = variables[0].getValue();
	double T0 = variables[1].getValue();
	double T1 = variables[2].getValue();
        double T2 = variables[3].getValue();
        double n = Math.floor((T - T0)/T1);
	if((T >= n*T1 + T0 ) && (T < T0 + n* T1 + T2))  
            value =  1.0;
	else        
            value =  0.0;
    }
   
  public String toString () { return "pulses(" + variables[0].toString() 
					+ ", " + variables[1].toString() + 
					", " + variables[2].toString() + 
                                        ", " + variables[3].toString() + ")"; }
   
}
