package dynetica.objective;

import dynetica.entity.Substance;

/**
 * 
 * @author Derek Eidum (2013)
 */
public abstract class AbstractMetric {

    Substance substance;

    public abstract double getValue();

    public void setSubstance(Substance s) {
        substance = s;
    }

    public Substance getSubstance() {
        return substance;
    }

}
