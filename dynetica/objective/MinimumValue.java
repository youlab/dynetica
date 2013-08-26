
package dynetica.objective;

import dynetica.entity.Substance;
import dynetica.util.Numerics;

/**
 *
 * @author Derek Eidum
 */
public class MinimumValue extends AbstractMetric {
    
    public MinimumValue(Substance s) {
        substance = s;
    }
    
    public double getValue() {
        return Numerics.min(substance.getValues());
    }
    
    public String toString() {
        return "Minimum value of " + substance.getName();
    }
}
