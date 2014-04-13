/**
 * Min.java
 *
 *
 * Created: Tue Sep 18 14:00:00 2001
 *
 * @author Apirak Hoonlor
 * @version 0.01
 */

package dynetica.expression;

public class MinL extends FunctionExpression {

    public MinL(GeneralExpression[] a) {
        super(a);
        type = ExpressionConstants.MIN;
    }

    public void compute() {
        int index = 0;
        GeneralExpression tempExp = variables[index];
        double result = tempExp.getValue();
        index++;
        while (index < variables.length) {
            if (variables[index].getValue() < result) {
                result = variables[index].getValue();
            }
            index++;
        }
        value = result;
    }

    //
    // This is added for correct output of min(a,b)
    //
    public String toString() {
        StringBuffer tempString = new StringBuffer("min(");
        int index = 0;
        for (index = 0; index < (variables.length - 1); index++) {
            tempString.append(variables[index].toString());
            tempString.append(", ");
        }
        index = variables.length - 1;
        tempString.append(variables[index].toString());
        tempString.append(")");
        return tempString.toString();
    }
} // MinL
