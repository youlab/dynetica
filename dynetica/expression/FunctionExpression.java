// implemented by Apirak Hoonlar

package dynetica.expression;

/**
 * A FunctionExpression takes one or more GeneralExpressions as arguments. A
 * derived class needs to define compute() properly.
 */

abstract public class FunctionExpression implements GeneralExpression {
    protected GeneralExpression[] variables;
    protected int type = -1;
    protected double value;

    public FunctionExpression() {
        variables = null;
    }

    public FunctionExpression(int keyType, int length) {
        variables = new GeneralExpression[length];
        type = keyType;
    }

    public FunctionExpression(GeneralExpression[] a) {
        variables = a;
    }

    public GeneralExpression[] getVariables() {
        return variables;
    }

    public GeneralExpression getVariables(int index) {
        return variables[index];
    }

    public int getLength() {
        return variables.length;
    }

    public void setA(GeneralExpression a, int index) {
        variables[index] = a;
    }

    public void setB(GeneralExpression[] a) {
        variables = a;
    }

    abstract public void compute(); // this should set the value.

    public double getValue() {
        compute();
        return value;
    }

    public int getType() {
        return type;
    }

    public String toString() {
        int i = 0;
        StringBuffer sb = new StringBuffer(getClass().getName());
        sb.append("( ");
        for (i = 0; i < (variables.length - 1); i++) {
            sb.append(variables[i] + " , ");
        }
        sb.append(variables[i] + " )");
        return sb.toString();
    }

} // Expression
