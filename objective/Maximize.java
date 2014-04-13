package dynetica.objective;

import dynetica.entity.*;

/**
 * For now, this class just uses the TargetValue scoring algorithm with the user
 * defined substance max value.
 * 
 * @author Derek Eidum
 */
public class Maximize extends AbstractObjective {

    public Maximize(AbstractMetric m) {
        metric = m;
    }

    public double score() {
        double finalVal = metric.getValue();
        if (finalVal < 0) {
            finalVal = 0;
        }
        return 100 * (1 - 1 / (1 + 50 * finalVal));
    }

    @Override
    public String toString() {
        return "Maximizing: " + metric.toString();
    }
}
