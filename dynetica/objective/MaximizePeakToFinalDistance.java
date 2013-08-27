package dynetica.objective;

import dynetica.util.Numerics;

/**
 * 
 * @author Derek Eidum
 */
public class MaximizePeakToFinalDistance extends AbstractDoseObjective {

    public double score() {
        double range = Numerics.max(response) - response[response.length - 1];
        return 100 * (1 - 1 / (1 + 0.5 * Math.abs(range)));
    }

    public String toString() {
        return "Mazimizing peak to final distance";
    }
}
