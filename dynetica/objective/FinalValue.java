
package dynetica.objective;

import dynetica.entity.Substance;

/**
 *
 * @author Derek Eidum
 */
public class FinalValue extends AbstractMetric {
    public FinalValue(Substance s) {
        substance = s;
    }
    public double getValue() {
        return substance.getValue(substance.getValues().length - 1);
    }
    
    public String toString() {
        return "Final value of " + substance.getName();
    }
}
