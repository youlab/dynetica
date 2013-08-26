
package dynetica.objective;

import dynetica.entity.Substance;
import dynetica.system.ReactiveSystem;

/**
 *
 * @author Derek Eidum
 */
public class TimeToSteadyState extends AbstractMetric {
    
    double fractionReached;
    
    public TimeToSteadyState(Substance s, double frac) {
        substance = s;
        fractionReached = frac;
    }
    
    // Gets the time at which the value reaches the specified fraction of steady state
    public double getValue() {
        double[] times = ((ReactiveSystem) substance.getSystem()).timer.getTimePoints();
        double[] vals = substance.getValues();
        double initialVal = vals[0];
        double finalVal = vals[vals.length-1];
        double threshold = fractionReached * (finalVal - initialVal);
        
        for (int i = 0; i < vals.length; i++) {
            vals[i] -= (threshold + initialVal);
        }
        for (int i = times.length - 1; i > 0; i--) {
            boolean a = (vals[i] > 0);
            boolean b = (vals[i-1] > 0);
            if (a ^ b){
                return times[i];
            }
        }
        return 0;
    }
    
    public String toString() {
        return "Time for " + substance.getName() + " to reach " + fractionReached + " of steady state";
    }

}
