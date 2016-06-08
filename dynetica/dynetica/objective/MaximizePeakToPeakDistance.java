package dynetica.objective;

/**
 * 
 * @author Derek Eidum (2013)
 */
public class MaximizePeakToPeakDistance extends AbstractDoseObjective {

    public double score() {
        double[] slopes = new double[response.length - 1];
        for (int i = 0; i < slopes.length; i++) {
            slopes[i] = (response[i + 1] - response[1])
                    / (dose[i + 1] - dose[i]);
        }

        Double[] extrema = { null, null };
        int i = 0;
        for (int j = 0; j < slopes.length - 1; j++) {
            if ((slopes[j] > 0) ^ (slopes[j + 1] > 0)) {
                extrema[i] = response[j + 1];
                i++;
            }
            if (i > 1) {
                break;
            }
        }
        // If two peaks weren't found
        if (extrema[1] == null) {
            return Double.MIN_NORMAL;
        }

        double range = Math.abs(extrema[1] - extrema[0]);
        return 100 * (1 - 1 / (1 + 0.5 * range));
    }

    @Override
    public String toString() {
        return "Maximizing Peak to Peak Distance";
    }

}
