package dynetica.objective;

import dynetica.entity.Substance;
import dynetica.system.ReactiveSystem;

/**
 * 
 * @author Derek Eidum
 */
public class AreaUnderCurve extends AbstractMetric {

    public AreaUnderCurve(Substance s) {
        substance = s;
    }

    public double getValue() {
        double[] vals = substance.getValues();
        double[] times = ((ReactiveSystem) substance.getSystem()).timer
                .getTimePoints();
        double sum = 0;
        for (int i = 0; i < vals.length - 1; i++) {
            double area = (times[i + 1] - times[i]) * 0.5
                    * (vals[i] + vals[i + 1]);
            if (area > 0) {
                sum += area;
            }
        }
        return sum;
    }

    public String toString() {
        return "AUC of " + substance.getName();
    }

}
