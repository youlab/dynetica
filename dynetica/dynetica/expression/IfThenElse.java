/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynetica.expression;

/**
 * This class implements conditional operation:
 * IfThenElse(condition, a, b) computes a if condition is satisfied (>0) and computes b if otherwise.
 * 
 * @author Lingchong You
 */

public class IfThenElse extends FunctionExpression{
    
    public static int parameter_num = 3;

    /** Creates a new instance of Pulses
     * @param a */
    public IfThenElse(GeneralExpression[] a) {
        super(a);
        type = ExpressionConstants.IFTHENELSE;
    }

    @Override
    public void compute() {
        double condition = variables[0].getValue();
        if (condition > 0) 
            value = variables[1].getValue();
        else
            value = variables[2].getValue();
    }

    @Override
    public String toString() {
        return "IfThenElse(" + variables[0].toString() + ", "
                + variables[1].toString() + ", " + variables[2].toString()
                + ")";
    }

} //IfThenElse
