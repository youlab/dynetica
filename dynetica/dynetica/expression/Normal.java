/*
 * Normal.java
 *
 * Created on April 30, 2005, 8:54 PM
 */

package dynetica.expression;

/**
 * 
 * @author Lingchong You
 * @version 1.0
 * 
 *          This class generates a random number following the Normal (Gaussian)
 *          distribution, with a mean of 0 and standard deviation of 1.0.
 */

public class Normal extends NonExpression {
    private static int count = 0;
    private static double value2;

    /** Creates a new instance of Normal */
    public Normal() {
        super();
        reset();
        type = ExpressionConstants.NORMAL;
    }

    public void reset() {
        value = (new java.util.Random()).nextGaussian();
    }

    /**
     * every time this is executed, the class will generate the same number
     * unless it has been reset
     */
    public void compute() {
        value = ExpressionConstants.doubleNumber.nextGaussian();
    }

    public String toString() {
        return ("normal()");
    }
}
