/*
 // Implemented by Lingchong You 1/13/2013
 */
package dynetica.expression;

/**
 * This class implements the Hill equation: hill(c, nH, KH) = c ^ nH/ (KH^nH + c^nH).
 * 
 * @author Lingchong You
 */
public class Hill extends FunctionExpression {
     public static int parameter_num = 3;
   
    /** Creates a new instance of Pulses */
    public Hill(GeneralExpression[] a) {
	super(a);
	type = ExpressionConstants.HILL;
    }
    
   public void compute() {
       /* c is the input concentration */
        double c = variables [0].getValue();
        /* nH is the Hill Coefficient
        */
	double nH = variables[1].getValue();
        /* KH is the half-activation concentration
         * 
         */
	double KH = variables[2].getValue();
        value =  Math.pow(c, nH) / (Math.pow(KH,nH) + Math.pow(c, nH));
    }
   
     @Override
  public String toString () { return "hill(" + variables[0].toString() 
					+ ", " + variables[1].toString() + 
					", " + variables[2].toString() + ")"; }
   
}
