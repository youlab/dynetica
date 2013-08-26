
package dynetica.objective;

import dynetica.entity.Substance;
import dynetica.util.Numerics;

/**
 *
 * @author Derek Eidum
 */
public class MaximumRate extends AbstractMetric {
    
    public MaximumRate(Substance s){
        substance = s;
    }
    
    public double getValue() {
        return Numerics.max(substance.getRates());
    }
    
    public String toString() {
        return "Maximum rate of " + substance.getName();
    }

}
