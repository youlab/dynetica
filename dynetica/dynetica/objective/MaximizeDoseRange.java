package dynetica.objective;

import dynetica.util.Numerics;

/**
 * 
 * @author Derek Eidum
 */
public class MaximizeDoseRange extends AbstractDoseObjective {

    public MaximizeDoseRange() {
    }

    public MaximizeDoseRange(double[] x) {
        dose = x;
    }

    public MaximizeDoseRange(double[] x, double[] y) {
        dose = x;
        response = y;
    }

    public double score() {
        double range = Numerics.max(response) - Numerics.min(response);
        return 100 * (1 - 1 / (1 + 0.1 * range));
    }

    @Override
    public String toString() {
        return "Maximizing range";
    }

}
