package dynetica.objective;

import dynetica.entity.*;

/**
 * @author Derek Eidum (2013)
 */
public class TargetValue extends AbstractObjective {
    double targetValue;
    
    public TargetValue(AbstractMetric m, double t) {
        metric = m;
        targetValue = t;
    }
    
    public double score() {
        double value = metric.getValue();
        double error = Math.abs(targetValue - value);
        if (targetValue == 0) {return 100 / (1 + error);}
        double relError = error / targetValue;
        return 100 / (1 + relError);
    }
    
    @Override
    public String toString() {
        return metric.toString() + " targeting to value " + targetValue;
    }
    
}
