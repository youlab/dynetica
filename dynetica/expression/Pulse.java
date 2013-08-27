// usage timeAt (x)
// stores the time at which x becomes positive

// Implemented by Lingchong You 5/3/2006
//
package dynetica.expression;

public class Pulse extends FunctionExpression {
    public static int parameter_num = 3;

    public Pulse(GeneralExpression[] a) {
        super(a);
        type = ExpressionConstants.PULSE;
    }

    public void compute() {
        double x = variables[0].getValue();
        double x_1 = variables[1].getValue();
        double x_2 = variables[2].getValue();
        if ((x >= x_1) && (x < x_2))
            value = 1;
        else
            value = 0;
    }

    @Override
    public String toString() {
        return "pulse(" + variables[0].toString() + ", "
                + variables[1].toString() + ", " + variables[2].toString()
                + ")";
    }
} // End