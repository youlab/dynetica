package dynetica.objective;

import dynetica.entity.*;

/**
 * 
 * @author Derek Eidum (2013)
 */
public abstract class AbstractObjective {
    AbstractMetric metric;

    abstract public double score();

    public AbstractMetric getMetric() {
        return metric;
    }

    public void setMetric(AbstractMetric m) {
        metric = m;
    }

}
