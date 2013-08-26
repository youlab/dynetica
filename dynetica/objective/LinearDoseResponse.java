
package dynetica.objective;

import dynetica.util.Statistics;

/**
 *
 * @author Derek Eidum
 */
public class LinearDoseResponse extends AbstractDoseObjective {
    
    public LinearDoseResponse(){}
    public LinearDoseResponse(double[] x) {dose = x;}
    public LinearDoseResponse(double[] x, double[] y) {dose = x; response = y;}
    
    public double score() {
        double[] regression = Statistics.linearRegression(dose, response);
        double rSquared = regression[2];
        if (rSquared <= 0) {return Double.MIN_VALUE;}
        return 100*rSquared;
    }

    @Override
    public String toString() {
        return "Maximally linear";
    }
}
