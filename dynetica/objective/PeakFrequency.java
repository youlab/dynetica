
package dynetica.objective;

import dynetica.entity.Substance;
import org.apache.commons.math3.transform.*;

/**
 *
 * @author Derek Eidum
 */
public class PeakFrequency extends AbstractMetric {
    
    public PeakFrequency(Substance s) {
        substance = s;
    }
    
    public double getValue() {
        double[] vals = substance.getValues();
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        fft.transform(vals, TransformType.FORWARD);
        return 0;
    }

}
