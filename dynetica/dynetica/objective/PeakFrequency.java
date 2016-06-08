package dynetica.objective;

import dynetica.entity.Substance;
import dynetica.system.ReactiveSystem;
import org.apache.commons.math3.transform.*;
import org.apache.commons.math3.complex.*;

/**
 * 
 * @author Derek Eidum
 */
public class PeakFrequency extends AbstractMetric {
    static boolean DEBUG = false;

    public PeakFrequency(Substance s) {
        substance = s;
    }

    public double getValue() {
        double[] vals = substance.getValues();
        double[] times = ((ReactiveSystem) substance.getSystem()).timer.getTimePoints();
        double dt = times[1] - times[0];
        
        
        // Make sure length is a power of two.  If not, zero pad
        if (!(isPowerOfTwo(vals.length))){
            int n = findNextPowerTwo(vals.length);
            double[] newVals = new double[n];
            for (int i = 0; i < vals.length; i++) {
                newVals[i] = vals[i];
            }
            vals = newVals;
        }
        
        // Calculate FFT, find magnitude for each point
        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] result = fft.transform(vals, TransformType.FORWARD);
        double[] mag = new double[result.length];
        for (int n = 0; n < result.length; n++) {
            Complex c = result[n];
            double r = c.getReal();
            double i = c.getImaginary();
            mag[n] = Math.sqrt(r*r + i*i);
        }
        debugArray(mag);
        
        // Calculate the corresponding frequencies
        double Fs = 1/dt; // sample rate
        double[] freqs = new double[vals.length/2];
        for (int i = 0; i < freqs.length; i++) {
            freqs[i] = i*Fs/vals.length;
        }
        debugArray(freqs);
        
        // Calculate the index of the max of mag and find the corresponding frequency
        
        int maxIdx=1;
        double maxMag = mag[1];
        for (int i = 2; i < freqs.length; i++) {
            if (mag[i] > maxMag) {
                maxMag = mag[i];
                maxIdx = i;
            }
        }
        
        return freqs[maxIdx];
    }
    
    public boolean isPowerOfTwo(int i) {
        return (i & -i) == i;
    }
    
    public int findNextPowerTwo(int i) {
        int result = 1;
        while (result < i) {
            result = result << 1;
        }
        return result;
    }
    public static void debugArray(double[] d) {
        if (!DEBUG) {return;}
        System.out.print("[");
        for (double ob : d) {
            System.out.print(ob + ", ");
        }
        System.out.println("]");
    }
    @Override
    public String toString() {
        return "Peak frequency of " + substance.getName();
    }

}
