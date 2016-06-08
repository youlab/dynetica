package dynetica.objective;

import dynetica.util.Numerics;

/**
 * 
 * @author Derek Eidum
 */
public class MinimizeDoseRange extends AbstractDoseObjective {
    public MinimizeDoseRange() {
    }

    public MinimizeDoseRange(double[] x) {
        dose = x;
    }

    public MinimizeDoseRange(double[] x, double[] y) {
        dose = x;
        response = y;
    }

    public double score() {
        double range = Numerics.max(response) - Numerics.min(response);
        return 100 / (1 + range);
    }

    public String toString() {
        return "Minimizing range";
    }
}
