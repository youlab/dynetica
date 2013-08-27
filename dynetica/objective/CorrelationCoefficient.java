package dynetica.objective;

import dynetica.entity.Substance;
import dynetica.util.Statistics;

/**
 * 
 * @author Derek Eidum
 */
public class CorrelationCoefficient extends AbstractMetric {
    Substance target;

    public CorrelationCoefficient(Substance sub, Substance tar) {
        substance = sub;
        target = tar;
    }

    public double getValue() {
        double[] subVals = substance.getValues();
        double[] tarVals = target.getValues();
        return Statistics.rSquared(subVals, tarVals);
    }

    public void setTarget(Substance t) {
        target = t;
    }

    public Substance getTarget() {
        return target;
    }

    public String toString() {
        return "Correlation coefficient of " + substance.getName() + " and "
                + target.getName();
    }

}
