
package dynetica.objective;

import dynetica.entity.Substance;
import dynetica.util.Numerics;

/**
 *
 * @author Derek Eidum
 */
public class MaximumValue extends AbstractMetric {

    public MaximumValue(Substance s) {
        substance = s;
    }
    
    public double getValue() {
        return Numerics.max(substance.getValues());
    }
    
    public String toString() {
        return "Maximum value of " + substance.getName();
    }
}
