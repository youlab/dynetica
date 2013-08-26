/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dynetica.objective;

import dynetica.entity.*;

/**
 *
 * @author Derek Eidum
 */
public class Minimize extends AbstractObjective {
    
    public Minimize(AbstractMetric m) {
        metric = m;
    }
    
    public double score() {
        double finalVal = metric.getValue();
        return 100 / (1 + finalVal);
    }
    
    @Override
    public String toString() {
        return "Minimizing: " + metric.toString();
    }

}
