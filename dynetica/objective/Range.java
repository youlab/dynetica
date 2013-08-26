
package dynetica.objective;

import dynetica.entity.Substance;
import dynetica.util.Numerics;

/**
 *
 * @author Derek Eidum
 */
public class Range extends AbstractMetric {
    
    public Range(Substance s) {
        substance = s;
    }
    
    public double getValue() {
        double[] vals = substance.getValues();
        return Numerics.max(vals) - Numerics.min(vals);
    }
    
    public String toString() {
        return "Range of " + substance.getName();
    }

}
